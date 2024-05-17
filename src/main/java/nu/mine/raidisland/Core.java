package nu.mine.raidisland;

import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.models.Holograms;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import nu.mine.raidisland.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Core extends SimplePlugin {

	public static final String VERSION = "1.6.1-Experiment";
	public static final String PREFIX = "&8[&7Airdrop&cX&8]";
	private static final Map<Airdrop , BukkitTask> autoSpawnMap = new HashMap<>();

	/**
	 * Reduce lag
	 */
	private static final Map<Chest , BukkitTask> garbageCollector = new HashMap<>();

	@Override
	protected void onPluginStart() {

		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_16)) {
			Bukkit.getPluginManager().disablePlugin(this);
			Common.log(PREFIX + "&c has been stopping!. You're using supported Minecraft version.");
			return;
		} else {
			Common.setTellPrefix("");
			Common.log(PREFIX + "&f has been running!");
		}

		if (Settings.CrashFixer.ENABLED)
			new CrashFixerTask().runTaskTimer(Core.getInstance() , 0 , Settings.CrashFixer.SPEED.getTimeTicks());
	}

	@Override
	protected void onReloadablesStart() {

		Airdrop.loadAirdrop();
		Holograms.deleteAll();
		Airdrop.clearAllWhenReload();
		Airdrop.clearAllOpeningDelay();
		clearGarbage();
		stopAutoSpawnTask();

		Common.runLater(1 , Airdrop::safetyStartAutoDrop);

		new ArmorstandChecker().runTaskLater(Core.getInstance() , 20);

		if (DataSaver.getInstance().isEnabled()) {

			if (DataSaver.getInstance().getRandomSpawningTask() != null) {
				if (!DataSaver.getInstance().getRandomSpawningTask().isCancelled())
					DataSaver.getInstance().getRandomSpawningTask().cancel();
			} else {
				BukkitTask randomSpawnTask = new RandomSpawnTask().runTaskTimerAsynchronously(Core.getInstance() , 0, DataSaver.getInstance().getDelay().getTimeTicks());
				DataSaver.getInstance().setRandomSpawningTask(randomSpawnTask);
			}

		}
	}

	@Override
	protected void onPluginStop() {
		Holograms.deleteAll();
		Airdrop.clearAllWhenReload();
		Airdrop.clearAllOpeningDelay();
		clearGarbage();
		stopAutoSpawnTask();
	}

	public static void startAutoSpawn(Airdrop airdrop) {
		autoSpawnMap.put(airdrop,
				new AutoSpawnTask(airdrop).runTaskTimerAsynchronously(Core.getInstance()
						,0
						, airdrop.getAutoSpawnTime().getTimeTicks()));
	}

	public static void stopAutoSpawn(Airdrop airdrop) {
		BukkitTask task = autoSpawnMap.get(airdrop);

		if (task != null) {
			try {
				task.cancel();
				autoSpawnMap.remove(airdrop);
			} catch (IllegalStateException ex) {
				autoSpawnMap.remove(airdrop);
			}

		}
	}

	public static boolean containsAirdrop(Airdrop airdrop) {
		return autoSpawnMap.containsKey(airdrop);
	}


	// Using this method when user reload plugin or server.
	public static void stopAutoSpawnTask() {
		if (autoSpawnMap.isEmpty()) return;

		Common.runLater(1 , () -> {
			for (Map.Entry<Airdrop , BukkitTask> entry : autoSpawnMap.entrySet()) {

				if (entry.getValue() != null) {
					try {
						entry.getValue().cancel();
					} catch (final IllegalStateException ignored) {
					}

				}

			}
		});

	}

	public static void addGarbage(Chest chest) {
		garbageCollector.put(chest , new RecycleTask(chest).runTaskTimer(Core.getInstance(),0,20));
	}

	public static void removeGarbage(Chest chest) {
		BukkitTask task = garbageCollector.get(chest);

		try {
			task.cancel();
			garbageCollector.remove(chest);
		} catch (IllegalStateException | NullPointerException ex) {
			garbageCollector.remove(chest);
		}

		DataSaver.getInstance().removeData(chest.getLocation());
	}

	public static void clearGarbage() {
		if (!garbageCollector.isEmpty()) {
			for (Map.Entry<Chest, BukkitTask> entry : garbageCollector.entrySet()) {
				garbageCollector.put(entry.getKey() , Common.runLater(5, () -> {
					entry.getKey().getBlock().setType(Material.AIR);
					garbageCollector.remove(entry.getKey());
				}));
			}
		}
	}


	public static Map<Airdrop , BukkitTask> getAutoSpawnMap() {
		return Collections.unmodifiableMap(autoSpawnMap);
	}

	@Override
	public int getMetricsPluginId() {
		return 16291;
	}
}
