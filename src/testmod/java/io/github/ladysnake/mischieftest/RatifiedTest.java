package io.github.ladysnake.mischieftest;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import io.github.ladysnake.elmendorf.ElmendorfTestContext;
import io.github.ladysnake.elmendorf.GameTestUtil;
import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.MischiefRequiemPlugin;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class RatifiedTest implements FabricGameTest {
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void originExists(TestContext ctx) {
        Origin origin = OriginRegistry.get(new Identifier(Mischief.MODID, "rat"));
        GameTestUtil.assertTrue("Rat origin should exist", origin != null);
        ServerPlayerEntity player = ((ElmendorfTestContext)ctx).spawnServerPlayer(1, 0, 1);
        OriginComponent originComponent = ModComponents.ORIGIN.get(player);
        originComponent.setOrigin(OriginLayers.getLayer(new Identifier("origins", "origin")), origin);
        GameTestUtil.assertTrue("Player should be ratified", RemnantComponent.get(player).getRemnantType() == MischiefRequiemPlugin.RATIFIED_REMNANT_TYPE);
        GameTestUtil.assertTrue("Player should be a rat", PossessionComponent.get(player).getHost() instanceof RatEntity);
        ctx.complete();
    }
}
