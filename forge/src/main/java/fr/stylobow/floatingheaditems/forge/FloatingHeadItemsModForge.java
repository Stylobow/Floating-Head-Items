package fr.stylobow.floatingheaditems.forge;

import dev.architectury.platform.forge.EventBuses;
import fr.stylobow.floatingheaditems.FloatingHeadItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FloatingHeadItemsModForge.MOD_ID)
public class FloatingHeadItemsModForge {
    public FloatingHeadItemsModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FloatingHeadItemsMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FloatingHeadItemsMod.init();
    }
}
