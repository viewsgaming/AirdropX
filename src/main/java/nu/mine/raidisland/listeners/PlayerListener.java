package nu.mine.raidisland.listeners;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.menus.AutoDropSettingsMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.settings.Lang;

@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler
	public void onChestOpen(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if (block != null && block.getType() == Material.CHEST) {
			Chest chest = (Chest) event.getClickedBlock().getState();
			if (CompMetadata.hasMetadata(chest , "Opened")) return;
			if (CompMetadata.hasMetadata(chest, Airdrop.NBT_TAG) && !player.hasPermission("AirdropX.bypass.delay")) {

				if (PlayerCache.canAddCooldown(event.getPlayer())) {
					PlayerCache.addCooldown(event.getPlayer());
				} else {
					event.setCancelled(true);
				}

			}
		}

	}


	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getType().equals(InventoryType.CHEST)) {
			if (!event.getView().getTitle().equals("Chest")) return;
			try {
				Chest chest = (Chest) event.getInventory().getLocation().getBlock().getState();
				if (CompMetadata.hasMetadata(chest , "Opened")) return;

				boolean hasMetadata = CompMetadata.hasMetadata(chest, Airdrop.NBT_TAG);
				boolean isExecute = CompMetadata.hasMetadata(chest, "isExecute");
				String name = CompMetadata.getMetadata(chest , Airdrop.NBT_TAG);

				if (hasMetadata && event.getInventory().isEmpty()) {

					try {
						Airdrop.getChestFromActiveTask(chest).cancel();
						Airdrop.removeActiveTask(chest);
					} catch (NullPointerException ex) {
						Messenger.warn(event.getPlayer() , "Seem like this airdrop isn't up to date.");
					}

					chest.getLocation().getBlock().setType(Material.AIR);

					String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Player_Took_Airdrop") , "prefix" , Core.PREFIX , "player" , event.getPlayer().getName() , "airdrop_name" , name);

					Airdrop airdrop = Airdrop.findAirdrop(name);

					if (!isExecute)
						if (RandomUtil.chance(airdrop.getChanceToExecuteCommand()))
							Common.dispatchCommand(event.getPlayer() , airdrop.getCommandToExecute());

					Messenger.broadcastAnnounce(broadcast);
					CompMetadata.setMetadata(chest, "Opened" , event.getPlayer().getName());

				} else if (hasMetadata && !event.getInventory().isEmpty() && !isExecute) {

					Airdrop airdrop = Airdrop.findAirdrop(name);

					if (RandomUtil.chance(airdrop.getChanceToExecuteCommand()))
						Common.dispatchCommand(event.getPlayer() ,airdrop.getCommandToExecute());

					CompMetadata.setMetadata(chest, "isExecute" , "true");
				}


			} catch (ClassCastException ex) {
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() != Material.CHEST) return;
		Chest chest = (Chest) event.getBlock().getState();
		Player player = event.getPlayer();

		if (CompMetadata.hasMetadata(chest , Airdrop.NBT_TAG) && !player.isOp()) {
			event.setCancelled(true);
		} else if (CompMetadata.hasMetadata(chest , Airdrop.NBT_TAG) && player.isOp()) {
			try {
				Airdrop.getChestFromActiveTask(chest).cancel();
			} catch (NullPointerException ignored) {
			}

			Common.tellNoPrefix(player , Core.PREFIX + " &fYou have broke the box of airdrop.");
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		PlayerCache cache = PlayerCache.from(event.getPlayer());
		if (!cache.isDoingSetting()) return;
		if (event.getClickedBlock() == null) return;
		Airdrop airdrop = cache.getSelectedAirdrop();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.setCancelled(true);

			airdrop.setCenter(event.getClickedBlock().getLocation());

			Messenger.success(event.getPlayer(), "You've set center location to " + Common.shortLocation(airdrop.getCenter()));

			cache.setDoingSetting(false);

			new AutoDropSettingsMenu(cache.getSelectedAirdrop()).displayTo(event.getPlayer());
		}
	}
}
