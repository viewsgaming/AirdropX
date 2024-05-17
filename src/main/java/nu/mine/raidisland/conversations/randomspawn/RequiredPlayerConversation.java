package nu.mine.raidisland.conversations.randomspawn;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import nu.mine.raidisland.settings.DataSaver;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimplePrompt;

public class RequiredPlayerConversation extends SimplePrompt {

	public RequiredPlayerConversation() {
		super(true);
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the requirement players to spawn random airdrop between 0 - 1000. &cType \"Exit\" to end conversation.";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		if (!Valid.isInteger(input))
			return false;

		final int range = Integer.parseInt(input);

		return range >= 0 && range <= 1000;
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "Invalid value: '" + invalidInput + "'. Only enter whole numbers";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		final int requirement = Integer.parseInt(s);

		DataSaver.getInstance().setRequiredPlayer(requirement);

		tell(Core.PREFIX + " &aYou're set requirement players&a to " + s);

		return END_OF_CONVERSATION;
	}
}
