package nu.mine.raidisland.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.CompMetadata;

public class OpeningDelayTask extends BukkitRunnable {

	private final Airdrop airdrop;
	private long second;

	private final Chest chest;

	public OpeningDelayTask(Airdrop airdrop , Chest chest) {
		this.airdrop = airdrop;
		this.second = airdrop.getOpeningDelayTime().getTimeSeconds();
		this.chest = chest;
	}

	@Override
	public void run() {

		if(second < 0) this.cancel();
		if(second == 0) {
			Airdrop.removeOpeningDelay(chest);
			if (CompMetadata.hasMetadata(chest , "CAN_OPEN"))
				CompMetadata.setMetadata(chest , "CAN_OPEN", "Yes");
			Core.addGarbage(chest);
			this.cancel();
		} else {
			second--;
		}
	}
}
