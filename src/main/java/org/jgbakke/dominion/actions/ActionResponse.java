package org.jgbakke.dominion.actions;

import org.jgbakke.dominion.ModifierWrapper;

public class ActionResponse {
    public ModifierWrapper resources;

    public ActionResponse(ModifierWrapper resources) {
        this.resources = resources;
    }

    public static ActionResponse emptyResponse(){
        return new ActionResponse(ModifierWrapper.noModifiers());
    }
}
