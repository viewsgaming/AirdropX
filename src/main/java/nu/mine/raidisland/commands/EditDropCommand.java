package nu.mine.raidisland.commands;

import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.menus.AirdropEditor;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public class EditDropCommand extends SimpleSubCommand {

	protected EditDropCommand(SimpleCommandGroup parent) {
		super(parent, "edit");

		setMinArguments(1);
		setUsage("<name>");
		setPermission("AirdropX.edit");
		setDescription("Customize your airdrop.");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		Airdrop airdrop = Airdrop.findAirdrop(args[0]);

		checkNotNull(airdrop , "Couldn't find airdrop '{0}'. Available: " + Common.join(Airdrop.getAirdropsNames()));

		PlayerCache.from(getPlayer()).setSelectedAirdrop(airdrop);

		new AirdropEditor(airdrop).displayTo(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return completeLastWord(Airdrop.getAirdropsNames());
	}
}
