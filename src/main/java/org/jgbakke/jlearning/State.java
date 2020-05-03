package org.jgbakke.jlearning;

public class State {
    private int[] stateIdentifier;

    private StateUpdater stateUpdater;

    public State(int[] stateIdentifier, StateUpdater stateUpdater){
        this.stateIdentifier = stateIdentifier;
        this.stateUpdater = stateUpdater;
    }

    public State(StateUpdater updater){
        this(new int[ActionContainer.getInstance().getActionsCount()], updater);
    }

    public void updateState(Action taken){
        stateUpdater.updateState(taken, stateIdentifier);
    }
}
