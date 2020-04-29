package org.jgbakke.dominion.players;

import org.jgbakke.dominion.actions.DominionCard;

import java.util.LinkedList;
import java.util.List;

public class QLearningPlayer extends Player {
    public QLearningPlayer(){
        this.name = "Dominionator";
    }

    @Override
    public DominionCard chooseAction(int actionsRemaining) {
        return null;
    }

    @Override
    public List<DominionCard> buyPhase(int coins) {
        return new LinkedList<>();
    }
}
