package nu.mine.raidisland.listeners;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.menus.AutoDropSettingsMenu;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.checkerframework.checker.units.qual.A;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.settings.Lang;

@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getType().equals(InventoryType.CHEST)) {
			if (!event.getView().getTitle().equals("Chest")) return;
			try {
				Chest chest = (Chest) event.getInventory().getLocation().getBlock().getState();
				if (CompMetadata.hasMetadata(chest , "Opened")) return;

				if (CompMetadata.hasMetadata(chest, Airdrop.NBT_TAG) && event.getInventory().isEmpty()) {

					try {
						Airdrop.getChestFromActiveTask(chest).cancel();
						Airdrop.removeActiveTask(chest);
					} catch (NullPointerException ex) {
						Messenger.warn(event.getPlayer() , "Seem like this airdrop isn't up to date.");
					}

					chest.getLocation().getBlock().setType(Material.AIR);
					String name = CompMetadata.getMetadata(chest , Airdrop.NBT_TAG);
					String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Player_Took_Airdrop") , "prefix" , Core.PREFIX , "player" , event.getPlayer().getName() , "airdrop_name" , name);

					String airdropName = CompMetadata.getMetadata(chest , Airdrop.NBT_TAG);
					Airdrop airdrop = Airdrop.findAirdrop(airdropName);

					Common.dispatchCommand(event.getPlayer() ,airdrop.getCommandToExecute());
					Messenger.broadcastAnnounce(broadcast);
					CompMetadata.setMetadata(chest, "Opened" , event.getPlayer().getName());
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() != Material.CHEST) return;
		Chest chest = (Chest) event.getBlock().getState();

		if (CompMetadata.hasMetadata(chest , Airdrop.NBT_TAG) && !event.getPlayer().isOp()) {
			event.setCancelled(true);
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
