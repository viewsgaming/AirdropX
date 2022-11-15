package nu.mine.raidisland.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.SimpleExpansion;

@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Placeholder extends SimpleExpansion {

	@Getter(value = AccessLevel.PRIVATE)
	private static final Placeholder instance = new Placeholder();

	@Override
	protected String onReplace(@NonNull CommandSender sender, String identifier) {


		return NO_REPLACE;
	}


}
