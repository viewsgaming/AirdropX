package nu.mine.raidisland.menus;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.conversations.randomspawn.DelayConversation;
import nu.mine.raidisland.conversations.randomspawn.RequiredPlayerConversation;
import nu.mine.raidisland.settings.DataSaver;
import nu.mine.raidisland.tasks.RandomSpawnTask;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class RandomSpawnMenu extends Menu {

	@Position(9 * 1 + 1)
	private final ButtonConversation delayButton;

	@Position(9 * 1 + 2)
	private final ButtonConversation requiredPlayer;

	@Position(9 * 1 + 3)
	private final ButtonMenu selectedAirdrop;

	@Position(9 * 1 + 4)
	private final Button centerButton;

	@Position(9 * 1 + 7)
	private final Button startButton;


	public RandomSpawnMenu() {


		setSize(9 * 3);
		setTitle("Random Spawn Setup");

		delayButton = new ButtonConversation(new DelayConversation(),
				ItemCreator.of(CompMaterial.CLOCK,
						"&7Delay",
						"",
						"Setup the delay for",
						"each random spawn",
						"",
						"Current: " + DataSaver.getInstance().getDelay()));

		requiredPlayer = new ButtonConversation(new RequiredPlayerConversation(),
				ItemCreator.of(CompMaterial.PLAYER_HEAD,
						"&cPlayer Requirement",
						"",
						"Setup the player requirement",
						"for each random spawn",
						"",
						"Current: " + DataSaver.getInstance().getRequiredPlayer()));

		selectedAirdrop = new ButtonMenu(new AirdropContainer(),
				ItemCreator.of(CompMaterial.CHEST,
						"&aSelect Airdrops",
						"",
						"Select airdrops that will",
						"be able to random spawn"));

		centerButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {

				DataSaver.getInstance().setCenterLocaiton(player.getLocation());
				restartMenu();
			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(CompMaterial.MAP,
						"&eCenter Location",
						"",
						"Setup the center location",
						"it is based on your current location.",
						"",
						"&cThe spawning range is based on each airdrop",
						"",
						"Current: " + Common.shortLocation(DataSaver.getInstance().getCenterLocaiton())).make();
			}
		};

		startButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {

				DataSaver saver = DataSaver.getInstance();

				if (!saver.isEnabled()) {

					if (saver.getDelay() == null) {
						Messenger.error(player, "Please setup the delay before start!");
						return;
					}

					if (saver.getRequiredPlayer() < 0) {
						Messenger.error(player, "Please setup the requirement before start!");
						return;
					}

					if (saver.getAirdropName() == null || saver.getAirdropName().isEmpty()) {
						Messenger.error(player, "Please select airdrops before start!");
						return;
					}

					if (saver.getCenterLocaiton() == null) {
						Messenger.error(player, "Please setup the center location before start!");
						return;
					}

					BukkitTask spawningTask = new RandomSpawnTask().runTaskTimerAsynchronously(Core.getInstance(), 0,DataSaver.getInstance().getDelay().getTimeTicks());

					DataSaver.getInstance().setRandomSpawningTask(spawningTask);

					DataSaver.getInstance().setEnabled(true);

					restartMenu("&aEnabled");

				} else {

					stopTask(DataSaver.getInstance().getRandomSpawningTask());
					DataSaver.getInstance().setEnabled(false);
					restartMenu("&cDisabled");

				}




			}

			@Override
			public ItemStack getItem() {

				boolean isEnabled = DataSaver.getInstance().isEnabled();

				return ItemCreator.of(isEnabled
								? CompMaterial.LIME_WOOL
								: CompMaterial.RED_WOOL,
						isEnabled ? "&a&lENABLED" : "&c&lDISABLED").make();
			}
		};

	}

	@Override
	public ItemStack getItemAt(int slot) {
		return CompMaterial.GRAY_STAINED_GLASS_PANE.toItem();
	}

	private final class AirdropContainer extends MenuPagged<String> {

		public AirdropContainer() {
			super(RandomSpawnMenu.this, Airdrop.getAirdropsNames());

		}

		@Override
		protected ItemStack convertToItemStack(String item) {
			return ItemCreator.of(CompMaterial.CHEST, item , ""
					, "&c&l<<&f LEFT-CLICK TO ADD",
					"&fRIGHT-CLICK TO REMOVE &c&l>>",
					"",
					DataSaver.getInstance().getAirdropsNames().contains(item) ? "&aStatus:&f Selected" : "&aStatus: &fUnselected").make();
		}

		@Override
		protected void onPageClick(Player player, String item, ClickType click) {

			if (click.isLeftClick()) {
				if (!DataSaver.getInstance().getAirdropsNames().contains(item))
					DataSaver.getInstance().addAirdropName(item);

				restartMenu("&aAdded!");
			}

			if (click.isRightClick()) {
				DataSaver.getInstance().removeAirdropName(item);
				restartMenu("&cRemoved!");
			}
		}
	}

	public void stopTask(BukkitTask task) {

		if (task != null) {
			try {
				task.cancel();
			} catch (IllegalStateException ignored) {
				Common.log("Something wrong.. X_x");
			}
		}
	}
}
