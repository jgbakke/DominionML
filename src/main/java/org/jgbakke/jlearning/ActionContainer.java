package org.jgbakke.jlearning;

import org.jgbakke.dominion.actions.ThroneRoom;
import org.jgbakke.dominion.actions.Woodcutter;

public class ActionContainer {
    private static ActionContainer actionContainer = new ActionContainer();

    // TODO: Remove the 2
    private int actionsCount = 2;

    public static ActionContainer getInstance(){
        return actionContainer;
    }

    public int getActionsCount() {
        return actionsCount;
    }

    public Action getActionById(int id){
        return actionsArray[id];
    }

    // TODO: Load these in for real
    private Action[] actionsArray = new Action[]{new ThroneRoom(), new Woodcutter()};

    public Action getAction(int id){
        return actionsArray[id];
    }

    public void registerSize(int actions){
        actionsCount = actions;
        actionsArray = new Action[actionsCount];
    }

    public void registerAction(Action act){
        actionsArray[act.id()] = act;
    }

}
