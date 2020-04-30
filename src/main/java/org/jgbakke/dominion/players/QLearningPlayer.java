package org.jgbakke.dominion.players;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.jlearning.QLearning;

import java.util.LinkedList;
import java.util.List;

public class QLearningPlayer extends Player {

    private QLearning qLearning = new QLearning();

    public QLearningPlayer(){
        this.name = "Dominionator";
    }

    @Override
    public DominionCard chooseAction(int actionsRemaining) {
        // TODO: A real implementation
        return hand.get(0);
    }

    @Override
    public List<DominionCard> buyPhase(int coins) {
        return new LinkedList<>();
    }
}
