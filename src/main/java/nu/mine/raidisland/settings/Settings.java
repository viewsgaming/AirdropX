package nu.mine.raidisland.settings;

import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.settings.SimpleSettings;

public class Settings extends SimpleSettings {

	@Override
	protected int getConfigVersion() {
		return 1;
	}

	public final static class Airdrop {

		public static SimpleTime DEFAULT_AUTO_DESTROY_TIME;
		public static Integer DEFAULT_RANGE;

		public static Boolean DEFAULT_RANDOM_LOCATION;
		public static Boolean BROADCAST_LOCATION_ON_CALL;
		public static Integer LOCATION_SEARCH_ATTEMPTS;

		private static void init() {
			setPathPrefix("Airdrop");

			DEFAULT_AUTO_DESTROY_TIME = getTime("Default_Auto_Destroy_Time");
			DEFAULT_RANGE = getInteger("Default_Range");
			DEFAULT_RANDOM_LOCATION = getBoolean("Default_Random_Location");
			BROADCAST_LOCATION_ON_CALL = getBoolean("Broadcast_Location_On_Call");
			LOCATION_SEARCH_ATTEMPTS = getInteger("Location_Search_Attempts");
		}
	}
}
