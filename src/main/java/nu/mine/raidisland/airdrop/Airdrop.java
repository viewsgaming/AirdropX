package nu.mine.raidisland.airdrop;

import lombok.Getter;
import lombok.NonNull;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.Lang;
import org.mineacademy.fo.settings.YamlConfig;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class Airdrop extends YamlConfig {

	private static final ConfigItems<Airdrop> loadedAirdrop = ConfigItems.fromFolder("airdrop" , Airdrop.class);

	private static final Map<Chest , BukkitTask> activeTask = new HashMap<>();

	public static final String NBT_TAG = "AirdropX";


	// ---------- Common ---------- \\
	private String commandToExecute;
	private String airdropName;
	private SimpleTime destroyTime;

	private List<Tuple<ItemStack, Double>> itemsList;

	private boolean randomLocation;
	private int range;

	// ----------------------------- \\

	// ---------- Region ---------- \\

	private boolean worldGuardSetting;

	private String regionByWorldGuard;
	private Location center;
	private int randomSpawnRange;

	private SimpleTime autoSpawnTime;

	// Called when ConfigItems class loads your airdrop from given folder.
	private Airdrop(String name) {
		this(name , null);
	}

	public Airdrop(@NonNull String airdropName , @Nullable List<Tuple<ItemStack, Double>> itemsList) {
		this.airdropName = airdropName;
		this.itemsList = itemsList;
		this.destroyTime = Settings.Airdrop.DEFAULT_AUTO_DESTROY_TIME;
		this.randomLocation = true;
		this.range = Settings.Airdrop.DEFAULT_RANGE;
		this.commandToExecute = "";
		this.worldGuardSetting = false;
		this.center = null;
		this.randomSpawnRange = Settings.Airdrop.DEFAULT_RANGE;
		this.regionByWorldGuard = null;
		this.autoSpawnTime = SimpleTime.from("30 minutes");

		this.loadConfiguration(NO_DEFAULT, "airdrop/" + airdropName + ".yml");
	}

	public void setAutoSpawnTime(SimpleTime autoSpawnTime) {
		this.autoSpawnTime = autoSpawnTime;

		save();
	}

	public void setRegionByWorldGuard(String regionByWorldGuard) {
		this.regionByWorldGuard = regionByWorldGuard;

		save();
	}

	public void setCenter(Location center) {
		this.center = center;

		save();
	}

	public void setRandomSpawnRange(int randomSpawnRange) {
		this.randomSpawnRange = randomSpawnRange;

		save();
	}

	public void setWorldGuardSetting(boolean worldGuardSetting) {
		this.worldGuardSetting = worldGuardSetting;

		save();
	}

	public void setCommandToExecute(String commandToExecute) {
		this.commandToExecute = commandToExecute;

		save();
	}

	public void setRandomLocation(boolean randomLocation) {
		this.randomLocation = randomLocation;

		save();
	}

	public void setRange(int range) {
		this.range = range;

		save();
	}


	public void setItemsList(List<Tuple<ItemStack, Double>> itemsList) {
		this.itemsList = itemsList;

		save();
	}

	public void setDestroyTime(SimpleTime destroyTime) {
		this.destroyTime = destroyTime;

		save();
	}

	@Override
	protected void onLoad() {
		this.airdropName = getString("Name");
		this.destroyTime = getTime("Destroy_Time" , Settings.Airdrop.DEFAULT_AUTO_DESTROY_TIME);
		this.itemsList = getTupleList("Items" , ItemStack.class , Double.class);
		this.range = getInteger("Range" , Settings.Airdrop.DEFAULT_RANGE);
		this.randomLocation = getBoolean("Random_Location" , Settings.Airdrop.DEFAULT_RANDOM_LOCATION);
		this.commandToExecute = getString("Command_To_Execute", "");
		this.randomSpawnRange = getInteger("Random_Spawn_Range" , Settings.Airdrop.DEFAULT_RANGE);
		this.worldGuardSetting = getBoolean("Enabled_World_Guard_Setting" , false);
		this.center = getLocation("Center_Location_For_Auto_Spawn");
		this.regionByWorldGuard = getString("World_Guard_Region");
		this.autoSpawnTime = getTime("Auto_Spawn_Time");
		save();
	}

	@Override
	protected void onSave() {
		this.set("Name" , Common.getOrDefault(this.airdropName , this.getName()));
		this.set("Items" , this.itemsList);
		this.set("Destroy_Time" , this.destroyTime);
		this.set("Range", this.range);
		this.set("Random_Location" , this.randomLocation);
		this.set("Command_To_Execute", this.commandToExecute);
		this.set("Random_Spawn_Range" , this.randomSpawnRange);
		this.set("Enabled_World_Guard_Setting" , this.worldGuardSetting);
		this.set("Center_Location_For_Auto_Spawn" , this.center);
		this.set("World_Guard_Region" , this.regionByWorldGuard);
		this.set("Auto_Spawn_Time" , this.autoSpawnTime);
	}

	public boolean isReadyToStart() {

		if (this.isWorldGuardSetting()) {
			return getRegionByWorldGuard() != null && getAutoSpawnTime() != null && getCenter() != null;
		} else {
			return getCenter() != null && getAutoSpawnTime() != null;
		}
	}

	/* ------------------------------------------------------------------------------- */
	/* Static access */
	/* ------------------------------------------------------------------------------- */

	public static Airdrop createAirdrop(@NonNull final String name) {
		return loadedAirdrop.loadOrCreateItem(name , () -> new Airdrop(name , null));
	}

	public static Airdrop findAirdrop(@NonNull final String name) {
		return loadedAirdrop.findItem(name);
	}

	public static Set<String> getAirdropsNames() {
		return loadedAirdrop.getItemNames();
	}

	public static void loadAirdrop() {
		loadedAirdrop.loadItems();
	}

	public static void removeAirdrop(Airdrop airdrop) {
		loadedAirdrop.removeItem(airdrop);
	}

	public static void addActiveTask(Airdrop airdrop , Chest chest , int timer) {
		activeTask.put(chest , Common.runLater(timer, () -> {
			chest.getLocation().getBlock().setType(Material.AIR);
			String message = Replacer.replaceArray(Lang.of("Broadcast_Timeout"), "prefix" , Core.PREFIX , "airdrop_name" , airdrop.getName());
			Common.broadcast(message);
			activeTask.remove(chest);
		}));

	}

	public static void clearAllWhenReload() {
		if (!activeTask.isEmpty()) {
			for (Map.Entry<Chest, BukkitTask> entry : activeTask.entrySet()) {
				activeTask.put(entry.getKey() , Common.runLater(5, () -> {
					entry.getKey().getBlock().setType(Material.AIR);
					activeTask.remove(entry.getKey());
				}));
			}
		}
	}

	public static void removeActiveTask(Chest chest) {
		activeTask.remove(chest);
	}

	public static BukkitTask getChestFromActiveTask(Chest chest) {
		return activeTask.get(chest);
	}
}
