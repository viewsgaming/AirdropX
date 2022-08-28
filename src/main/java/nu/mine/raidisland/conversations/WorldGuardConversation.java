package nu.mine.raidisland.conversations;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.SimpleTime;

public class WorldGuardConversation extends SimplePrompt {

	private final Airdrop airdrop;

	public WorldGuardConversation(Airdrop airdrop) {
		super(true);

		this.airdrop = airdrop;
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the world guard's region name. If you want to disable type \"null\". &cType \"Exit\" to end conversation.";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		Player player = getPlayer(conversationContext).getPlayer();

		if (s.equalsIgnoreCase("null")) {
			airdrop.setRegionByWorldGuard(null);
			airdrop.setWorldGuardSetting(false);
			return END_OF_CONVERSATION;
		}

		if (HookManager.getRegion(s) == null) {
			Messenger.error(player , "&cThere are no region name &f" + s);
			return END_OF_CONVERSATION;
		}

		airdrop.setRegionByWorldGuard(s);
		airdrop.setWorldGuardSetting(true);
		airdrop.setCenter(HookManager.getRegion(s).getCenter());

		tell(Core.PREFIX + " &aYou're setting the region for &f" + airdrop.getName() + "&a to " + s);

		return END_OF_CONVERSATION;
	}
}
