package nu.mine.raidisland.commands;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class AirdropCommand extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new CreateAirdropCommand(this));
		registerSubcommand(new EditDropCommand(this));
		registerSubcommand(new CallAirdropCommand(this));
		registerSubcommand(new RemoveAirdropCommand(this));
		registerSubcommand(new AirdropListCommand(this));
		registerSubcommand(new CallAtPlayerCommand(this));
		registerSubcommand(new CrashFixerCommand(this));
		registerSubcommand(new AirdropCompassCommand(this));
		registerSubcommand(new RandomSpawnCommand(this));
		registerSubcommand(new ReloadCommand());
	}

	@Override
	protected String getCredits() {
		return "&fAirdropX&7 has been developing since 2022";
	}
}
