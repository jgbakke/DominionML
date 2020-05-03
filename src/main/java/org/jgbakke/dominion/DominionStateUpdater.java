package org.jgbakke.dominion;

import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.StateUpdater;

public class DominionStateUpdater implements StateUpdater {
    @Override
    public void updateState(Action taken, int[] stateIdentifier) {
        // Cards less then ID 0 indicate they should never be bought by the AI
        // Therefore we also don't want them wasting space in our QTable, so it is out of range
        if(taken.id() > 0) {
            stateIdentifier[taken.id()]++;
        }
    }
}
