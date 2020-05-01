package org.jgbakke.jlearning;

public interface Action {
    int id();

    Object executeAction(Object inputWrapper);
}
