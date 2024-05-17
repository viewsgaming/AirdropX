package nu.mine.raidisland.menus;

import nu.mine.raidisland.PlayerCache;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.conversations.SetMaxItemsConversation;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.collection.StrictMap;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuContainerChances;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

public class AirdropEditor extends MenuContainerChances {

	private final Airdrop airdrop;

	@Position(53)
	private final ButtonMenu settingsMenu;

	@Position(51)
	private final ButtonConversation setMaximumConversation;

	public AirdropEditor(Airdrop airdrop) {
		super(null);

		this.airdrop = airdrop;
		setTitle("Place items here.");
		setSize(9 * 6);

		setMaximumConversation = new ButtonConversation(new SetMaxItemsConversation(airdrop) , ItemCreator.of(CompMaterial.ANVIL ,
				"Set maximum items.",
				"",
				"Set maximum items for the" ,
				"airdrop can have."));

		settingsMenu = new ButtonMenu(new SettingsMenu(airdrop), ItemCreator.of(CompMaterial.REPEATER,
				"&fSettings",
				"",
				"Settings your preference",
				"for &f" + airdrop.getName()));
	}

	@Override
	protected boolean canEditItem(MenuClickLocation location, int slot, ItemStack clicked, ItemStack cursor, InventoryAction action) {
		return true;
	}

	@Override
	protected ItemStack getDropAt(int slot) {

		final Tuple<ItemStack, Double> tuple = this.getTuple(slot);

		return tuple != null ? tuple.getKey() : NO_ITEM;
	}

	@Override
	protected double getDropChance(int slot) {
		final Tuple<ItemStack, Double> tuple = this.getTuple(slot);
		return tuple != null ? tuple.getValue() : 0;
	}

	private Tuple<ItemStack , Double> getTuple(final int slot) {
		final List<Tuple<ItemStack, Double>> items = airdrop.getItemsList();

		return slot < items.size() ? items.get(slot) : null;
	}

	@Override
	protected void onMenuClose(StrictMap<Integer, Tuple<ItemStack, Double>> items) {
		airdrop.setItemsList(new ArrayList<>(items.values()));
	}

	@Override
	protected String[] getInfo() {
		return new String[] {
			"",
			"This menu allows you to create",
			"loots for &f" + airdrop.getName() + "&7.",
			"",
			"Simply &2drag and drop&7 items",
			"from your inventory here and",
			"&cdo not forgot to set drop percentage."
		};
	}

	@Override
	public boolean allowDecimalQuantities() {
		return true;
	}

	@Override
	public Menu newInstance() {
		return new AirdropEditor(airdrop);
	}

}
