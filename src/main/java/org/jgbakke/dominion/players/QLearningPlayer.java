package org.jgbakke.dominion.players;

import org.jgbakke.dominion.DominionStateUpdater;
import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.actions.Copper;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.actions.Province;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.ActionContainer;
import org.jgbakke.jlearning.QLearning;
import org.jgbakke.jlearning.State;

import java.util.LinkedList;
import java.util.List;

public class QLearningPlayer extends Player {
    private Game game;
    private QLearning qLearning = new QLearning();

    protected State currentState = new State(new DominionStateUpdater());

    public QLearningPlayer(Game game){
        this.game = game;
        this.name = "Dominionator";
    }

    @Override
    public DominionCard chooseAction(int actionsRemaining) {
        // TODO: More advanced implementation
        // Current algorithm: Just pick the first action card
        return hand.stream()
                .filter(c -> c.getCardType().equals(DominionCard.CardType.ACTION))
                .findFirst()
                .orElse(null);

    }

    @Override
    public List<DominionCard> buyPhase(int coins, int buys) {
        LinkedList<DominionCard> chosenBuys = new LinkedList<>();

        while(coins > 0 && buys > 0){
            List<Action> validCards = validBuyChoices(coins);

            DominionCard chosen;

            if(coins >= 8){
                chosen = new Province();
            } else {
                Action qLearningAction = qLearning.chooseAction(currentState, validCards);

                if(qLearningAction == null){
                    chosen = new Copper();
                } else {
                    chosen = (DominionCard)qLearningAction;
                }
            }

            chosenBuys.add(chosen);

            buys--;
            coins -= chosen.cost();
        }

        return chosenBuys;

    }

    private boolean canBuyCard(DominionCard card, int coins){
        return game.bank.hasCardsRemaining(card.id()) && card.cost() <= coins;
    }

    private boolean cardIsTooCheap(DominionCard card, int coins){
        //return !card.getCardType().equals(DominionCard.CardType.TREASURE);
        return card.cost() < coins - 1;
    }

    private List<Action> validBuyChoices(int coins){
        List<Action> validChoices = new LinkedList<>();

        for (Action action : ActionContainer.getInstance().getActions()) {
            DominionCard card = (DominionCard)action;

            if(canBuyCard(card, coins) && !cardIsTooCheap(card, coins)){
                validChoices.add(card);
            }
        }

        return validChoices;
    }

    @Override
    public void gainNewCard(DominionCard card){
        //qle
        currentState.updateState(card);
        super.gainNewCard(card);
    }
}
