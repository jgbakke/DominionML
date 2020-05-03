package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.Logger;

public class Woodcutter implements DominionCard {

    private static ModifierWrapper woodcutterBonuses = new ModifierWrapper(0,0,2,1);

    @Override
    public int id() {
        return 1;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        Logger.log("Woodcutter executed!");
        return ActionResponse.emptyResponse();
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return woodcutterBonuses;
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }
}
