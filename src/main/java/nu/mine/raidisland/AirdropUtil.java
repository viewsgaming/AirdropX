package nu.mine.raidisland;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.enums.HologramsMode;
import nu.mine.raidisland.enums.SpawningEvent;
import nu.mine.raidisland.models.Holograms;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import nu.mine.raidisland.tasks.RadioactiveRenderer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.*;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.*;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AirdropUtil {

	private static final int CHUNK_LOAD_RADIUS = 2;


	/**
	 * Called a airdrop at specific location
	 */
	public static void callAt(Airdrop airdrop , final Location destination) { // Test
		Common.runLater(() -> {
			if (airdrop.getItemsList().isEmpty()) return;
			if (destination == null) {
				Common.log("cannot find suitable location to spawn. Try to set range higher, increase Location search attempts or check worldguard region if installed.");
				return;
			}

			loadChunksAsync(destination);

			Common.runLater(Settings.Airdrop.BIG_WORLD ? 20 * 5 : 1 , () -> {
				if (airdrop.getOnSpawnEvent().contains(SpawningEvent.FALLING_ANIMATION)) {
					FallingBlock fallingBlock = Remain.spawnFallingBlock(
							destination.clone().add(0, Settings.Airdrop.FALLING_HEIGHT,0) , Settings.Airdrop.FALLING_MATERIAL.toMaterial());
					fallingBlock.setDropItem(true);
					CompMetadata.setMetadata(fallingBlock , "Airdrop_FallingBlock");

					EntityUtil.trackFlying(fallingBlock, () -> CompParticle.REDSTONE.spawn(fallingBlock.getLocation()));

					EntityUtil.trackFalling(fallingBlock , () -> {
						CompSound.FALL_BIG.play(fallingBlock.getLocation() , 5.0F , 5.0F);
						fallingBlock.remove();
						fallingBlock.getLocation().getBlock().setType(Material.AIR);

						spawnAirdrop(airdrop , destination);
					});

				} else {

					spawnAirdrop(airdrop , destination);
				}
			});
		});
	}

	public static Location findRandomLocation(final Location centerLocation , final int range) {

		for (int i = 0; i < Settings.Airdrop.LOCATION_SEARCH_ATTEMPTS; i++) {

			boolean canSpawn = false;
			final Location location = RandomUtil.nextLocation(centerLocation, range, false);
			final int highestPointY = !location.getWorld().getEnvironment().equals(World.Environment.NETHER)
					? BlockUtil.findHighestBlock(location, material -> !CompMaterial.isLeaves(material))
					: BlockUtil.findHighestNetherAirBlock(location);

			if (location.getWorld().getEnvironment().equals(World.Environment.NETHER) && highestPointY >= 128)
				return null;

			if (Settings.WorldGuard.ENABLED) {
				if (!Settings.WorldGuard.BYPASS_REGION.isEmpty()) {
					for (String region : Settings.WorldGuard.BYPASS_REGION) {

						if (HookManager.getRegion(region) == null) {
							Common.error(new NullPointerException(), "There are no region name " + region + ". Please correct it.");
							break;
						}

						if (HookManager.getRegion(region).isWithin(location))
							canSpawn = true;
					}
				} else {
					if (!HookManager.getRegions().isEmpty()) {
						for (String region : HookManager.getRegions()) {
							if (!HookManager.getRegion(region).isWithin(location)) {
								canSpawn = true;
							}
						}
					} else {
						canSpawn = true;
					}
				}

			} else
				canSpawn = true;


			if (highestPointY != -1) {

				if (canSpawn) {
					location.setY(highestPointY);

					final Block block = location.getBlock();
					final Block blockAbove = block.getRelative(BlockFace.UP);
					final Block blockBelow = block.getRelative(BlockFace.DOWN);

					if (blockBelow.getType().isSolid() && CompMaterial.isAir(block) && CompMaterial.isAir(blockAbove))
						return location;
				}

			}
		}

		return null;
	}



	public static void startOpeningDelay(Location location , Airdrop airdrop , Chest chest) {
		for (int cord = -1; cord <= 1; cord++) {
			for (Entity ent : location.getWorld().getNearbyEntities(location, cord, cord, cord)) {
				if (ent instanceof ArmorStand) {
					ent.remove();
				}
			}
		}

		Airdrop.addOpeningDelay(airdrop , chest);

		if (Settings.Airdrop.HOLOGRAMS_ENABLED) {
			Holograms openingHolo = new Holograms(
					location.clone().add(Settings.Airdrop.ADJUSTMENT_X, Settings.Airdrop.ADJUSTMENT_Y, Settings.Airdrop.ADJUSTMENT_Z)
					, airdrop.getName(), airdrop.getOpeningDelayTime().getTimeSeconds(), HologramsMode.OPENING_DELAY);
			openingHolo.spawn();
		}
	}

	public static void deleteChest(Chest chest) {

		if (Settings.Airdrop.AUTO_REMOVE_CHEST) {
			final Inventory chestInv = chest.getInventory();
			if (!chestInv.isEmpty())
				chestInv.clear();
			chest.getLocation().getBlock().setType(Material.AIR);
		}

	}

	private static void spawnAirdrop(Airdrop airdrop, Location destination) {
		Location chestLocation = destination.clone();

		if (chestLocation.getBlock().getType() == Material.CHEST) {
			Common.log("&cYou cannot spawn airdrop at &7" + Common.shortLocation(chestLocation) + "&c because there are remaining airdrop.");
			return;
		}

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

		for (SpawningEvent event : airdrop.getOnSpawnEvent()) {
			if (event == SpawningEvent.FALLING_ANIMATION) continue;
			callSpawningEvent(event, chestLocation);
		}

		String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Location"), "airdrop_name" , airdrop.getName()
				, "world", chest.getLocation().getWorld().getName()
				, "x" , chest.getLocation().getBlockX()
				, "y" , chest.getLocation().getBlockY()
				, "z" , chest.getLocation().getBlockZ()
				, "prefix" , Core.PREFIX);
		Common.broadcast(broadcast);

		Airdrop.addActiveTask(airdrop , chest , airdrop.getDestroyTime().getTimeTicks());

		if (Settings.Airdrop.HOLOGRAMS_ENABLED) {
			Holograms holograms = new Holograms(chest.getLocation().clone().add(Settings.Airdrop.ADJUSTMENT_X, Settings.Airdrop.ADJUSTMENT_Y, Settings.Airdrop.ADJUSTMENT_Z), airdrop.getName(), airdrop.getDestroyTime().getTimeSeconds(), HologramsMode.TIMEOUT);
			holograms.spawn();
		}
	}

	public static Location getAirdropLocation() {
		DataSaver saver = DataSaver.getInstance();

		return saver.getGarbage().isEmpty() ? null : RandomUtil.nextItem(saver.getGarbage());
	}

	private static void callSpawningEvent(SpawningEvent event , Location location) {

		switch (event) {
			case LIGHTNING_STRIKE:

				for (int i = 1; i <= Settings.SpawningEvent.HOW_MANY_STRIKE; i++)
					if (Settings.SpawningEvent.LIGHTNING_DEAL_DAMAGE)
						location.getWorld().strikeLightning(location);
					else
						location.getWorld().strikeLightningEffect(location);

				CompSound.ENTITY_LIGHTNING_BOLT_THUNDER.play(location);
				CompParticle.EXPLOSION_HUGE.spawn(location.clone().add(0 , 2 , 0));
				break;

			case SPAWN_ZOMBIE:

				for (int i = 0; i < Settings.SpawningEvent.HOW_MANY_ZOMBIE; i++) {
					Entity zombie = location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
					CompMetadata.setMetadata(zombie , "DewareZombie" , "A");
					Remain.setCustomName(zombie , Settings.SpawningEvent.ZOMBIE_NAME);
					CompAttribute.GENERIC_MAX_HEALTH.set((LivingEntity) zombie, Settings.SpawningEvent.ZOMBIE_HEALTH);
					((LivingEntity) zombie).setHealth(Settings.SpawningEvent.ZOMBIE_HEALTH);
					CompAttribute.GENERIC_MOVEMENT_SPEED.set((LivingEntity) zombie, Settings.SpawningEvent.ZOMBIE_WALK_SPEED);
					CompAttribute.GENERIC_ATTACK_DAMAGE.set((LivingEntity) zombie, Settings.SpawningEvent.ZOMBIE_ATTACK_DAMAGE);
				}
				break;

			case AIRSTRIKE:

				CompSound.BLOCK_BELL_USE.play(location , 50 ,50);
				for (Entity ent : Remain.getNearbyEntities(location , Settings.SpawningEvent.BOSS_BAR_WARNING_RANGE)) {
					if (ent instanceof Player) {
						Remain.sendBossbarTimed((Player) ent, Settings.SpawningEvent.BOSS_BAR_TEXT, Settings.SpawningEvent.BOSS_BAR_TIMED);
					}
				}



				Common.runLater(20 * Settings.SpawningEvent.BOSS_BAR_TIMED , () -> {
					for (int i = 0; i < Settings.SpawningEvent.HOW_MANY_TNT; i++) {
						Location randLoc = RandomUtil.nextLocation(location.clone() , Settings.SpawningEvent.RANDOM_RADIUS ,false);
						Entity tnt = location.getWorld().spawnEntity(randLoc.add(0 ,40,0) , EntityType.PRIMED_TNT);
						if (!Settings.SpawningEvent.DESTROY_BLOCK)
							CompMetadata.setMetadata(tnt , "DewareTnt","A");

					}
				});
				break;

			case RADIATION_AREA:

				new RadioactiveRenderer(location).runTaskTimer(Core.getInstance(), 0,20);

				break;
		}
	}

	public static String formatTimeGeneric(long seconds) {
		long second = seconds % 60L;
		long minute = seconds / 60L;
		String hourMsg = "";
		String[] secondPlural = Lang.of("Cases.Second").split(",");
		String[] minutePlural = Lang.of("Cases.Minute").split(",");
		String[] hourPlural = Lang.of("Cases.Hour").split(",");

		if (minute >= 60L) {
			long hour = seconds / 60L / 60L;
			minute %= 60L;
			hourMsg = hour + (hour == 1L ? " " + hourPlural[0].trim() : " " + hourPlural[1].trim()) + " ";
		}

		return hourMsg + (minute != 0L ? minute : "") + (minute > 0L ? (minute == 1L ? " " + minutePlural[0].trim(): " " + minutePlural[1].trim()) + " " : "") + Long.parseLong(String.valueOf(second)) + (Long.parseLong(String.valueOf(second)) == 1L ? " " + secondPlural[0].trim() : " " + secondPlural[1].trim());
	}

	public static void loadChunksAsync(Location location) {
		Common.runAsync(() -> {
			World world = location.getWorld();
			int chunkX = location.getChunk().getX();
			int chunkZ = location.getChunk().getZ();

			for (int x = chunkX - CHUNK_LOAD_RADIUS; x <= chunkX + CHUNK_LOAD_RADIUS; x++) {
				for (int z = chunkZ - CHUNK_LOAD_RADIUS; z <= chunkZ + CHUNK_LOAD_RADIUS; z++) {
					Chunk chunk = world.getChunkAt(x, z);
					if (!chunk.isLoaded()) {
						chunk.load(Settings.Airdrop.LOAD_CHUNK);
					}
				}
			}
		});
	}

	public static Boolean isAirdropInRadius(final Location loc, final int height, final int radius) {


		for (int y = -height; y <= height; y++)
			for (int x = -radius; x <= radius; x++)
				for (int z = -radius; z <= radius; z++) {
					final Block checkBlock = loc.getBlock().getRelative(x, y, z);

					if (checkBlock.getType() == CompMaterial.CHEST.toMaterial() && CompMetadata.hasMetadata(checkBlock.getState() , Airdrop.NBT_TAG)) {
						return true;
					}

				}

		return false;
	}
}
