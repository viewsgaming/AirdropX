package nu.mine.raidisland.tasks;

import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArmorstandChecker extends BukkitRunnable {

	@Override
	public void run() {

		for (Location location : DataSaver.getInstance().getGarbage()) {

			if (location.getBlock().getType() != Material.CHEST) {

				for (double i = -4; i <= 4; i++)
					for (Entity entity : getNearbyHolograms(location, i))
						if (entity instanceof ArmorStand)
							if (entity.isCustomNameVisible() && CompMetadata.hasMetadata(entity , "AirdropX_Hologram"))
								entity.remove();



			}
		}

	}

	public static Collection<Entity> getNearbyHolograms(final Location location, final double radius) {
		try {
			return location.getWorld().getNearbyEntities(location, Settings.Airdrop.ADJUSTMENT_X, radius, Settings.Airdrop.ADJUSTMENT_Z);

		} catch (final Throwable t) {
			final List<Entity> found = new ArrayList<>();

			for (final Entity nearby : location.getWorld().getEntities())
				if (nearby.getLocation().distance(location) <= radius)
					found.add(nearby);

			return found;
		}
	}
}
