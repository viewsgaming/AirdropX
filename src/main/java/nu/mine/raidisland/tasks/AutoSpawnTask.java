package nu.mine.raidisland.tasks;

import lombok.RequiredArgsConstructor;
import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.debug.LagCatcher;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.settings.Lang;

@RequiredArgsConstructor
public class AutoSpawnTask extends BukkitRunnable {

	private final Airdrop airdrop;

	@Override
	public void run() {

		if (airdrop.getRequirementConnectedPlayers() >= Bukkit.getOnlinePlayers().size() + 1) {
			Common.broadcast(Replacer.replaceArray(Lang.of("Broadcast_Not_Enough_Player_To_Spawn"),"prefix" , Core.PREFIX , "requirement" , airdrop.getRequirementConnectedPlayers() , "airdrop_name" , airdrop.getName()));
			return;
		}

		int randomRange = RandomUtil.nextBetween(10 , airdrop.getRandomSpawnRange());
		Location randomLocation;

		if (airdrop.isWorldGuardSetting()) {
			Valid.checkNotNull(HookManager.getRegion(airdrop.getRegionByWorldGuard()) , "AirdropX cannot find the region calls " + airdrop.getRegionByWorldGuard() + " please re-check your region name.");

			randomLocation = AirdropUtil.findRandomLocation(HookManager.getRegion(airdrop.getRegionByWorldGuard()).getCenter() , randomRange);
			AirdropUtil.callAt(airdrop , randomLocation);

		} else {

			randomLocation = AirdropUtil.findRandomLocation(airdrop.getCenter() ,randomRange);
			AirdropUtil.callAt(airdrop , randomLocation);
		}

		LagCatcher.end("AutoSpawnTask");
	}
}
