package nu.mine.raidisland.commands;

import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.models.Holograms;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.debug.LagCatcher;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.Lang;
import java.util.ArrayList;

import java.util.List;


public class CallAirdropCommand extends SimpleSubCommand {

	protected CallAirdropCommand(SimpleCommandGroup parent) {
		super(parent, "call");
		setMinArguments(1);
		setUsage("<airdrop> [x] [y] [z]");
		setPermission("AirdropX.call");
	}

	@Override
	protected void onCommand() {

		checkConsole();
		Airdrop airdrop = Airdrop.findAirdrop(args[0]);
		Player player = getPlayer();

		checkNotNull(airdrop , "Couldn't find airdrop '{0}'. Available: " + (Airdrop.getAirdropsNames().isEmpty() ? "-" : Common.join(Airdrop.getAirdropsNames())));

		if (airdrop.isRandomLocation()) {
			final Location randomLocation = AirdropUtil.findRandomLocation(player.getLocation(), airdrop.getRange());
			this.checkNotNull(randomLocation , "Could not find any suitable location, try again later.");

			if (randomLocation.getWorld().isChunkLoaded(randomLocation.getBlockX() / 16 , randomLocation.getBlockZ() /16 ))
				randomLocation.getChunk().load(true);

			randomLocation.setYaw(0);
			randomLocation.setPitch(90);

			AirdropUtil.callAt(airdrop , randomLocation);

		} else {

			if (args.length == 5) {
				final World world = findWorld(args[1]);
				final int x = findNumber(2 , "Please check your X position.");
				final int y = findNumber(3 , "Please check your Y position.");
				final int z = findNumber(4 , "Please check your Z position.");

				Location spawnLocation = new Location(world , x , y , z);
				AirdropUtil.callAt(airdrop , spawnLocation);
			} else {

				tellNoPrefix(Core.PREFIX + " &cPlease specify a location.");
			}

		}
	}

	@Override
	protected List<String> tabComplete() {
		if (!isPlayer())
			return new ArrayList<>();

		final Player player = (Player) sender;

		switch (args.length) {
			case 1:
				return completeLastWord(Airdrop.getAirdropsNames());
			case 2:
				return completeLastWordWorldNames();
			case 3:
				return completeLastWord(player.getLocation().getBlockX());
			case 4:
				return completeLastWord(player.getLocation().getBlockY());
			case 5:
				return completeLastWord(player.getLocation().getBlockZ());
		}

		return NO_COMPLETE;
	}
}
