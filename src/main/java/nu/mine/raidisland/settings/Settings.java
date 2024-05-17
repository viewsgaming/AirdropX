package nu.mine.raidisland.settings;

import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;

public final class Settings extends SimpleSettings {

	@Override
	protected int getConfigVersion() {
		return 1;
	}

	public final static class CrashFixer {
		public static Boolean ENABLED;
		public static Integer VALUE;
		public static SimpleTime SPEED;

		private static void init() {
			setPathPrefix("Crash_Fixer");
			ENABLED = getBoolean("Enabled");
			VALUE = getInteger("Value");
			SPEED = getTime("Speed");
		}
	}

	public final static class Airdrop {

		public static SimpleTime DEFAULT_AUTO_DESTROY_TIME;
		public static Integer DEFAULT_RANGE;
		public static Boolean DEFAULT_RANDOM_LOCATION;
		public static Boolean BROADCAST_LOCATION_ON_CALL;
		public static Boolean AUTO_REMOVE_CHEST;
		public static Integer LOCATION_SEARCH_ATTEMPTS;
		public static Integer DELAY_BETWEEN_EACH_UNBOXING;
		public static SimpleTime DEFAULT_OPENING_DELAY_TIME;
		public static SimpleTime DELETE_WHEN_DELAY_IS_END;
		public static List<String> HOLOGRAMS_LINE;
		public static String TIMEOUT_HOLO;
		public static String OPENING_HOLO;
		public static Double ADJUSTMENT_X;
		public static Double ADJUSTMENT_Y;
		public static Double ADJUSTMENT_Z;
		public static Boolean HOLOGRAMS_ENABLED;

		public static Double FALLING_HEIGHT;
		public static CompMaterial FALLING_MATERIAL;
		public static Boolean BIG_WORLD;
		public static Boolean LOAD_CHUNK;

		private static void init() {
			setPathPrefix("Airdrop");

			DEFAULT_AUTO_DESTROY_TIME = getTime("Default_Auto_Destroy_Time");
			DEFAULT_RANGE = getInteger("Default_Range");
			DEFAULT_RANDOM_LOCATION = getBoolean("Default_Random_Location");
			BROADCAST_LOCATION_ON_CALL = getBoolean("Broadcast_Location_On_Call");
			AUTO_REMOVE_CHEST = getBoolean("Auto_Remove_Chest");
			LOCATION_SEARCH_ATTEMPTS = getInteger("Location_Search_Attempts");
			DELAY_BETWEEN_EACH_UNBOXING = getInteger("Delay_Between_Each_Unboxing");
			DEFAULT_OPENING_DELAY_TIME = getTime("Default_Opening_Delay_Time");
			DELETE_WHEN_DELAY_IS_END = getTime("Delete_When_Delay_Is_End");
			HOLOGRAMS_LINE = getStringList("Holograms.Main");
			HOLOGRAMS_ENABLED = getBoolean("Holograms.Enabled");
			TIMEOUT_HOLO = getString("Holograms.Last_Line.Timeout");
			OPENING_HOLO = getString("Holograms.Last_Line.Opening");
			ADJUSTMENT_X = getDouble("Holograms.Adjustment.X");
			ADJUSTMENT_Y = getDouble("Holograms.Adjustment.Y");
			ADJUSTMENT_Z = getDouble("Holograms.Adjustment.Z");
			FALLING_HEIGHT = getDouble("Falling_Animation.Height");
			FALLING_MATERIAL = getMaterial("Falling_Animation.Material");
			BIG_WORLD = getBoolean("Big_World");
			LOAD_CHUNK = getBoolean("Load_Chunk");

		}
	}

	public final static class Commands {

		public static Integer COMPASS_COOLDOWN;

		private static void init() {
			setPathPrefix("Commands");

			COMPASS_COOLDOWN = getInteger("Compass.Cooldown");
		}

	}

	public final static class RegionPrevention {

		public static Boolean ENABLED;
		public static List<String> BYPASS_REGIONS;

		public static Boolean ALLOW_BUILDING;
		public static Integer DISALLOW_RADIUS;
		public static Boolean BYPASS_OP;

		private static void init() {
			setPathPrefix("Region_Prevention");

			ENABLED = getBoolean("Enabled");
			BYPASS_REGIONS = getStringList("Bypass_Region");
			ALLOW_BUILDING = getBoolean("Allow_Building_Nearby_Airdrops.Enabled");
			DISALLOW_RADIUS = getInteger("Allow_Building_Nearby_Airdrops.Radius");
			BYPASS_OP = getBoolean("Allow_Building_Nearby_Airdrops.Bypass_OP");
		}

	}

	public final static class WorldGuard {

		public static Boolean ENABLED;
		public static List<String> BYPASS_REGION;

		private static void init() {

			setPathPrefix("WorldGuard");

			ENABLED = getBoolean("Enabled");
			BYPASS_REGION = getStringList("Bypass_Region");

		}

	}

	public final static class SpawningEvent {

		public static Integer HOW_MANY_STRIKE;
		public static Boolean LIGHTNING_DEAL_DAMAGE;

		public static Integer HOW_MANY_ZOMBIE;
		public static String ZOMBIE_NAME;
		public static Boolean IGNORED_COMBUST;
		public static Double ZOMBIE_HEALTH;
		public static Double ZOMBIE_WALK_SPEED;
		public static Double ZOMBIE_ATTACK_DAMAGE;

		public static Integer HOW_MANY_TNT;
		public static Integer RANDOM_RADIUS;
		public static Boolean DESTROY_BLOCK;
		public static Boolean BOSS_BAR_ENABLED;
		public static Integer BOSS_BAR_WARNING_RANGE;
		public static String BOSS_BAR_TEXT;
		public static Integer BOSS_BAR_TIMED;
		public static Integer RADIATION_RANGE;
		public static Double RADIATION_DPS;
		public static Integer RADIATION_TIME;

		private static void init() {
			setPathPrefix("Airdrop.Spawning_Event");

			HOW_MANY_STRIKE = getInteger("Lightning_Strike.How_Many_Strike");
			LIGHTNING_DEAL_DAMAGE = getBoolean("Lightning_Strike.Deal_Damage");

			HOW_MANY_ZOMBIE = getInteger("Spawn_Zombie.How_Many_Zombie");
			ZOMBIE_NAME = getString("Spawn_Zombie.Custom_Name");
			IGNORED_COMBUST = getBoolean("Spawn_Zombie.Ignore_Combust");
			ZOMBIE_HEALTH = getDouble("Spawn_Zombie.Health");
			ZOMBIE_WALK_SPEED = getDouble("Spawn_Zombie.Walk_Speed");
			ZOMBIE_ATTACK_DAMAGE = getDouble("Spawn_Zombie.Attack_Damage");

			HOW_MANY_TNT = getInteger("Airstrike.How_Many_Tnt");
			RANDOM_RADIUS = getInteger("Airstrike.Random_Radius");
			DESTROY_BLOCK = getBoolean("Airstrike.Destroy_Block");

			BOSS_BAR_ENABLED = getBoolean("Airstrike.Warning_Boss_Bar.Enabled");
			BOSS_BAR_WARNING_RANGE = getInteger("Airstrike.Warning_Boss_Bar.Range");
			BOSS_BAR_TEXT = getString("Airstrike.Warning_Boss_Bar.Text");
			BOSS_BAR_TIMED = getInteger("Airstrike.Warning_Boss_Bar.Seconds");

			RADIATION_RANGE = getInteger("Radiation.Radiation_Range");
			RADIATION_DPS = getDouble("Radiation.Damage_Per_Second");
			RADIATION_TIME = getInteger("Radiation.Seconds");

		}

	}
}
