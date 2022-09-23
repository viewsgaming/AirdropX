package nu.mine.raidisland.conversations;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimplePrompt;

public class RequirementPlayerConversation extends SimplePrompt {

	private final Airdrop airdrop;

	public RequirementPlayerConversation(Airdrop airdrop) {
		super(true);

		this.airdrop = airdrop;
	}

	@Override
	protected String getPrompt(ConversationContext context) {
		return "&eWrite the requirement players to spawn airdrop between 0 - 1000. &cType \"Exit\" to end conversation.";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input) {
		if (!Valid.isInteger(input))
			return false;

		final int range = Integer.parseInt(input);

		return range >= 0 && range <= 100;
	}

	@Override
	protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
		return "Invalid value: '" + invalidInput + "'. Only enter whole numbers";
	}

	@Nullable
	@Override
	protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

		final int requirement = Integer.parseInt(s);

		airdrop.setRequirementConnectedPlayers(requirement);

		tell(Core.PREFIX + " &aYou're set requirement players for &f" + airdrop.getName() + "&a to " + s);

		return END_OF_CONVERSATION;
	}
}
