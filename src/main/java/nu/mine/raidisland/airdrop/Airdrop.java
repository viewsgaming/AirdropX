package nu.mine.raidisland.airdrop;

import lombok.Getter;
import lombok.NonNull;
import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.enums.SpawningEvent;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.settings.Settings;
import nu.mine.raidisland.tasks.OpeningDelayTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.Lang;
import org.mineacademy.fo.settings.YamlConfig;

import javax.annotation.Nullable;
import java.util.*;

@Getter
public class Airdrop extends YamlConfig {

	private static final ConfigItems<Airdrop> loadedAirdrop = ConfigItems.fromFolder("airdrop" , Airdrop.class);

	private static final Map<Chest , BukkitTask> activeTask = new HashMap<>();

	private static final Map<Chest , BukkitTask> openingDelayTask = new HashMap<>();


	public static final String NBT_TAG = "AirdropX";


	// ---------- Common ---------- \\
	private String commandToExecute;
	private int chanceToExecuteCommand;
	private String airdropName;
	private SimpleTime destroyTime;

	private List<Tuple<ItemStack, Double>> itemsList;

	private boolean randomLocation;
	private int range;

	private int maximumItems;

	private SimpleTime openingDelayTime;

	private List<SpawningEvent> onSpawnEvent = new ArrayList<>();

	private boolean autoStartSpawn;

	// ---------------------------- \\

	// ---------- Auto drop ---------- \\

	private boolean worldGuardSetting;

	private String regionByWorldGuard;
	private Location center;
	private int randomSpawnRange;
	private SimpleTime autoSpawnTime;

	private int requirementConnectedPlayers;

	// ----------------------------- \\


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
		this.chanceToExecuteCommand = 100;
		this.worldGuardSetting = false;
		this.center = null;
		this.randomSpawnRange = Settings.Airdrop.DEFAULT_RANGE;
		this.regionByWorldGuard = null;
		this.autoSpawnTime = SimpleTime.from("30 minutes");
		this.maximumItems = 27;
		this.requirementConnectedPlayers = 1;
		this.openingDelayTime = Settings.Airdrop.DEFAULT_OPENING_DELAY_TIME;
		this.onSpawnEvent.add(SpawningEvent.LIGHTNING_STRIKE);
		this.autoStartSpawn = false;

		this.loadConfiguration(NO_DEFAULT, "airdrop/" + airdropName + ".yml");
	}


	public void setAutoStartSpawn(boolean autoStartSpawn) {
		this.autoStartSpawn = autoStartSpawn;

		save();
	}

	public void setOpeningDelayTime(SimpleTime openingDelayTime) {
		this.openingDelayTime = openingDelayTime;

		save();
	}

	public void setChanceToExecuteCommand(int chanceToExecuteCommand) {
		this.chanceToExecuteCommand = chanceToExecuteCommand;

		save();
	}

	public void setRequirementConnectedPlayers(int requirementConnectedPlayers) {
		this.requirementConnectedPlayers = requirementConnectedPlayers;

		save();
	}

	public void setMaximumItems(int maximumItems) {
		this.maximumItems = maximumItems;

		save();
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

	public void addSpawningEvent(SpawningEvent event) {
		this.onSpawnEvent.add(event);

		save();
	}

	public void removeSpawningEvent(SpawningEvent event) {
		this.onSpawnEvent.remove(event);

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
		this.chanceToExecuteCommand = getInteger("Chance_To_Execute", 100);
		this.randomSpawnRange = getInteger("Random_Spawn_Range" , Settings.Airdrop.DEFAULT_RANGE);
		this.worldGuardSetting = getBoolean("Enabled_World_Guard_Setting" , false);
		this.center = getLocation("Center_Location_For_Auto_Spawn");
		this.regionByWorldGuard = getString("World_Guard_Region");
		this.autoSpawnTime = getTime("Auto_Spawn_Time");
		this.maximumItems = getInteger("Maximum_Items" , 27);
		this.requirementConnectedPlayers = getInteger("Requirement_Connected_Players" , 1);
		this.openingDelayTime = getTime("Opening_Delay_Time" , Settings.Airdrop.DEFAULT_OPENING_DELAY_TIME);
        this.onSpawnEvent = getList("Spawning_Event" , SpawningEvent.class);
		this.autoStartSpawn = getBoolean("Auto_Start_Spawn_When_Server_Start" , false);

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
		this.set("Chance_To_Execute" , this.chanceToExecuteCommand);
		this.set("Random_Spawn_Range" , this.randomSpawnRange);
		this.set("Enabled_World_Guard_Setting" , this.worldGuardSetting);
		this.set("Center_Location_For_Auto_Spawn" , this.center);
		this.set("World_Guard_Region" , this.regionByWorldGuard);
		this.set("Auto_Spawn_Time" , this.autoSpawnTime);
		this.set("Maximum_Items" , this.maximumItems);
		this.set("Requirement_Connected_Players" , this.requirementConnectedPlayers);
		this.set("Opening_Delay_Time" , this.openingDelayTime);
		this.set("Spawning_Event" , this.onSpawnEvent);
		this.set("Auto_Start_Spawn_When_Server_Start" , this.autoStartSpawn);
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

	public static List<Airdrop> getAirdrops() {
		return loadedAirdrop.getItems();
	}


	/* ------------------------------------------------------------------------------- */
	/* Static access (Active Task) */
	/* ------------------------------------------------------------------------------- */

	public static void addActiveTask(Airdrop airdrop , Chest chest , int timer) {

		activeTask.put(chest , Common.runLater(timer, () -> {
			AirdropUtil.deleteChest(chest);
			String message = Replacer.replaceArray(Lang.of("Broadcast_Timeout"), "prefix" , Core.PREFIX , "airdrop_name" , airdrop.getName());
			Common.broadcast(message);
			activeTask.remove(chest);
			DataSaver.getInstance().removeData(chest.getLocation());
		}));

	}

	public static void clearAllWhenReload() {
		if (!activeTask.isEmpty()) {
			for (Map.Entry<Chest, BukkitTask> entry : activeTask.entrySet()) {

				activeTask.put(entry.getKey() , Common.runLater(5, () -> {
					AirdropUtil.deleteChest(entry.getKey());
					activeTask.remove(entry.getKey());
					DataSaver.getInstance().removeData(entry.getKey().getLocation());
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


	/* ------------------------------------------------------------------------------- */
	/* Static access (Opening Delay Task) */
	/* ------------------------------------------------------------------------------- */

	public static void addOpeningDelay(Airdrop airdrop , Chest chest) {
		openingDelayTask.put(chest , Common.runTimer(20 , new OpeningDelayTask(airdrop , chest)));
	}

	public static void clearAllOpeningDelay() {
		if (!openingDelayTask.isEmpty()) {
			for (Map.Entry<Chest, BukkitTask> entry : openingDelayTask.entrySet()) {
				openingDelayTask.put(entry.getKey() , Common.runLater(5, () -> {
					if (Settings.Airdrop.AUTO_REMOVE_CHEST)
						entry.getKey().getBlock().setType(Material.AIR);
					openingDelayTask.remove(entry.getKey());
					DataSaver.getInstance().removeData(entry.getKey().getLocation());
				}));
			}
		}
	}

	public static void removeOpeningDelay(Chest chest) {
		openingDelayTask.remove(chest);
	}

	public static void safetyStartAutoDrop() {
		Core.stopAutoSpawnTask();
		loadAirdrop();

		for (Airdrop airdrop : getAirdrops()) {
			if (airdrop.isReadyToStart() && airdrop.isAutoStartSpawn()) {
				Core.startAutoSpawn(airdrop);
			} else {
				if (Core.containsAirdrop(airdrop))
					Core.stopAutoSpawn(airdrop);
			}
		}
	}
}
