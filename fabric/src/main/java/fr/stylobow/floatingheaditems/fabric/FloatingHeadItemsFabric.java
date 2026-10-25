package fr.stylobow.floatingheaditems.fabric;

import fr.stylobow.floatingheaditems.FloatingItemMod;
import net.fabricmc.api.ModInitializer;

public class FloatingHeadItemsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FloatingItemMod.init();
    }
}
