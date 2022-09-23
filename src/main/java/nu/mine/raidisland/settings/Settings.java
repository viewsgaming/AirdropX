package nu.mine.raidisland.settings;

import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;

public class Settings extends SimpleSettings {

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

		private static void init() {
			setPathPrefix("Airdrop");

			DEFAULT_AUTO_DESTROY_TIME = getTime("Default_Auto_Destroy_Time");
			DEFAULT_RANGE = getInteger("Default_Range");
			DEFAULT_RANDOM_LOCATION = getBoolean("Default_Random_Location");
			BROADCAST_LOCATION_ON_CALL = getBoolean("Broadcast_Location_On_Call");
			LOCATION_SEARCH_ATTEMPTS = getInteger("Location_Search_Attempts");
			DELAY_BETWEEN_EACH_UNBOXING = getInteger("Delay_Between_Each_Unboxing");
			DEFAULT_OPENING_DELAY_TIME = getTime("Default_Opening_Delay_Time");
			DELETE_WHEN_DELAY_IS_END = getTime("Delete_When_Delay_Is_End");
			HOLOGRAMS_LINE = getStringList("Holograms.Main");
			TIMEOUT_HOLO = getString("Holograms.Last_Line.Timeout");
			OPENING_HOLO = getString("Holograms.Last_Line.Opening");
			ADJUSTMENT_X = getDouble("Holograms.Adjustment.X");
			ADJUSTMENT_Y = getDouble("Holograms.Adjustment.Y");
			ADJUSTMENT_Z = getDouble("Holograms.Adjustment.Z");

		}
	}
}
