package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Action;

public interface DominionCard extends Action {
    int cost();

    ModifierWrapper turnBonusResources();

    CardType getCardType();

    public enum CardType {
        ACTION,
        VICTORY,
        TREASURE
    }
}
