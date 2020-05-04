package org.jgbakke.jlearning;

import java.util.Arrays;

public class State {
    private int[] stateIdentifier;

    private StateUpdater stateUpdater;

    public State(int[] stateIdentifier, StateUpdater stateUpdater){
        this.stateIdentifier = stateIdentifier.clone();
        this.stateUpdater = stateUpdater;
    }

    public State(StateUpdater updater){
        this(new int[ActionContainer.getInstance().getActionsCount()], updater);
    }

    public void updateState(Action taken){
        stateUpdater.updateState(taken, stateIdentifier);
    }

    public State getResultingState(Action taken){
        // This should return a new State object that this state will transition to if taken is executed
        State newState = new State(stateIdentifier, stateUpdater);
        newState.updateState(taken);
        return newState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Arrays.equals(stateIdentifier, state.stateIdentifier);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(stateIdentifier);
    }
}
