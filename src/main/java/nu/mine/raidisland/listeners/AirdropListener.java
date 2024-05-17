package nu.mine.raidisland.listeners;

import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.MetadataValue;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.settings.Lang;

import java.util.Set;


@AutoRegister
public final class AirdropListener implements Listener {


	@EventHandler
	public void onBlockExplode(final EntityExplodeEvent event) {
		if (event.getEntity() instanceof TNTPrimed
				|| event.getEntity() instanceof EnderCrystal
				|| event.getEntity() instanceof Creeper
				|| event.getEntity() instanceof RespawnAnchor
				|| event.getEntity() instanceof Wither
				|| event.getEntity() instanceof Fireball) {

			event.blockList().removeIf(block -> {

				if (block.getType().equals(Material.CHEST)) {

					Chest chest = (Chest) block.getState();

					return CompMetadata.hasMetadata(chest, Airdrop.NBT_TAG);
				}

				return false;
			});
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onCombust(EntityCombustEvent event) {
		if (CompMetadata.hasMetadata(event.getEntity() , "DewareZombie")) {
			if (Settings.SpawningEvent.IGNORED_COMBUST)
				event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onAirStrike(EntityExplodeEvent event) {

		if (event.getEntity() instanceof TNTPrimed) {
			TNTPrimed tnt = (TNTPrimed) event.getEntity();
			if (CompMetadata.hasMetadata(tnt ,"DewareTnt")) {
				event.blockList().clear();
			}
		}
	}

	@EventHandler
	public void onFallingBlockDropItem(EntityDropItemEvent event) {
		if (event.getEntityType() == EntityType.FALLING_BLOCK) {
			if (!CompMetadata.hasMetadata(event.getEntity(), "Airdrop_FallingBlock"))
				return;

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		// Check if the destination inventory is a hopper
		if (isHopper(event.getDestination())) {
			// Get the source block of the hopper
			Block sourceBlock = event.getSource().getLocation().getBlock();

			// Check if the source block is a chest and has the metadata
			if (sourceBlock.getType() == Material.CHEST && CompMetadata.hasMetadata(sourceBlock.getState() , Airdrop.NBT_TAG)) {
				event.setCancelled(true); // Cancel the item transfer
			}
		}
	}

	private boolean isHopper(Inventory inventory) {
		return inventory.getType() == org.bukkit.event.inventory.InventoryType.HOPPER;
	}

	@EventHandler
	public void onChestConnect(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location blockLoc = block.getLocation();


		if (!(block.getType() == Material.CHEST))
			return;
			
		Block rightBlock = blockLoc.getBlock().getRelative(BlockFace.EAST);
		Block leftBlock = blockLoc.getBlock().getRelative(BlockFace.WEST);

		if (rightBlock.getType() == Material.CHEST && CompMetadata.hasMetadata(rightBlock.getState(),Airdrop.NBT_TAG)) {
			event.setCancelled(true);

		} else if (leftBlock.getType() == Material.CHEST && CompMetadata.hasMetadata(leftBlock.getState(),Airdrop.NBT_TAG)) {
			event.setCancelled(true);

		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {

		int radius = Settings.RegionPrevention.DISALLOW_RADIUS;
		Player player = event.getPlayer();
		Block placedBlock = event.getBlock();
		Location placedLocation = placedBlock.getLocation();

		if (Settings.RegionPrevention.BYPASS_OP)
			if (player.isOp())
				return;

		if (AirdropUtil.isAirdropInRadius(placedLocation , radius, radius)) {
			Common.tellNoPrefix(player , Replacer.replaceArray(Lang.of("Cannot_Build") , "radius" , radius));
			event.setCancelled(true);
		}
	}
}
