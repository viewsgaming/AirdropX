package nu.mine.raidisland.tasks;

import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.DataSaver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.settings.Lang;

public class RandomSpawnTask extends BukkitRunnable {

	@Override
	public void run() {

		int reqPlayer = DataSaver.getInstance().getRequiredPlayer();
		Airdrop airdrop = Airdrop.findAirdrop(RandomUtil.nextItem(DataSaver.getInstance().getAirdropName()));

		if (reqPlayer >= Bukkit.getOnlinePlayers().size() + 1) {
			Common.broadcast(Replacer.replaceArray(Lang.of("Broadcast_Not_Enough_Player_To_Spawn"), "prefix", Core.PREFIX, "requirement", reqPlayer, "airdrop_name", airdrop.getName()));
			return;
		}

		Location loc = AirdropUtil.findRandomLocation(DataSaver.getInstance().getCenterLocaiton(), airdrop.getRange());

		AirdropUtil.callAt(airdrop , loc);
	}
}
