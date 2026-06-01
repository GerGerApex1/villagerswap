package me.gergerapex1.villagerswap.client.states;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateManager {
    private static ArrayList<String> prioritizedEnchants = new ArrayList<String>();
    private static AtomicBoolean autoCloseUi =  new AtomicBoolean(false);

    public static void setAutoCloseUi(boolean value) {
        autoCloseUi.set(value);
    }
    public static boolean getAutoCloseUi() {
        return autoCloseUi.get();
    }
    public static void addPrioritizedEnchant(String enchantment) {
        prioritizedEnchants.add(enchantment);
    }
    public static void removePrioritizedEnchant(String enchantment) {
        prioritizedEnchants.remove(enchantment);
    }
    public static ArrayList<String> getPrioritizedEnchants() {
        return prioritizedEnchants;
    }
}
