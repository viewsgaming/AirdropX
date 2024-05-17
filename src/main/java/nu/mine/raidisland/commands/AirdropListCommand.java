package nu.mine.raidisland.commands;

import nu.mine.raidisland.airdrop.Airdrop;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.SimpleComponent;

public class AirdropListCommand extends SimpleSubCommand {

	protected AirdropListCommand(SimpleCommandGroup parent) {
		super(parent, "listall");

		setMinArguments(0);
		setPermission("AirdropX.listall");
	}

	@Override
	protected void onCommand() {
		tellNoPrefix("&8&m&l----------------------------------");
		tellNoPrefix("&cAvailable: &f" + (Airdrop.getAirdropsNames().isEmpty() ? "-" : Common.join(Airdrop.getAirdropsNames())));
		tellNoPrefix("&8&m&l----------------------------------");
	}
}
