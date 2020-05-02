package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.HandVisitor;
import org.jgbakke.dominion.players.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CellarVisitor implements HandVisitor {

    @Override
    public List<DominionCard> visit(Player visited, List<DominionCard> hand) {
        int actionsLeft = visited.getResources().actions;
        double averageCoinValue = visited.averageCoinValue(actionsLeft > 1);

        return hand.stream()
                .filter(c -> (isTreasureCard(c) && isBelowCoinValue(c, averageCoinValue)) ||
                            (hasOnlyTerminalCards(hand) && isActionCard(c)) ||
                            isVictoryCard(c)
                        ).collect(Collectors.toList());
    }

    public static boolean hasOnlyTerminalCards(List<DominionCard> hand){
        return hand.stream().noneMatch(c -> c.turnBonusResources().actions > 0 || c.turnBonusResources().cards > 0);
    }

    private static boolean isVictoryCard(DominionCard c){
        return c.getCardType().equals(DominionCard.CardType.VICTORY);
    }

    private static boolean isActionCard(DominionCard c){
        return c.getCardType().equals(DominionCard.CardType.ACTION);
    }

    private static boolean isTreasureCard(DominionCard c){
        return c.getCardType().equals(DominionCard.CardType.TREASURE);
    }

    private static boolean isBelowCoinValue(DominionCard c, double value){
        return c.turnBonusResources().coins < value;
    }
}
