package nu.mine.raidisland.conversations;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimplePrompt;

public class SetMaxItemsConversation extends SimplePrompt {

	private final Airdrop airdrop;

	public SetMaxItemsConversation(Airdrop airdrop) {
		super(true);

		this.airdrop = airdrop;
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the maximum items airdrop can have between 1 - 27. &cType \"Exit\" to end conversation.";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		if (!Valid.isInteger(input))
			return false;

		final int range = Integer.parseInt(input);

		return range >= 1 && range <= 27;
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "Invalid value: '" + invalidInput + "'. Only enter whole numbers";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		final int maximum = Integer.parseInt(s);

		airdrop.setMaximumItems(maximum);

		tell(Core.PREFIX + " &aYou're changing maximum items for &f" + airdrop.getName() + "&a to " + s);

		return END_OF_CONVERSATION;
	}
}
