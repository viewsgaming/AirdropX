package nu.mine.raidisland.commands;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.SimpleComponent;

public class CreateAirdropCommand extends SimpleSubCommand {

	public CreateAirdropCommand(SimpleCommandGroup parent) {
		super(parent, "create");

		setMinArguments(1);
		setUsage("<name>");
		setPermission("AirdropX.create");
		setDescription("Create your new airdrop.");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		String name = args[0];

		if (Airdrop.getAirdropsNames().contains(name)) {
			tellError("You cannot create the airdrop with the same name!");
			return;
		}

		Airdrop.createAirdrop(name);

		tellNoPrefix(Core.PREFIX + " &f" + name + "&7 has been created!");


		tellNoPrefix("&8&m&l----------------------------------");
		tellNoPrefix("&f Please edit the airdrop first before call it.");
		SimpleComponent.of("&e                         [⚙]").onClickRunCmd("/airdrop edit " + name).onHover("Edit the airdrop").send(getPlayer());
		tellNoPrefix("&8&m&l----------------------------------");
	}
}
