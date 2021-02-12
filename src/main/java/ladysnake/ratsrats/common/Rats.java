package ladysnake.ratsrats.common;

import ladysnake.ratsrats.common.entity.RatEntity;
import ladysnake.ratsrats.common.item.RatPouchItem;
import ladysnake.ratsrats.common.item.RatStaffItem;
import ladysnake.ratsrats.common.world.RatSpawner;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import software.bernie.geckolib3.GeckoLib;

public class Rats implements ModInitializer {
    public static final String MODID = "ratsrats";

    public static EntityType<RatEntity> RAT;

    public static Item LEATHER_RAT_POUCH;
    public static Item TWISTED_RAT_POUCH;
    public static Item PURPUR_RAT_POUCH;

    public static Item HARVEST_STAFF;
    public static Item COLLECTION_STAFF;
    public static Item SKIRMISH_STAFF;

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        RAT = registerEntity("rat", FabricEntityTypeBuilder.createMob().entityFactory(RatEntity::new).spawnGroup(SpawnGroup.AMBIENT).dimensions(EntityDimensions.changing(0.8F, 0.4F)).trackRangeBlocks(8).spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RatEntity::canSpawn).build());
        FabricDefaultAttributeRegistry.register(RAT, RatEntity.createEntityAttributes());

        // rat custom spawner
        RatSpawner ratSpawner = new RatSpawner();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getWorlds().forEach(world -> {
                ratSpawner.spawn(world, server.getSaveProperties().getDifficulty() != Difficulty.PEACEFUL, server.shouldSpawnAnimals());
            });
        });

        LEATHER_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), 3), "leather_rat_pouch");
        TWISTED_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), 5), "twisted_rat_pouch");
        PURPUR_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), 7), "purpur_rat_pouch");

        HARVEST_STAFF = registerItem(new RatStaffItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.HARVEST), "harvest_staff");
        COLLECTION_STAFF = registerItem(new RatStaffItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.COLLECT), "collection_staff");
        SKIRMISH_STAFF = registerItem(new RatStaffItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.SKIRMISH), "skirmish_staff");

        // rat kid painting
        Registry.register(Registry.PAINTING_MOTIVE, new Identifier(MODID, "a_rat_in_time"), new PaintingMotive(64, 48));
    }

    private static <T extends Entity> EntityType<T> registerEntity(String s, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, MODID + ":" + s, entityType);
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, MODID + ":" + name, item);
        return item;
    }
}
