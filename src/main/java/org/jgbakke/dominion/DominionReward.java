package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.actions.Province;
import org.jgbakke.dominion.actions.Victory;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.Reward;

public class DominionReward implements Reward {
    @Override
    public double getReward(Action action) {
        DominionCard card = (DominionCard)action;

        // The reward will be victory points if it is a victory card
        if(card.getCardType().equals(DominionCard.CardType.VICTORY)){
            if(card.id() == new Province().id()) {
                return ((Victory) card).VICTORY_POINTS;
            }

            return 0;
        }

        // For any treasure or action cards, no reward
        return 0;
    }
}
