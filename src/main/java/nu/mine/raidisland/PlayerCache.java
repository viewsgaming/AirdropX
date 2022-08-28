package nu.mine.raidisland;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import lombok.Setter;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.entity.Player;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.remain.Remain;
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

	/**
	 * Clear the entire cache map
	 */
	public static void clearCaches() {
		synchronized (cacheMap) {
			cacheMap.clear();
		}
	}
}
