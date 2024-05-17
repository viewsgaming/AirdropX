package nu.mine.raidisland.settings;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public final class DataSaver extends YamlConfig {


	private static final DataSaver instance = new DataSaver();

	private List<Location> garbage = new ArrayList<>();

	private SimpleTime delay;
	private int requiredPlayer;
	private List<String> airdropName = new ArrayList<>();
	private Location centerLocaiton;
	private boolean enabled;

	private BukkitTask randomSpawningTask;

	public DataSaver() {

		this.loadConfiguration(NO_DEFAULT , FoConstants.File.DATA);
	}


	public void setCenterLocaiton(Location centerLocaiton) {
		this.centerLocaiton = centerLocaiton;

		save();
	}


	public void setDelay(SimpleTime delay) {
		this.delay = delay;

		save();
	}

	public void setRequiredPlayer(int requiredPlayer) {
		this.requiredPlayer = requiredPlayer;

		save();
	}

	public void addAirdropName(String name) {
		airdropName.add(name);

		save();
	}

	public void removeAirdropName(String name) {
		airdropName.remove(name);

		save();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		save();
	}

	public void addData(Location location) {
		garbage.add(location);

		save();
	}

	public void removeData(Location location){
		garbage.remove(location);

		save();
	}

	public Collection<String> getAirdropsNames() {
		return Collections.unmodifiableCollection(airdropName);
	}

	public void setRandomSpawningTask(BukkitTask randomSpawningTask) {
		this.randomSpawningTask = randomSpawningTask;
	}

	@Override
	protected void onLoad() {
		if (isSet("DataSaver.Remaining_Chest"))
			garbage = getList("DataSaver.Remaining_Chest" , Location.class);

		if (isSet("Random_Spawn.Delay"))
			delay = getTime("Random_Spawn.Delay");

		if (isSet("Random_Spawn.Required_Player"))
			requiredPlayer = getInteger("Random_Spawn.Required_Player");

		if (isSet("Random_Spawn.Enabled"))
			enabled = getBoolean("Random_Spawn.Enabled" , false);

		if (isSet("Random_Spawn.Airdrops_Name"))
			airdropName = getStringList("Random_Spawn.Airdrops_Name");

		if (isSet("Random_Spawn.Center"))
			centerLocaiton = getLocation("Random_Spawn.Center");
	}

	@Override
	protected void onSave() {
		this.set("DataSaver.Remaining_Chest" , garbage);
		this.set("Random_Spawn.Delay" , delay);
		this.set("Random_Spawn.Required_Player" , requiredPlayer);
		this.set("Random_Spawn.Enabled" , enabled);
		this.set("Random_Spawn.Airdrops_Name", airdropName);
		this.set("Random_Spawn.Center" , centerLocaiton);
	}

	public void clean() {
		if (!garbage.isEmpty())
			for (Location location : garbage) {
				location.getBlock().setType(Material.AIR);
				for (int i = -1; i <= 1; i++)
					for (Entity ent : Remain.getNearbyEntities(location, i))
						if (ent instanceof ArmorStand)
							ent.remove();
			}

		this.garbage.clear();
		for (World world : Bukkit.getWorlds())
			world.save();
		save();
	}

	public List<Location> getGarbage() {
		return garbage;
	}

	public SimpleTime getDelay() {
		return delay;
	}

	public int getRequiredPlayer() {
		return requiredPlayer;
	}

	public List<String> getAirdropName() {
		return airdropName;
	}

	public Location getCenterLocaiton() {
		return centerLocaiton;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public BukkitTask getRandomSpawningTask() {
		return randomSpawningTask;
	}

	public static DataSaver getInstance() {
		return instance;
	}

}