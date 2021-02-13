package ladysnake.ratsrats.common.world;

import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;
import java.util.Random;

public class RatSpawner implements Spawner {
    private int ticksUntilNextSpawn;

    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (spawnAnimals && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            --this.ticksUntilNextSpawn;
            if (this.ticksUntilNextSpawn <= 0) {
                this.ticksUntilNextSpawn = 300;
                Random random = world.random;
                world.getPlayers().forEach(serverPlayerEntity -> {
                    int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    BlockPos blockPos = serverPlayerEntity.getBlockPos().add(i, 0, j);
                    if (world.isRegionLoaded(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10)) {
                        if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, Rats.RAT)) {
                            this.spawnInHouse(world, blockPos);
                        }
                    }
                });
            }
        }

        return 0;
    }

    private void spawnInHouse(ServerWorld world, BlockPos pos) {
        long bedCount = world.getPointOfInterestStorage().count(PointOfInterestType.HOME.getCompletionCondition(), pos, 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        List<RatEntity> list = world.getNonSpectatingEntities(RatEntity.class, (new Box(pos)).expand(96.0, 16.0D, 96.0D));

        if (list.size() < bedCount *3) {
            this.spawn(world, pos);
        }
    }

    private void spawn(ServerWorld world, BlockPos pos) {
        RatEntity ratEntity = Rats.RAT.create(world);
        if (ratEntity != null) {
            ratEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, (EntityData)null, (CompoundTag)null);
            ratEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            world.spawnEntityAndPassengers(ratEntity);
        }
    }
}
