package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Logger;

public class Village implements DominionCard {
    private static final ModifierWrapper VILLAGE_RESOURCES = new ModifierWrapper(2, 1, 0, 0);

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return VILLAGE_RESOURCES;
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 4;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        Logger.log("Village executed!");
        return ActionResponse.emptyResponse();
    }
}
