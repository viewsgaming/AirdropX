package nu.mine.raidisland;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import lombok.Setter;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.Lang;
import org.mineacademy.fo.settings.YamlConfig;

import lombok.Getter;

@Getter
public final class PlayerCache extends YamlConfig {

	private static volatile Map<UUID, PlayerCache> cacheMap = new HashMap<>();

	private final UUID uniqueId;
	private final String playerName;

	// This won't save into file
	@Setter
	private Airdrop selectedAirdrop;
	@Setter
	private boolean isDoingSetting;

	private static final Map<UUID , Long> cooldownMap = new HashMap<>();
	// -------------------------

	private PlayerCache(String playerName , UUID uniqueId) {
		this.uniqueId = uniqueId;
		this.playerName = playerName;

		this.setPathPrefix("Players." + uniqueId.toString());
		this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
	}

	@Override
	protected void onLoad() {

	}

	@Override
	public void onSave() {

	}

	/**
	 * Return player from cache if online or null otherwise
	 *
	 * @return
	 */
	@Nullable
	public Player toPlayer() {
		final Player player = Remain.getPlayerByUUID(this.uniqueId);

		return player != null && player.isOnline() ? player : null;
	}

	/**
	 * Remove this cached data from memory if it exists
	 */
	public void removeFromMemory() {
		synchronized (cacheMap) {
			cacheMap.remove(this.uniqueId);
		}
	}

	@Override
	public String toString() {
		return "PlayerCache{" + this.playerName + ", " + this.uniqueId + "}";
	}

	/* ------------------------------------------------------------------------------- */
	/* Static access */
	/* ------------------------------------------------------------------------------- */

	/**
	 * Return or create new player cache for the given player
	 *
	 * @param player
	 * @return
	 */
	public static PlayerCache from(Player player) {
		synchronized (cacheMap) {
			final UUID uniqueId = player.getUniqueId();
			final String playerName = player.getName();

			PlayerCache cache = cacheMap.get(uniqueId);

			if (cache == null) {
				cache = new PlayerCache(playerName, uniqueId);

				cacheMap.put(uniqueId, cache);
			}

			return cache;
		}
	}

	public static boolean canAddCooldown(Player player) {

		if (cooldownMap.containsKey(player.getUniqueId())) {
			if (cooldownMap.get(player.getUniqueId()) > System.currentTimeMillis()) {
				long timeLeft = (cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
				String formatMessage = Replacer.replaceArray(Lang.of("Player_Is_In_Cooldown"), "seconds_plural" , Common.plural(timeLeft , "second"));
				Common.tellNoPrefix(player , formatMessage);
				return false;
			}
		}

		return true;
	}

	public static void addCooldown(Player player) {
		cooldownMap.put(player.getUniqueId() , System.currentTimeMillis() + (Settings.Airdrop.DELAY_BETWEEN_EACH_UNBOXING * 1000L));
	}


	/**
	 * Clear the entire cache map
	 */
	public static void clearCaches() {
		synchronized (cacheMap) {
			cacheMap.clear();
		}
	}
}
