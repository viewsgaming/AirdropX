package nu.mine.raidisland.conversations;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.conversation.SimplePrompt;

public class CommandToExecuteConversation extends SimplePrompt {

	private final Airdrop airdrop;

	public CommandToExecuteConversation(Airdrop airdrop) {
		super(true);

		this.airdrop = airdrop;
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the command without \"/\". &cType \"Exit\" to end conversation.";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		return !input.contains("/");
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "Invalid range: '" + invalidInput + "'. Please specific command without '/'";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		airdrop.setCommandToExecute(s);

		tell(Core.PREFIX + " &aYou're changing command for &f" + airdrop.getName() + "&a to " + s);

		return END_OF_CONVERSATION;
	}
}
