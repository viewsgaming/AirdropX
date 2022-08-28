package nu.mine.raidisland.commands;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.checkerframework.checker.units.qual.A;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class RemoveAirdropCommand extends SimpleSubCommand {

	public RemoveAirdropCommand(SimpleCommandGroup parent) {
		super(parent, "remove");

		setMinArguments(1);
		setUsage("<name>");
		setPermission("AirdropX.remove");
		setDescription("Remove a specify airdrop.");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		String name = args[0];
		Airdrop itemToRemove = Airdrop.findAirdrop(name);

		checkNotNull(itemToRemove , "Couldn't find airdrop '{0}'. Available: " + Common.join(Airdrop.getAirdropsNames()));

		Airdrop.removeAirdrop(itemToRemove);

		tellNoPrefix(Core.PREFIX + " &f" + name + "&c has been removed!");
	}

	@Override
	protected List<String> tabComplete() {
		return completeLastWord(Airdrop.getAirdropsNames());
	}
}
