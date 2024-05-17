package nu.mine.raidisland.conversations.randomspawn;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.DataSaver;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.model.SimpleTime;

public class DelayConversation extends SimplePrompt {

	public DelayConversation() {
		super(true);
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the delay you want to set. Ex: 5 seconds , 5 minutes , 1 hours. &cType \"Exit\" to end conversation.";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		Player player = getPlayer(conversationContext).getPlayer();

		try {
			DataSaver.getInstance().setDelay(SimpleTime.from(s));
		} catch (IllegalArgumentException ex) {
			Messenger.error(player , "Must define date type! Example: '5 seconds, 5 minutes , 5 hours' (Got '{0}')".replace("{0}", s));
			return END_OF_CONVERSATION;
		}

		tell(Core.PREFIX + " &aYou're set delay&a to " + s);

		return END_OF_CONVERSATION;
	}
}
