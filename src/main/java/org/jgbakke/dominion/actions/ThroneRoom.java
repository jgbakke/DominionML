package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.players.Player;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.Logger;

public class ThroneRoom implements DominionCard {
    private static ModifierWrapper throneRoomResources = new ModifierWrapper(0,0,0,0);

    @Override
    public int id() {
        return 0;
    }

    @Override
    public ActionResponse executeAction(Object inputWrapper) {
        ActionRequest req = (ActionRequest) inputWrapper;
        Player player = req.player;
        DominionCard throneRoomedCard = player.chooseAction(req.resources.actions);

        if(throneRoomedCard != null) {
            // TODO: Test throne room
            for (int i = 0; i < 2; i++) {
                req.callingGame.playCard(player, throneRoomedCard);
            }
        }


        // Give 0 resources here because they should already have them added from the playCard function
        return ActionResponse.emptyResponse();
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return throneRoomResources;
    }
}
