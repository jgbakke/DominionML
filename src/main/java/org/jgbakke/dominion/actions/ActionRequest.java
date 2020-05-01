package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.players.Player;

public class ActionRequest {
    public Game callingGame;
    public Player player;
    public ModifierWrapper resources;


    public ActionRequest(Game g, Player player, ModifierWrapper resources) {
        this.callingGame = g;
        this.player = player;
        this.resources = resources;
    }
}
