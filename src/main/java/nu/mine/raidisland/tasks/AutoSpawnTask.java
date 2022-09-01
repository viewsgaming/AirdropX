package nu.mine.raidisland.tasks;

import lombok.RequiredArgsConstructor;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.debug.LagCatcher;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.Lang;

@RequiredArgsConstructor
public class AutoSpawnTask extends BukkitRunnable {
	private final Airdrop airdrop;

	@Override
	public void run() {

		LagCatcher.start("AutoSpawnTask");

		int randomRange = RandomUtil.nextBetween(10 , airdrop.getRandomSpawnRange());
		Location randomLocation;

		if (airdrop.isWorldGuardSetting()) {
			Valid.checkNotNull(HookManager.getRegion(airdrop.getRegionByWorldGuard()) , "AirdropX cannot find the region calls " + airdrop.getRegionByWorldGuard() + " please re-check your region name.");

			randomLocation = findRandomLocation(HookManager.getRegion(airdrop.getRegionByWorldGuard()).getCenter() , randomRange);
			callAt(airdrop , randomLocation);

		} else {

			randomLocation = findRandomLocation(airdrop.getCenter() ,randomRange);
			callAt(airdrop , randomLocation);
		}

		LagCatcher.end("AutoSpawnTask");
	}

	private void callAt(Airdrop airdrop , final Location destination) {
		if (airdrop.getItemsList().isEmpty()) return;
		final Location chestLocation = destination.clone();
		final Block block = chestLocation.getBlock();
		chestLocation.getBlock().setType(Material.CHEST);
		final Chest chest = (Chest) block.getState();
		final Inventory chestInv = chest.getInventory();
		CompMetadata.setMetadata(chest , Airdrop.NBT_TAG , airdrop.getName());


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
	}

	private Location findRandomLocation(final Location centerLocation , final int range) {

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
}
