package org.jgbakke.jlearning;

public interface StateUpdater {
    // Update the state identifier, preferably in place
    void updateState(Action taken, int[] stateIdentifier);
}
