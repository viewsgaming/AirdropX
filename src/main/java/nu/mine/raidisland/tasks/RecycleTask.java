package nu.mine.raidisland.tasks;

import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.debug.LagCatcher;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.settings.Lang;

public class RecycleTask extends BukkitRunnable {

	private long seconds;
	private final Chest chest;

	public RecycleTask(Chest chest) {
		this.seconds = Settings.Airdrop.DELETE_WHEN_DELAY_IS_END.getTimeSeconds();
		this.chest = chest;
	}


	@Override
	public void run() {
		/*
		Inventory chestInv = chest.getInventory();
		if (chestInv.isEmpty()) {
			clearData();
			this.cancel();
		}
		*/

		if(seconds < 0) this.cancel();
		if(seconds == 0) {
			clearData();
			this.cancel();
		} else {
			seconds--;
		}
	}

	private void clearData() {
		// chest.getLocation().getBlock().setType(Material.AIR);
		AirdropUtil.deleteChest(chest);
		Core.removeGarbage(chest);
		DataSaver.getInstance().removeData(chest.getLocation());
	}
}
