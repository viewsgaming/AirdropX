package nu.mine.raidisland.tasks;

import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.Remain;

public class CrashFixerTask extends BukkitRunnable {

	private int counter;
	private boolean willContinue;

	public CrashFixerTask() {
		willContinue = true;
		counter = 0;
	}

	@Override
	public void run() {
		int lastValue = Settings.CrashFixer.VALUE;
		DataSaver saver = DataSaver.getInstance();

		if (counter < lastValue) {
			if (!saver.getGarbage().isEmpty()) {

				for (Location location : saver.getGarbage()) {
					Location bugLocation = location.clone();
					bugLocation.getBlock().setType(Material.AIR);
					for (int i = -1; i <= 1; i++) {
						for (Entity ent : Remain.getNearbyEntities(bugLocation, i)) {
							if (ent instanceof ArmorStand) {
								ent.remove();
								Common.log("Found glitched airdrop at " + Common.shortLocation(location) + ". Task continue finding!");
							}
						}
					}

					counter++;
				}
			}
		} else {

			saver.clean();
			Common.log("CrashFixer task has been stopped!.");
			this.cancel();
		}
	}
}
