package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;

public class Workshop implements DominionCard {
    @Override
    public int cost() {
        return 3;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return ModifierWrapper.noModifiers();
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 10;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        ActionRequest req = (ActionRequest) inputWrapper;
        req.callingGame.buyPhase(req.player, new ModifierWrapper(0, 0, 4, 1));
        return ActionResponse.emptyResponse();
    }
}
