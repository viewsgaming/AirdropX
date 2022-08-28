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
import org.mineacademy.fo.model.SimpleTime;

public class DestroyTimeConversation extends SimplePrompt {

	private final Airdrop airdrop;

	public DestroyTimeConversation(Airdrop airdrop) {
		super(true);

		this.airdrop = airdrop;
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the times you want to auto destroy. Ex: 5 seconds , 5 minutes. &cType \"Exit\" to end conversation.";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		Player player = getPlayer(conversationContext).getPlayer();

		try {
			airdrop.setDestroyTime(SimpleTime.from(s));
		} catch (IllegalArgumentException ex) {
			Messenger.error(player , "Must define date type! Example: '5 seconds, 5 minutes , 5 hours' (Got '{0}')".replace("{0}", s));
			return END_OF_CONVERSATION;
		}


		tell(Core.PREFIX + " &aYou're changing auto destroy time for &f" + airdrop.getName() + "&a to " + s);

		return END_OF_CONVERSATION;
	}
}
