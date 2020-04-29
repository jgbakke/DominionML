package org.jgbakke.dominion;

public class ModifierWrapper {
    public int actions;
    public int cards;
    public int coins;
    public int buys;

    public ModifierWrapper(int actions, int cards, int coins, int buys) {
        this.actions = actions;
        this.cards = cards;
        this.coins = coins;
        this.buys = buys;
    }

    public void combineModifiers(ModifierWrapper mw) {
        actions += mw.actions;
        cards += mw.cards;
        coins += mw.coins;
        buys += mw.coins;
    }
}
