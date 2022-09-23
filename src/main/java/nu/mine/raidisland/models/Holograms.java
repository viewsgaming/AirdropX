package nu.mine.raidisland.models;

import nu.mine.raidisland.enums.HologramsMode;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Consumer;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleHologram;
import org.mineacademy.fo.remain.Remain;

public class Holograms extends SimpleHologram {

	private final String airdropName;


	private final long time;

	private final HologramsMode mode;

	private long tick;

	public Holograms(Location spawnLocation , String airdropName , long time , HologramsMode mode) {
		super(spawnLocation);
		this.airdropName = airdropName;
		this.time = time;
		this.mode = mode;
		tick = (int) (time * 20);
		setLore(Common.toArray(Replacer.replaceArray(Settings.Airdrop.HOLOGRAMS_LINE , "airdrop_name" , airdropName)));
	}

	@Override
	protected void onTick() {

		tick--;
		int seconds = (int) (tick / 20);

		switch (mode) {
			case TIMEOUT:

				if (tick <= 0) {

					Common.runLater(5 , () -> {
						removeLore();
						remove();
					});

				} else {
					String textReplacer = Replacer.replaceArray(Settings.Airdrop.TIMEOUT_HOLO , "time" , TimeUtil.formatTimeGeneric(seconds));
					Remain.setCustomName(getLoreEntities().get(getLoreEntities().size()-1), textReplacer);
				}

				break;

			case OPENING_DELAY:

				if (tick <= 0) {
					Common.runLater(5 , () -> {
						removeLore();
						remove();
					});


				} else {
					String textReplacer = Replacer.replaceArray(Settings.Airdrop.OPENING_HOLO , "time" , TimeUtil.formatTimeGeneric(seconds));
					Remain.setCustomName(getLoreEntities().get(getLoreEntities().size() - 1), textReplacer);
				}

				break;
		}

	}

	@Override
	protected Entity createEntity() {

		final Consumer<ArmorStand> consumer = armorStand -> {
			armorStand.setGravity(false);
			armorStand.setVisible(false);
			armorStand.setSmall(false);
			armorStand.setInvulnerable(true);

		};

		return this.getLastTeleportLocation().getWorld().spawn(this.getLastTeleportLocation(), ArmorStand.class, consumer);
	}
}
