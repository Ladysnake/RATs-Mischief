package io.github.ladysnake.mischieftest;

import ladysnake.ratsmischief.common.Mischief;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class MischiefTestSuite implements FabricGameTest {
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void gameLaunches(TestContext ctx) {
        ctx.spawnMob(Mischief.RAT, 2, 2, 2);
        ctx.expectEntityAt(Mischief.RAT, 2, 2, 2);
        ctx.complete();
    }
}
