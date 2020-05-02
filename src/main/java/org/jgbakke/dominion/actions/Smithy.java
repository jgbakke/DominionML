package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Logger;

public class Smithy implements DominionCard {
    private static final ModifierWrapper SMITHY_RESOURCES = new ModifierWrapper(0, 3, 0, 0);

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
        return 2;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        Logger.log("Smithy executed!");
        return ActionResponse.emptyResponse();
    }
}
