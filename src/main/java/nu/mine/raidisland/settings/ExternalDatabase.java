package nu.mine.raidisland.settings;

import lombok.Getter;
import nu.mine.raidisland.PlayerCache;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleFlatDatabase;

public class ExternalDatabase extends SimpleFlatDatabase<PlayerCache> {

	@Getter
	private static final ExternalDatabase instance = new ExternalDatabase();

	public ExternalDatabase() {
		this.addVariable("table" , "AirdropX");
	}

	@Override
	protected void onLoad(SerializedMap map, PlayerCache data) {

	}

	@Override
	protected SerializedMap onSave(PlayerCache data) {
		return null;
	}


}
