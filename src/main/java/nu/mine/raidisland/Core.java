package nu.mine.raidisland;

import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.tasks.AutoSpawnTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Core extends SimplePlugin {

	public static final String VERSION = "1.2.0a";
	public static final String PREFIX = "&8[&7Airdrop&cX&8]";

	private static final Map<Airdrop , BukkitTask> autoSpawnMap = new HashMap<>();

	@Override
	protected void onPluginStart() {
		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_16)) {
			Bukkit.getPluginManager().disablePlugin(this);
			Common.log(PREFIX + "&c has been stopping!. You're using supported Minecraft version.");
		} else {
			Common.setTellPrefix("");
			Common.log(PREFIX + "&f has been running!");
		}
	}
	@Override
	protected void onReloadablesStart() {

		Airdrop.loadAirdrop();
		Airdrop.clearAllWhenReload();
		stopAutoSpawnTask();

	}

	@Override
	protected void onPluginStop() {
		Airdrop.clearAllWhenReload();
	}

	public static void startAutoSpawn(Airdrop airdrop) {
		autoSpawnMap.put(airdrop,
				new AutoSpawnTask(airdrop).runTaskTimer(Core.getInstance()
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
	private void stopAutoSpawnTask() {
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

	@Override
	public int getMetricsPluginId() {
		return 16291;
	}
}
