package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.Logger;

public class ThroneRoom implements DominionCard {
    private static ModifierWrapper throneRoomResources = new ModifierWrapper(0,0,0,0);

    @Override
    public int id() {
        return 0;
    }

    @Override
    public void executeAction() {
        Logger.log("Throne Room executed!");
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return throneRoomResources;
    }
}
