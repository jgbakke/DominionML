package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Logger;

public class Smithy implements DominionCard {
    private static final ModifierWrapper SMITHY_RESOURCES = new ModifierWrapper(0, 3, 0, 0);

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return SMITHY_RESOURCES;
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 5;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        return ActionResponse.emptyResponse();
    }
}
