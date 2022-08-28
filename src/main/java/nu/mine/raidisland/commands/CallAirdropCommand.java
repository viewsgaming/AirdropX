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
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.Lang;
import java.util.ArrayList;

import java.util.List;


public class CallAirdropCommand extends SimpleSubCommand {

	protected CallAirdropCommand(SimpleCommandGroup parent) {
		super(parent, "call");
		setMinArguments(1);
		setUsage("<airdrop> [x] [y] [z]");
		setPermission("AirdropX.call");
	}

	@Override
	protected void onCommand() {

		checkConsole();
		Airdrop airdrop = Airdrop.findAirdrop(args[0]);
		Player player = getPlayer();

		checkNotNull(airdrop , "Couldn't find airdrop '{0}'. Available: " + (Airdrop.getAirdropsNames().isEmpty() ? "-" : Common.join(Airdrop.getAirdropsNames())));

		if (airdrop.isRandomLocation()) {
			final Location randomLocation = findRandomLocation(player.getLocation(), airdrop.getRange());
			this.checkNotNull(randomLocation , "Could not find any suitable location, try again later.");

			if (randomLocation.getWorld().isChunkLoaded(randomLocation.getBlockX() / 16 , randomLocation.getBlockZ() /16 ))
				randomLocation.getChunk().load(true);

			randomLocation.setYaw(0);
			randomLocation.setPitch(90);

			callAt(airdrop , randomLocation);

		} else {

			if (args.length == 4) {
				final int x = findNumber(1 , "Please check your X position.");
				final int y = findNumber(2 , "Please check your Y position.");
				final int z = findNumber(3 , "Please check your Z position.");

				Location spawnLocation = new Location(player.getWorld() , x , y , z);
				callAt(airdrop , spawnLocation);
			} else {
				tellNoPrefix(Core.PREFIX + " &cPlease specify a location.");
			}

		}

	}

	private Location findRandomLocation(final Location centerLocation , final int range) {

		for (int i = 0; i < Settings.Airdrop.LOCATION_SEARCH_ATTEMPTS; i++) {
			final Location location = RandomUtil.nextLocation(centerLocation , range , false);
			final int highestPointY = BlockUtil.findHighestBlock(location , material -> !CompMaterial.isLeaves(material));

			if (highestPointY != -1) {
				location.setY(highestPointY);

				final Block block = location.getBlock();
				final Block blockAbove = block.getRelative(BlockFace.UP);
				final Block blockBelow = block.getRelative(BlockFace.DOWN);

				if (blockBelow.getType().isSolid() && CompMaterial.isAir(block) && CompMaterial.isAir(blockAbove))
					return location;

			}
		}

		return null;
	}

	private void callAt(Airdrop airdrop , final Location destination) {
		if (airdrop.getItemsList().isEmpty()) return;

		final Location chestLocation = destination.clone();
		final Block block = chestLocation.getBlock();
		chestLocation.getBlock().setType(Material.CHEST);
		final Chest chest = (Chest) block.getState();
		final Inventory chestInv = chest.getInventory();
		chestInv.setMaxStackSize(9 * 6);
		CompMetadata.setMetadata(chest , Airdrop.NBT_TAG , airdrop.getName());



		for (int items = 0; items < airdrop.getItemsList().size(); items++) {

			boolean hasEmptySlot = false;

			for (ItemStack stack : chestInv.getContents()) {
				if (stack == null) {
					hasEmptySlot = true;
					break;
				}
			}

			try {
				if (airdrop.getItemsList().get(items).getKey() == null) continue;
				if (airdrop.getItemsList().get(items).getValue() == null) continue;
			} catch (NullPointerException ex) {
				continue;
			}

			if (hasEmptySlot) {
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
		CompParticle.EXPLOSION_HUGE.spawn(chest.getLocation().clone().add(0 , 2 , 0));

		String broadcast = Replacer.replaceArray(Lang.of("Broadcast_Location"), "airdrop_name" , airdrop.getName() , "location" , Common.shortLocation(chest.getLocation()), "prefix" , Core.PREFIX);
		Common.broadcast(broadcast);

		Airdrop.addActiveTask(airdrop , chest , airdrop.getDestroyTime().getTimeTicks());

	}

	@Override
	protected List<String> tabComplete() {
		if (!isPlayer())
			return new ArrayList<>();

		final Player player = (Player) sender;

		switch (args.length) {
			case 1:
				return completeLastWord(Airdrop.getAirdropsNames());
			case 2:
				return completeLastWord(player.getLocation().getBlockX());
			case 3:
				return completeLastWord(player.getLocation().getBlockY());
			case 4:
				return completeLastWord(player.getLocation().getBlockZ());
		}

		return NO_COMPLETE;
	}
}
