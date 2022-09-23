package nu.mine.raidisland;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.enums.HologramsMode;
import nu.mine.raidisland.models.Holograms;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleHologram;
import org.mineacademy.fo.remain.*;
import org.mineacademy.fo.settings.Lang;

import java.util.Iterator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AirdropUtil {

	/**
	 * Called a airdrop at specific location
	 */
	public static void callAt(Airdrop airdrop , final Location destination) {
		if (airdrop.getItemsList().isEmpty()) return;
		if (destination == null) {
			Common.log("cannot find suitable location to spawn. Try to set range higher or increase Location search attempts.");
			return;
		}

		final Location chestLocation = destination.clone();
		final Block block = chestLocation.getBlock();
		chestLocation.getBlock().setType(Material.CHEST);
		final Chest chest = (Chest) block.getState();
		final Inventory chestInv = chest.getInventory();
		chestInv.setMaxStackSize(9 * 6);
		CompMetadata.setMetadata(chest , Airdrop.NBT_TAG , airdrop.getName());
		CompMetadata.setMetadata(chest , "CAN_OPEN" , "No");

		DataSaver.getInstance().addData(chest.getLocation());

		for (int items = 0; items < airdrop.getItemsList().size(); items++) {

			boolean hasEmptySlot = false;
			int filledSlot = 0;

			for (ItemStack stack : chestInv.getContents()) {
				if (stack == null) {
					hasEmptySlot = true;
					break;
				} else {
					filledSlot++;
				}
			}

			try {
				if (airdrop.getItemsList().get(items).getKey() == null) continue;
				if (airdrop.getItemsList().get(items).getValue() == null) continue;
			} catch (NullPointerException ex) {
				continue;
			}

			if (hasEmptySlot && filledSlot < airdrop.getMaximumItems()) {
				if (RandomUtil.chanceD(airdrop.getItemsList().get(items).getValue())) {
					chestInv.addItem(airdrop.getItemsList().get(items).getKey());
				}
			} else {
				break;
			}
		}

		for (int i = 1; i <= 3; i++) {
			chest.getWorld().strikeLightningEffect(chest.getLocation());
		}

		CompSound.ENTITY_LIGHTNING_BOLT_THUNDER.play(chest.getLocation());
		CompParticle.EXPLOSION_HUGE.spawn(chest.getLocation().clone().add(0 , 2 , 0));

		String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Location"), "airdrop_name" , airdrop.getName() , "location" , Common.shortLocation(chest.getLocation()), "prefix" , Core.PREFIX);
		Common.broadcast(broadcast);

		Airdrop.addActiveTask(airdrop , chest , airdrop.getDestroyTime().getTimeTicks());

		Holograms holograms = new Holograms(chest.getLocation().clone().add(Settings.Airdrop.ADJUSTMENT_X , Settings.Airdrop.ADJUSTMENT_Y , Settings.Airdrop.ADJUSTMENT_Z) , airdrop.getName() , airdrop.getDestroyTime().getTimeSeconds() , HologramsMode.TIMEOUT);
		holograms.spawn();
	}

	public static Location findRandomLocation(final Location centerLocation , final int range) {

		for (int i = 0; i < Settings.Airdrop.LOCATION_SEARCH_ATTEMPTS; i++) {
			final Location location = RandomUtil.nextLocation(centerLocation , range , false);
			final int highestPointY = BlockUtil.findHighestBlock(location , material -> !CompMaterial.isLeaves(material));

			if (highestPointY != -1) {
				location.setY(highestPointY);

				final Block block = location.getBlock();
				final Block blockAbove = block.getRelative(BlockFace.UP);
				final Block blockBelow = block.getRelative(BlockFace.DOWN);

				if (blockBelow.getType().isSolid() && CompMaterial.isAir(block) && CompMaterial.isAir(blockAbove))
					return location;

			}
		}

		return null;
	}

	public static void startOpeningDelay(Location location , Airdrop airdrop , Chest chest) {
		for (int cord = -1; cord <= 1; cord++) {
			for (Entity ent : location.getWorld().getNearbyEntities(location,cord,cord,cord)) {
				if (ent instanceof ArmorStand) {
					ent.remove();
				}
			}
		}


		Airdrop.addOpeningDelay(airdrop , chest);

		Holograms openingHolo = new Holograms(location.clone().add(Settings.Airdrop.ADJUSTMENT_X , Settings.Airdrop.ADJUSTMENT_Y , Settings.Airdrop.ADJUSTMENT_Z) , airdrop.getName(),airdrop.getOpeningDelayTime().getTimeSeconds() , HologramsMode.OPENING_DELAY);
		openingHolo.spawn();
	}

	public static void deleteChest(Chest chest) {

		final Inventory chestInv = chest.getInventory();
		if (!chestInv.isEmpty())
			chestInv.clear();
		chest.getLocation().getBlock().setType(Material.AIR);

	}
}
