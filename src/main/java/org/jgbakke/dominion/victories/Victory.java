package org.jgbakke.dominion.victories;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.DominionCard;

public class Victory implements DominionCard {
    public final int VICTORY_POINTS;

    public final int COST;

    public Victory(int c, int pts){
        COST = c;
        VICTORY_POINTS = pts;
    }

    @Override
    public int cost() {
        return COST;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return ModifierWrapper.noModifiers();
    }

    @Override
    public CardType getCardType() {
        return CardType.VICTORY;
    }

    @Override
    public int id() {
        return -1;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        return null;
    }
}
