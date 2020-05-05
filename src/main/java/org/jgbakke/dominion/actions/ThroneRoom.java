package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.players.Player;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.Logger;

public class ThroneRoom implements DominionCard {
    private static ModifierWrapper throneRoomResources = new ModifierWrapper(0,0,0,0);

    @Override
    public int id() {
        return 4;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        ActionRequest req = (ActionRequest) inputWrapper;
        Player player = req.player;
        DominionCard throneRoomedCard = player.chooseAction(req.resources);

        if(throneRoomedCard != null) {
            for (int i = 0; i < 2; i++) {
                req.callingGame.playCard(player, throneRoomedCard);
            }
        }

        // Give 0 resources here because they should already have them added from the playCard function
        return ActionResponse.emptyResponse();
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return throneRoomResources;
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }
}
