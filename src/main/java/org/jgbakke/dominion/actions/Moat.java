package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;

public class Moat implements DominionCard {
    @Override
    public int cost() {
        return 2;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return new ModifierWrapper(0, 2, 0, 0);
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 11;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        return ActionResponse.emptyResponse();
    }
}
