package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.jlearning.Action;

public interface DominionCard extends Action {
    ModifierWrapper turnBonusResources();
}
