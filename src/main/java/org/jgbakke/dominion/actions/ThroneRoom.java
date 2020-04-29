package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Action;

public class ThroneRoom implements DominionCard {
    private static ModifierWrapper throneRoomResources = new ModifierWrapper(0,0,0,0);

    @Override
    public int id() {
        return 0;
    }

    @Override
    public void executeAction() {

    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return throneRoomResources;
    }
}
