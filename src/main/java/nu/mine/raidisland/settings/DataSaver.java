package nu.mine.raidisland.settings;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class DataSaver extends YamlConfig {

	@Getter
	private static final DataSaver instance = new DataSaver();

	private List<Location> garbage = new ArrayList<>();

	public DataSaver() {

		this.loadConfiguration(NO_DEFAULT , FoConstants.File.DATA);
	}


	public void addData(Location location) {
		garbage.add(location);

		save();
	}

	public void removeData(Location location){
		garbage.remove(location);

		save();
	}

	@Override
	protected void onLoad() {
		if (isSet("DataSaver.Remaining_Chest"))
			garbage = getList("DataSaver.Remaining_Chest" , Location.class);
	}

	@Override
	protected void onSave() {
		this.set("DataSaver.Remaining_Chest" , garbage);
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

}