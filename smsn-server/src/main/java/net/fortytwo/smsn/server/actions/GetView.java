package net.fortytwo.smsn.server.actions;

import net.fortytwo.smsn.brain.model.Note;
import net.fortytwo.smsn.server.ActionContext;
import net.fortytwo.smsn.server.errors.BadRequestException;
import net.fortytwo.smsn.server.errors.RequestProcessingException;

import java.io.IOException;

/**
 * A service for retrieving hierarchical views of Extend-o-Brain graphs
 */
public class GetView extends RootedViewAction {

    @Override
    protected void performTransaction(final ActionContext context)
            throws RequestProcessingException, BadRequestException {
        super.performTransaction(context);

        Note note = context.getQueries().view(getRoot(), height, getFilter(), style);
        try {
            addView(note, context);
        } catch (IOException e) {
            throw new RequestProcessingException(e);
        }

        addToHistory(getRoot().getId());
    }

    @Override
    protected boolean doesRead() {
        return true;
    }

    @Override
    protected boolean doesWrite() {
        return false;
    }
}
