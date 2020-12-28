package ladysnake.ratsrats.common;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;

public class Rats implements ModInitializer {
    public static final String MODID = "ratsrats";

    public static EntityType<RatEntity> RAT;

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        RAT = registerEntity("rat", FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RatEntity::new).dimensions(EntityDimensions.changing(1.0F, 1.0F)).trackRangeBlocks(8).build());
        FabricDefaultAttributeRegistry.register(RAT, RatEntity.createEntityAttributes());
    }

    private static <T extends Entity> EntityType<T> registerEntity(String s, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, MODID + ":" + s, entityType);
    }
}
