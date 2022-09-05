package nu.mine.raidisland.commands;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.*;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.List;


public class CallAtPlayerCommand extends SimpleSubCommand {

	protected CallAtPlayerCommand(SimpleCommandGroup parent) {
		super(parent, "callatplayer");
		setMinArguments(1);
		setUsage("<airdrop> [player]");
		setPermission("AirdropX.callatplayer");
	}

	@Override
	protected void onCommand() {

		checkConsole();
		Airdrop airdrop = Airdrop.findAirdrop(args[0]);

		checkNotNull(airdrop, "Couldn't find airdrop '{0}'. Available: " + (Airdrop.getAirdropsNames().isEmpty() ? "-" : Common.join(Airdrop.getAirdropsNames())));

		if (args.length == 2) {
			Player targetPlayer = findPlayer(args[1], "Specify player doesn't exist.");

			callAt(airdrop, targetPlayer.getLocation());

		} else {

			tellNoPrefix(Core.PREFIX + " &cPlease specify a player name.");
		}

	}

	private void callAt(Airdrop airdrop, final Location destination) {
		if (airdrop.getItemsList().isEmpty()) return;

		final Location chestLocation = destination.clone();
		final Block block = chestLocation.getBlock();
		chestLocation.getBlock().setType(Material.CHEST);
		final Chest chest = (Chest) block.getState();
		final Inventory chestInv = chest.getInventory();
		chestInv.setMaxStackSize(9 * 6);
		CompMetadata.setMetadata(chest, Airdrop.NBT_TAG, airdrop.getName());


		for (int items = 0; items < airdrop.getItemsList().size(); items++) {

			boolean hasEmptySlot = false;
			int filledSlot = 0;

			for (ItemStack stack : chestInv.getContents()) {
				if (stack == null) {
					hasEmptySlot = true;
					break;
				} else {
					filledSlot++;
				}
			}

			try {
				if (airdrop.getItemsList().get(items).getKey() == null) continue;
				if (airdrop.getItemsList().get(items).getValue() == null) continue;
			} catch (NullPointerException ex) {
				continue;
			}

			if (hasEmptySlot && filledSlot < airdrop.getMaximumItems()) {
				if (RandomUtil.chanceD(airdrop.getItemsList().get(items).getValue())) {
					chestInv.addItem(airdrop.getItemsList().get(items).getKey());
				}
			} else {
				break;
			}
		}

		for (int i = 1; i <= 3; i++) {
			chest.getWorld().strikeLightningEffect(chest.getLocation());
		}
		CompSound.ENTITY_LIGHTNING_BOLT_THUNDER.play(chest.getLocation());
		CompParticle.EXPLOSION_HUGE.spawn(chest.getLocation().clone().add(0, 2, 0));

		String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Location"), "airdrop_name", airdrop.getName(), "location", Common.shortLocation(chest.getLocation()), "prefix", Core.PREFIX);
		Common.broadcast(broadcast);

		Airdrop.addActiveTask(airdrop, chest, airdrop.getDestroyTime().getTimeTicks());

	}

	@Override
	protected List<String> tabComplete() {

		switch (args.length) {
			case 1:
				return completeLastWord(Airdrop.getAirdropsNames());
			case 2:
				return completeLastWordPlayerNames();
		}

		return NO_COMPLETE;
	}
}
