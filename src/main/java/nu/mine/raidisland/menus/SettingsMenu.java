package nu.mine.raidisland.menus;

import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.conversations.CommandToExecuteConversation;
import nu.mine.raidisland.conversations.DestroyTimeConversation;
import nu.mine.raidisland.conversations.RangeSetupConversation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class SettingsMenu extends Menu {

	private final Airdrop airdrop;

	@Position(9 * 1 + 1)
	private final ButtonConversation destroySetting;

	@Position(9 * 1 + 2)
	private final Button randomLocationButton;

	@Position(9 * 1 + 3)
	private final ButtonConversation rangeButton;

	@Position(9 * 1 + 4)
	private final ButtonConversation executeCommandButton;

	@Position(9 * 1 + 5)
	private final ButtonMenu autoSpawnButton;

	public SettingsMenu(Airdrop airdrop) {

		this.airdrop = airdrop;

		setSize(9 * 3);
		setTitle("&c" + airdrop.getName() + "'s settings");

		destroySetting = new ButtonConversation(new DestroyTimeConversation(airdrop), ItemCreator.of(CompMaterial.CLOCK,
						"&cAuto Destroy Timer",
						"",
						"Setting your preference destroy",
						"time for " + airdrop.getName() + " loots",
						"",
						"Current: " + airdrop.getDestroyTime()
		));

		randomLocationButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				if (airdrop.isRandomLocation()) {
					airdrop.setRandomLocation(false);
				} else {
					airdrop.setRandomLocation(true);
				}

				restartMenu("&aYou've been changed random location to &f" + airdrop.isRandomLocation());
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(CompMaterial.COMPASS,
						"&cRandom spawn",
						"",
						"Current: " + airdrop.isRandomLocation())
						.make();
			}
		};

		rangeButton = new ButtonConversation(new RangeSetupConversation(airdrop) ,ItemCreator.of(CompMaterial.GRASS_BLOCK,
				"&cSet Range",
				"",
				"Set range where airdrop",
				"going to spawn based on",
				"your center location.",
				"require random spawn enabled.",
				"&4Warning:&c Large numbers may cause",
				"&cserver freeze for a while.",
				"",
				"Current: " + airdrop.getRange()));

		executeCommandButton = new ButtonConversation(new CommandToExecuteConversation(airdrop) , ItemCreator.of(CompMaterial.COMMAND_BLOCK,
				"&cCommand to execute",
				"",
				"This going to execute",
				"a command as the console",
				"&4Note:&7 using {player}",
				"for instance of player",
				"",
				"Current: " + airdrop.getCommandToExecute()));

		autoSpawnButton = new ButtonMenu(new AutoDropSettingsMenu(airdrop) , ItemCreator.of(CompMaterial.MAP,
				"&cAuto Spawn Setting",
				"",
				"Settings and Start/Stop",
				"your airdrop for",
				"random spawning"));
	}

	@Override
	public ItemStack getItemAt(int slot) {
		return CompMaterial.GRAY_STAINED_GLASS_PANE.toItem();
	}

	@Override
	public Menu newInstance() {
		return new SettingsMenu(PlayerCache.from(getViewer()).getSelectedAirdrop());
	}
}
