package nu.mine.raidisland.menus;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.conversations.AutoSpawnTimeConversation;
import nu.mine.raidisland.conversations.RandomRangeSetupConversation;
import nu.mine.raidisland.conversations.RequirementPlayerConversation;
import nu.mine.raidisland.conversations.WorldGuardConversation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMaterial;

public class AutoDropSettingsMenu extends Menu {


	private final ButtonConversation worldGuardRegionSetting;
	private final Button.DummyButton worldGuardNotLoaded;
	@Position(9 * 1 + 2)
	private final Button setCenterButton;
	@Position(9 * 1 + 3)
	private final ButtonConversation setRandomRangeButton;
	@Position(9 * 1 + 4)
	private final ButtonConversation timeButton;

	@Position(9 * 1 + 5)
	private final ButtonConversation RequirementConnectedPlayersButton;

	@Position(8)
	private final Button setAutoSpawnButton;

	@Position(9 * 1 + 7)
	private final Button startOrStopButton;




	public AutoDropSettingsMenu(Airdrop airdrop) {

		setSize(9 * 3);
		setTitle("&eAuto drop settings.");


		setAutoSpawnButton = new Button() {

			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {

				boolean isReadyToStart = airdrop.isReadyToStart();

				if (isReadyToStart) {
					if (airdrop.isAutoStartSpawn()) {
						airdrop.setAutoStartSpawn(false);
						restartMenu("&CDisabled!!");
					} else {
						airdrop.setAutoStartSpawn(true);
						restartMenu("&aEnabled!!");
					}
				} else {
					restartMenu("&cPlease setup all the settings before start.");
				}


			}

			@Override
			public ItemStack getItem() {
				boolean isEnabled = airdrop.isAutoStartSpawn();
				return isEnabled ? ItemCreator.of(CompMaterial.LEVER,
						"&c&lAuto spawn when server start.",
						"",
						"Current: &a&lEnabled").make()
						:
						ItemCreator.of(CompMaterial.LEVER,
								"&c&lAuto spawn when server start.",
								"",
								"Current: &c&lDisabled").make();
			}
		};

		startOrStopButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				boolean isStart = Core.containsAirdrop(airdrop);
				boolean isReadyToStart = airdrop.isReadyToStart();

				if (!isStart) {
					if (isReadyToStart) {
						if (airdrop.isWorldGuardSetting()) {
							if (HookManager.getRegion(airdrop.getRegionByWorldGuard()) == null) {
								tellError("&cThere are no region name &f" + airdrop.getRegionByWorldGuard());
								animateTitle("&4Cannot start the task.");
								return;
							}
							Core.startAutoSpawn(airdrop);
							tellSuccess("&aYou have been lunched &f" + airdrop.getName() + "&a auto spawning task.");
							restartMenu("&aLunched!!");
						} else {
							Core.startAutoSpawn(airdrop);
							tellSuccess("&aYou have been lunched &f" + airdrop.getName() + "&a auto spawning task.");
							restartMenu("&aLunched!!");
						}
					} else {
						animateTitle("&4Please config all of your setting");
						tellError("Please make sure your auto spawning settings is all setting and correct.");
					}
				} else {
					Core.stopAutoSpawn(airdrop);
					tellSuccess("&cYou have been stopped &f" + airdrop.getName() + "&c auto spawning task.");
					restartMenu("&cStopped!!");
				}
			}

			@Override
			public ItemStack getItem() {
				boolean isStart = Core.containsAirdrop(airdrop);
				return isStart ? ItemCreator.of(CompMaterial.RED_WOOL,
						"&c&lSTOP",
						"",
						"Stop the auto spawning task").make()
						:
						ItemCreator.of(CompMaterial.GREEN_WOOL,
						"&a&lSTART",
						"",
						"Start the auto spawning task").make();
			}
		};

		timeButton = new ButtonConversation(new AutoSpawnTimeConversation(airdrop), ItemCreator.of(CompMaterial.CLOCK,
				"&cAuto Spawn Timer",
				"",
				"Setting auto spawn timer",
				"for " + airdrop.getName() + " airdrop",
				"",
				"Current: " + airdrop.getAutoSpawnTime()
		));

		setRandomRangeButton = new ButtonConversation(new RandomRangeSetupConversation(airdrop), ItemCreator.of(CompMaterial.DIRT_PATH,
				"&cSet Random Range",
				"",
				"Set random range where airdrop",
				"going to spawn by random",
				"based on you random range setup.",
				"",
				"&4Warning:&c Large numbers may cause",
				"&cserver freeze for a while.",
				"",
				"Current: " + airdrop.getRandomSpawnRange()));

		setCenterButton = new Button() {

			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {

				if (airdrop.isWorldGuardSetting()) {
					tellError("You're enable world guard region now. If you want to switch please disable world guard region first.");
					animateTitle("&4You're enable world guard region now. Disable it first.");
					return;
				}

				PlayerCache cache = PlayerCache.from(player);

				cache.setSelectedAirdrop(airdrop);
				cache.setDoingSetting(true);
				player.closeInventory();

				tellInfo("Please right-click on the block which you want it to be the center location.");
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(CompMaterial.WOODEN_AXE,
								"&cSet center location",
								"",
								"Setting your center of location",
								"where airdrop going to random spawn.",
								"",
								"&4Note:&f It's based on range you setup.",
								"",
								"Current: " + Common.shortLocation(airdrop.getCenter()))
						.make();
			}
		};

		worldGuardRegionSetting = new ButtonConversation(new WorldGuardConversation(airdrop), ItemCreator.of(CompMaterial.GRASS_BLOCK,
				"&cSet World Guard Region",
				"",
				"Set world guard region",
				"where airdrop going to spawn",
				"inside the region that you setting.",
				"Basically airdrop will spawn",
				"based on center location of",
				"world guard region and",
				"range you setup. ",
				"",
				"&4Requirement:&c World Guard",
				"",
				"&4Warning:&c If you want to",
				"&cdisable it type \"null\"",
				"",
				"Current: " + airdrop.getRegionByWorldGuard()));

		RequirementConnectedPlayersButton = new ButtonConversation(new RequirementPlayerConversation(airdrop) , CompMaterial.PLAYER_HEAD,
				"&cRequirement connected player to spawn airdrop.",
				"",
				"Set the requirement of connected",
				"player to spawn airdrop.",
				"If player less than",
				"your setup it won't spawn.",
				"",
				"Current: " + airdrop.getRequirementConnectedPlayers());

		worldGuardNotLoaded = Button.DummyButton.makeDummy(ItemCreator.of(CompMaterial.GRASS_BLOCK, "&cSet World Guard Region",
				"",
				"&4Please install World Guard",
				"&4to use this function."));




	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 9 * 1 + 1) {
			boolean isWorldGuardLoaded = HookManager.isWorldGuardLoaded();
			if (isWorldGuardLoaded) {
				return worldGuardRegionSetting.getItem();
			} else {
				return worldGuardNotLoaded.getItem();
			}
		}

		return CompMaterial.GRAY_STAINED_GLASS_PANE.toItem();
	}

	@Override
	public Menu newInstance() {
		PlayerCache cache = PlayerCache.from(getViewer());
		return new AutoDropSettingsMenu(cache.getSelectedAirdrop());
	}
}
