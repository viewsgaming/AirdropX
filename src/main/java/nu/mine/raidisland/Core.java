package nu.mine.raidisland;

import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.tasks.AutoSpawnTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.metrics.Metrics;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.HashMap;
import java.util.Map;

public final class Core extends SimplePlugin {

	public static final String VERSION = "1.0.0";
	public static final String PREFIX = "&8[&7Airdrop&cX&8]";

	private static final Map<Airdrop , BukkitTask> autoSpawnMap = new HashMap<>();

	@Override
	protected void onPluginStart() {
		Common.log(PREFIX + "&f has been running!");

		Common.setTellPrefix("");
	}
	@Override
	protected void onReloadablesStart() {

		Airdrop.loadAirdrop();
		Airdrop.clearAllWhenReload();
		restartAutoSpawnTask();

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
	private void restartAutoSpawnTask() {
		if (autoSpawnMap.isEmpty()) return;

		for (Map.Entry<Airdrop , BukkitTask> entry : autoSpawnMap.entrySet()) {
			if (entry.getValue() != null) {
				try {
					entry.getValue().cancel();

					autoSpawnMap.replace(entry.getKey() , entry.getValue() , new AutoSpawnTask(entry.getKey()).runTaskTimer(Core.getInstance()
							,0
							, entry.getKey().getAutoSpawnTime().getTimeTicks()));

				} catch (final IllegalStateException ex) {
				}
			}
		}
	}

	@Override
	public int getMetricsPluginId() {
		return 16291;
	}
}
