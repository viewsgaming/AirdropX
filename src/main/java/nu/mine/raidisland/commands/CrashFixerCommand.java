package nu.mine.raidisland.commands;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.settings.DataSaver;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class CrashFixerCommand extends SimpleSubCommand {

	public CrashFixerCommand(SimpleCommandGroup parent) {
		super(parent,"crashfixer");

		setMinArguments(0);
		setPermission("AirdropX.crashfixer");
		setDescription("Delete all glitched airdrops");
	}

	@Override
	protected void onCommand() {
		DataSaver.getInstance().clean();

		tellNoPrefix(Core.PREFIX +  " Every remaining airdrop has been delete!");
	}
}
