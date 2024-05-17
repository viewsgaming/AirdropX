package nu.mine.raidisland.conversations;

import nu.mine.raidisland.Core;
import nu.mine.raidisland.airdrop.Airdrop;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
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

		if (s.equalsIgnoreCase("null")) {
			airdrop.setCommandToExecute("");
		} else {
			airdrop.setCommandToExecute(s);
		}

		return new CommandChance(airdrop);
	}

	private class CommandChance extends SimplePrompt {

		private final Airdrop airdrop;

		private CommandChance(Airdrop airdrop) {
			super(true);

			this.airdrop = airdrop;
		}

		@Override
		protected String getPrompt(ConversationContext context) {
			return "&eWrite the number of chance between 1-100. &cType \"Exit\" to end conversation.";
		}

		@Override
		protected boolean isInputValid(final ConversationContext context, final String input) {
			if (!Valid.isInteger(input))
				return false;

			final int chance = Integer.parseInt(input);

			return chance >= 1 && chance <= 100;
		}

		@Override
		protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
			return "Invalid range: '" + invalidInput + "'. Only enter whole numbers (1 - 100)";
		}

		@Nullable
		@Override
		protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

			final int chance = Integer.parseInt(s);

			airdrop.setChanceToExecuteCommand(chance);

			return END_OF_CONVERSATION;
		}
	}
}
