package nu.mine.raidisland.commands;

import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.models.Holograms;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.*;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.List;


public class CallAtPlayerCommand extends SimpleSubCommand {

	protected CallAtPlayerCommand(SimpleCommandGroup parent) {
		super(parent, "callatplayer");
		setMinArguments(1);
		setUsage("<airdrop> [player]");
		setPermission("AirdropX.callatplayer");
	}

	@Override
	protected void onCommand() {

		checkConsole();
		Airdrop airdrop = Airdrop.findAirdrop(args[0]);

		checkNotNull(airdrop, "Couldn't find airdrop '{0}'. Available: " + (Airdrop.getAirdropsNames().isEmpty() ? "-" : Common.join(Airdrop.getAirdropsNames())));

		if (args.length == 2) {
			Player targetPlayer = findPlayer(args[1], "Specify player doesn't exist.");

			AirdropUtil.callAt(airdrop, targetPlayer.getLocation());

		} else {

			tellNoPrefix(Core.PREFIX + " &cPlease specify a player name.");
		}
	}

	@Override
	protected List<String> tabComplete() {

		switch (args.length) {
			case 1:
				return completeLastWord(Airdrop.getAirdropsNames());
			case 2:
				return completeLastWordPlayerNames();
		}

		return NO_COMPLETE;
	}
}
