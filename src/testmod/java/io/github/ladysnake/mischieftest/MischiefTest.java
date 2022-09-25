package io.github.ladysnake.mischieftest;

import io.github.ladysnake.elmendorf.Elmendorf;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class MischiefTest implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("origins") && FabricLoader.getInstance().isModLoaded("requiem")) {
            Elmendorf.registerTestClass(RatifiedTest.class, "mischieftest");
        }
    }
}
