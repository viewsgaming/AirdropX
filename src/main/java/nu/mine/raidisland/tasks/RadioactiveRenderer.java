package nu.mine.raidisland.tasks;

import lombok.RequiredArgsConstructor;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.Remain;


public class RadioactiveRenderer extends BukkitRunnable {

	private final Location location;
	int count = Settings.SpawningEvent.RADIATION_TIME;

	public RadioactiveRenderer(Location location) {
		this.location = location;
	}


	@Override
	public void run() {

		if (count <= 0)
			this.cancel();

		for (int x = 0; x < Settings.SpawningEvent.RANDOM_RADIUS / 2; x++) {
			CompParticle.REDSTONE.spawn(location.clone().add(x , 0 , x));
			CompParticle.REDSTONE.spawn(location.clone().add(-x , 0 , -x));
			CompParticle.REDSTONE.spawn(location.clone().add(x , 0 , -x));
			CompParticle.REDSTONE.spawn(location.clone().add(-x , 0 , x));
		}

		for (Entity ent : Remain.getNearbyEntities(location , Settings.SpawningEvent.RADIATION_RANGE / 2)) {
			if (ent instanceof Player) {
				Player player = ((Player) ent).getPlayer();
				player.damage(Settings.SpawningEvent.RADIATION_DPS);
			}
		}

		count--;

	}
}
