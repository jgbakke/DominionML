package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.players.Player;

import java.util.List;

public class Mine implements DominionCard {
    private final MineVisitor mineVisitor = new MineVisitor();

    private final int COPPER_VALUE = 1;
    private final int SILVER_VALUE = 2;

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public ModifierWrapper turnBonusResources() {
        return ModifierWrapper.noModifiers();
    }

    @Override
    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public int id() {
        return 8;
    }

    @Override
    public Object executeAction(Object inputWrapper) {
        Treasure minedTreasure = null;
        ActionRequest req = (ActionRequest) inputWrapper;
        Player p = req.player;

        List<DominionCard> treasureCardReturn = p.acceptHandVisitor(mineVisitor);

        if(!treasureCardReturn.isEmpty()){
            Treasure treasure = (Treasure)(treasureCardReturn.get(0));

            if(treasure.VALUE == COPPER_VALUE){
                minedTreasure = new Silver();
            } else if(treasure.VALUE == SILVER_VALUE){
                minedTreasure = new Gold();
            }
        }

        if(minedTreasure != null){
            p.putCardDirectlyInHand(minedTreasure);
            p.trashCardFromHand(treasureCardReturn.get(0));
        }

        return ActionResponse.emptyResponse();
    }
}
