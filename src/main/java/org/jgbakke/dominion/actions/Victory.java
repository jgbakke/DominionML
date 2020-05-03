package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.DominionCard;

public class Victory implements DominionCard {
    public final int VICTORY_POINTS;

    public final int COST;

    public final int ID;

    public Victory(int c, int pts){
        this(c, pts, -1);
    }

    public Victory(int c, int pts, int id){
        COST = c;
        VICTORY_POINTS = pts;
        ID = id;
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
        return ID;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        return null;
    }
}
