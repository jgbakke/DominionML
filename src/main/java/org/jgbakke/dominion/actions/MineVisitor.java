package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.HandVisitor;
import org.jgbakke.dominion.players.Player;

import java.util.LinkedList;
import java.util.List;

public class MineVisitor implements HandVisitor {
    private final int copperVal = new Copper().VALUE;
    private final int goldVal = new Gold().VALUE;

    @Override
    public List<DominionCard> visit(Player visited, List<DominionCard> hand) {
        int minValue = goldVal;
        DominionCard treasureMin = null;

        for (DominionCard card : hand) {
            if(card.getCardType().equals(DominionCard.CardType.TREASURE)){
                Treasure treasure = (Treasure)card;
                if(treasure.VALUE < minValue){
                    minValue =  treasure.VALUE;
                    treasureMin = treasure;
                }

                if(minValue == copperVal){
                    // No sense to continue going through the loop
                    break;
                }
            }
        }

        LinkedList<DominionCard> retval = new LinkedList<>();
        retval.add(treasureMin);
        return retval;
    }
}
