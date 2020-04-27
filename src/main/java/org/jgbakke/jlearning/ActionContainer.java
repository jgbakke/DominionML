package org.jgbakke.jlearning;

import org.jgbakke.dominion.actions.ThroneRoom;
import org.jgbakke.dominion.actions.Woodcutter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ActionContainer {
    private static ActionContainer actionContainer = new ActionContainer();

    // TODO: Remove
    private Action[] actionsArray;

    private int actionsCount;

    private ActionContainer(){
        initContainer();
    }

    public static ActionContainer getInstance(){
        return actionContainer;
    }

    public int getActionsCount() {
        return actionsCount;
    }

    public Action getActionById(int id){
        return actionsArray[id];
    }

    public Action getAction(int id){
        return actionsArray[id];
    }

    public void registerSize(int actions){
        actionsCount = actions;
        actionsArray = new Action[actionsCount];
    }

    public void registerAction(Action act){
        actionsArray[act.id()] = act;
    }

    private void initContainer() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream("dominion_actions.properties");
        try {
            Properties props = new Properties();
            props.load(is);
            String dominionActions = props.getProperty("actions");

            String[] actionStringNames = dominionActions.split(",");
            registerSize(actionStringNames.length);

            for (int i = 0; i < actionsCount; i++) {
                try {
                    Action act = (Action)
                            Class.forName(String.format("org.jgbakke.dominion.actions.%s", actionStringNames[i]))
                            .newInstance();

                    if(act.id() < actionsCount){
                        actionsArray[act.id()] = act;
                    } else {
                        throw new ArrayIndexOutOfBoundsException(
                                String.format(
                                        "ID for class %s was %d but there are only %d " +
                                        "actions. Check that your ID is less than the number " +
                                        "of actions.",
                                        actionStringNames[i],
                                        act.id(),
                                        actionsCount
                                )
                        );
                    }

                } catch (InstantiationException | ClassNotFoundException e) {
                    System.out.println(String.format("Could not instantiate class: %s", actionStringNames[i]));
                    e.printStackTrace();
                }
            }

            System.out.println("Classes loaded!");

        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
