package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.HandVisitor;
import org.jgbakke.dominion.players.Player;

import java.util.List;

public class AddToHandVisitor implements HandVisitor {
    @Override
    public List<DominionCard> visit(Player visited, List<DominionCard> hand) {
        return null;
    }
}
