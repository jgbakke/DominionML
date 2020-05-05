package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.actions.Duchy;
import org.jgbakke.dominion.actions.Province;
import org.jgbakke.dominion.actions.Victory;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.Reward;

public class DominionReward implements Reward {
    private Game game;

    @Override
    public double getReward(Action action) {
        DominionCard card = (DominionCard)action;

        // The reward will be victory points if it is a victory card
        if(card.getCardType().equals(DominionCard.CardType.VICTORY)){
            if(card.id() == new Province().id()) {
                return ((Victory) card).VICTORY_POINTS;
            }

            // Only reward for duchy if it happens near the end of the game
            if(card.id() == new Duchy().id()
                    && game.getRemainingRounds() >= 3) {
                return ((Victory) card).VICTORY_POINTS;
            }
        }

        // For any treasure or action cards, no reward
        return 0;
    }

    public DominionReward(Game game){
        this.game = game;
    }
}
