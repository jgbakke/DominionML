package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.players.Player;

import java.util.List;

public interface HandVisitor {
    List<DominionCard> visit(Player visited, List<DominionCard> hand);
}
