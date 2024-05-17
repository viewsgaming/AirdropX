package nu.mine.raidisland.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.menus.AutoDropSettingsMenu;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.CompassMeta;
import org.mineacademy.fo.*;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.settings.Lang;

import java.util.List;


@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChestOpen(PlayerInteractEvent event) {

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.SPECTATOR)
			return;

		PlayerCache cache = PlayerCache.from(player);

		if (block == null) return;

		if (block != null && block.getType() == Material.CHEST) {
			Chest chest = (Chest) event.getClickedBlock().getState();

			if (!CompMetadata.hasMetadata(chest , Airdrop.NBT_TAG)) return;

			String name = CompMetadata.getMetadata(chest , Airdrop.NBT_TAG);
			Airdrop airdrop = Airdrop.findAirdrop(name);

			if (CompMetadata.hasMetadata(chest , "Opened")) return;


			String airdropPreparing = Replacer.replaceArray(Lang.of("Airdrop_Preparing_To_Open") , "airdrop_name" , airdrop.getName() ,
					"prefix" , Core.PREFIX ,
					"time" , airdrop.getOpeningDelayTime() ,
					"world" , chest.getLocation().getWorld().getName() ,
					"x" , chest.getLocation().getBlockX() ,
					"y", chest.getLocation().getBlockY() ,
					"z", chest.getLocation().getBlockZ()
			);


			if (CompMetadata.getMetadata(chest , "CAN_OPEN").equals("No") && !player.hasPermission("airdropx.bypass.opening_delay")) {
				if (Airdrop.getChestFromActiveTask(chest) != null) {
					Airdrop.getChestFromActiveTask(chest).cancel();
					Airdrop.removeActiveTask(chest);
					AirdropUtil.startOpeningDelay(chest.getLocation() , airdrop , chest);
					Common.broadcast(airdropPreparing);
				}
				Common.tellNoPrefix(player , Lang.of("Airdrop_In_Delay"));
				event.setCancelled(true);
				return;

			}

			if (Settings.RegionPrevention.ENABLED) {
				if (CompMetadata.getMetadata(chest, "CAN_OPEN").equals("Yes")) {

					if (HookManager.isWorldGuardLoaded()) {

						List<String> originalRegion =  HookManager.getRegions(chest.getLocation());

						if (!originalRegion.isEmpty()) {

							for (String regionName : Settings.RegionPrevention.BYPASS_REGIONS) {
								if (!regionName.isEmpty()) {
									if (originalRegion.contains(regionName))
										return;
									else {
										PlayerUtil.addItemsOrDrop(player , chest.getBlockInventory().getContents());
										AirdropUtil.deleteChest(chest);
										return;
									}
								} else {
									break;
								}
							}

							PlayerUtil.addItemsOrDrop(player , chest.getBlockInventory().getContents());
							AirdropUtil.deleteChest(chest);
						}

					}
				}
			}



			if (CompMetadata.hasMetadata(chest, Airdrop.NBT_TAG) && !player.hasPermission("AirdropX.bypass.delay")) {

				if (cache.canAddCooldown())
					cache.addCooldown();
				else
					event.setCancelled(true);

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

					String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Player_Took_Airdrop") , "prefix" , Core.PREFIX , "player" , event.getPlayer().getName() , "airdrop_name" , name);
					Common.broadcast(broadcast);

					for (int cord = -1; cord <= 1; cord++) {
						for (Entity ent : chest.getWorld().getNearbyEntities(chest.getLocation(),cord,cord,cord)) {
							if (ent instanceof ArmorStand) {
								ent.remove();
							}
						}
					}


					AirdropUtil.deleteChest(chest);

					Airdrop airdrop = Airdrop.findAirdrop(name);

					if (!isExecute)
						if (RandomUtil.chance(airdrop.getChanceToExecuteCommand()))
							Common.dispatchCommand(event.getPlayer() , airdrop.getCommandToExecute());


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

			for (int cord = -1; cord <= 1; cord++) {

				for (Entity ent : chest.getWorld().getNearbyEntities(chest.getLocation(),cord,cord,cord)) {
					if (ent instanceof ArmorStand) {
						ent.remove();
					}
				}
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

	@EventHandler
	public void onInteractCompass(PlayerInteractEvent event) {
		if (!CompMetadata.hasMetadata(event.getPlayer().getInventory().getItemInMainHand() , "DewareCompass")) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

			CompassMeta compassMeta = (CompassMeta) event.getPlayer().getInventory().getItemInMainHand().getItemMeta();

			Player player = event.getPlayer();
			Location playerLocation = player.getLocation();
			Location location = compassMeta.getLodestone();

			int distance = (int) MathUtil.floor(playerLocation.distance(location));

			String resultDistance = Replacer.replaceArray(Lang.of("Show_Distance") ,"distance" , distance);

			Common.tellNoPrefix(player , resultDistance);
		}
	}


	/**
		Load cache on join
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PlayerCache cache = PlayerCache.from(event.getPlayer());
	}

	/**
		Remove cache on quit
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		PlayerCache cache = PlayerCache.from(event.getPlayer());
		cache.removeFromMemory();
	}
}
