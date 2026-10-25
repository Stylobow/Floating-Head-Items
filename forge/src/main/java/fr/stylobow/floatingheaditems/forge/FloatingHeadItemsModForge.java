package fr.stylobow.floatingheaditems.forge;

import dev.architectury.platform.forge.EventBuses;
import fr.stylobow.floatingheaditems.FloatingItemMod;
import fr.stylobow.floatingheaditems.forge.client.FloatingHeadItemsForgeClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FloatingItemMod.MOD_ID)
public class FloatingHeadItemsModForge {
    public FloatingHeadItemsModForge() {
        EventBuses.registerModEventBus(FloatingItemMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FloatingItemMod.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> FloatingHeadItemsForgeClient::init);
    }
}
