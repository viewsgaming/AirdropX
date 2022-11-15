package nu.mine.raidisland.commands;

import com.palmergames.bukkit.util.Compass;
import nu.mine.raidisland.AirdropUtil;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.settings.Lang;

import java.util.concurrent.TimeUnit;

public class AirdropCompassCommand extends SimpleSubCommand {

	public AirdropCompassCommand(SimpleCommandGroup parent) {
		super(parent, "compass");

		setPermission("AirdropX.compass");
		setMinArguments(0);
		setDescription("Get the compass which will point to airdrop location.");
		setCooldown(Settings.Commands.COMPASS_COOLDOWN, TimeUnit.SECONDS);
		setCooldownMessage(Lang.of("Commands.Compass_Cooldown"));
	}

	@Override
	protected void onCommand() {

		Player player = getPlayer();

		ItemStack compass = CompMetadata.setMetadata(ItemCreator.of(CompMaterial.COMPASS
				,"&cAirdrop Tracker",
				"",
				"Tracking remaining airdrops",
				"&cOne time use!").make() , "DewareCompass" , "none");

		checkNotNull(AirdropUtil.getAirdropLocation() , "There are no remaining airdrop.");


		CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
		compassMeta.setLodestoneTracked(false);
		compassMeta.setLodestone(AirdropUtil.getAirdropLocation());
		CompMetadata.setMetadata(compass , "DewareCompass" , "nothing");
		compass.setItemMeta(compassMeta);

		player.getInventory().addItem(compass);
	}
}
