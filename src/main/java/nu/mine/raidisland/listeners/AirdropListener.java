package nu.mine.raidisland.listeners;

import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;


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
			event.setCancelled(true);
		}
	}
}
