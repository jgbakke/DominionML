package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.players.Player;

import java.util.List;

public class Cellar implements DominionCard {
    private final static ModifierWrapper CELLAR_RESOURCES = new ModifierWrapper(1, 0,0 ,0);
    private final CellarVisitor cellarVisitor = new CellarVisitor();

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return CELLAR_RESOURCES;
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 6;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        ActionRequest req = (ActionRequest) inputWrapper;
        Player p = req.player;

        List<DominionCard> chosenToDiscard = p.acceptHandVisitor(cellarVisitor);
        int chosenLength = chosenToDiscard.size();

        chosenToDiscard.forEach(p::discardSpecificCard);

        // We already gave the +1 action so now we only need to give the cards
        return new ActionResponse(new ModifierWrapper(0, chosenLength, 0, 0));
    }
}
