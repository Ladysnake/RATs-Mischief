package ladysnake.ratsrats.common;

import ladysnake.ratsrats.common.entity.RatEntity;
import ladysnake.ratsrats.common.item.RatPouchItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;

public class Rats implements ModInitializer {
    public static final String MODID = "ratsrats";

    public static EntityType<RatEntity> RAT;

    public static Item LEATHER_RAT_POUCH;
    public static Item TWISTED_RAT_POUCH;
    public static Item PURPUR_RAT_POUCH;

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        RAT = registerEntity("rat", FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RatEntity::new).dimensions(EntityDimensions.changing(0.8F, 0.4F)).trackRangeBlocks(8).build());
        FabricDefaultAttributeRegistry.register(RAT, RatEntity.createEntityAttributes());

        LEATHER_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.MISC), 3), "leather_rat_pouch");
        TWISTED_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.MISC), 5), "twisted_rat_pouch");
        PURPUR_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.MISC), 7), "purpur_rat_pouch");
    }

    private static <T extends Entity> EntityType<T> registerEntity(String s, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, MODID + ":" + s, entityType);
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, MODID + ":" + name, item);
        return item;
    }
}
