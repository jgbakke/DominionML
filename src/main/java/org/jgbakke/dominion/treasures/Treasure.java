package org.jgbakke.dominion.treasures;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.DominionCard;

public class Treasure implements DominionCard {

    public final int VALUE;
    private final int ID;

    private final int COST;

    public Treasure(int c, int v, int id){
        VALUE = v;
        ID = id;
        COST = c;
    }

    @Override
    public int cost() {
        return COST;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return new ModifierWrapper(0, 0, VALUE, 0);
    }

    @Override
    public CardType getCardType() {
        return CardType.TREASURE;
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
