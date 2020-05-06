package org.jgbakke.dominion.players;

import org.jgbakke.dominion.DominionReward;
import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.actions.*;
import org.jgbakke.jlearning.*;

import java.util.List;

public class QLearningPlayer extends Player {
    private static final int THRONE_ROOM_ID = new ThroneRoom().id();
    private static final int CELLAR_ID = new Cellar().id();
    private QLearning qLearning;

    public QLearningPlayer(Game game){
        super(game);
        this.name = "Dominionator";
        qLearning = new QLearning(new DominionReward(game));
    }

    @Override
    public Action chooseBuy(State currentState, List<Action> validCards){
        return qLearning.chooseAction(currentState, validCards);
    }

    @Override
    public void cleanup() {
        qLearning.saveToDB();
        //saveGameResult(name, getVictoryPoints());
    }

    @Override
    public void gainNewCard(DominionCard card){
        // Do not reward for cards given at start of game
        if(allCards.size() >= 10 && card.id() >= 0) {
            qLearning.updateQTable(currentState, card);
            currentState = currentState.getResultingState(card);
        }

        super.gainNewCard(card);
    }

    @Override
    protected void trashCard(DominionCard card){
        if(card.id() >= 0) {
            currentState.undoStateUpdate(card);
        }

        super.trashCard(card);
    }
}
