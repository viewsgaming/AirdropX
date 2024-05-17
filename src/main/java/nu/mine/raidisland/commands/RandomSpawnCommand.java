package nu.mine.raidisland.commands;

import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.menus.AirdropEditor;
import nu.mine.raidisland.menus.RandomSpawnMenu;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class RandomSpawnCommand extends SimpleSubCommand {

	protected RandomSpawnCommand(SimpleCommandGroup parent) {
		super(parent, "randomspawn");

		setMinArguments(0);
		setPermission("AirdropX.randomspawn");
		setDescription("Automatic random your airdrops choice for schedule spawning.");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		new RandomSpawnMenu().displayTo(getPlayer());
	}
}
