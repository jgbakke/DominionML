package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Logger;

public class Market implements DominionCard {
    private static final ModifierWrapper MARKET_RESOURCES = new ModifierWrapper(1, 1, 1, 1);
    @Override
    public ModifierWrapper turnBonusResources() {
        return MARKET_RESOURCES;
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 3;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        Logger.log("Market executed!");
        return ActionResponse.emptyResponse();
    }
}
