package org.jgbakke.jlearning;

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
}
