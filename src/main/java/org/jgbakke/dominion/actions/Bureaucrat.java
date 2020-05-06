package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;

public class Bureaucrat implements DominionCard {
    @Override
    public int cost() {
        return 4;
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
        return 9;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        ActionRequest req = (ActionRequest) inputWrapper;
        req.player.addOnTopOfDeck(new Silver());
        return ActionResponse.emptyResponse();
    }
}
