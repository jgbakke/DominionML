package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Action;

public class Woodcutter implements DominionCard {

    private static ModifierWrapper woodcutterBonuses = new ModifierWrapper(0,0,2,1);

    @Override
    public int id() {
        return 1;
    }

    @Override
    public void executeAction() {

    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return woodcutterBonuses;
    }
}
