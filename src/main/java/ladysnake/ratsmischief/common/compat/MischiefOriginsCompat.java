package ladysnake.ratsmischief.common.compat;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import ladysnake.ratsmischief.common.Mischief;
import ladysnake.requiem.common.gamerule.RequiemSyncedGamerules;
import ladysnake.requiem.common.gamerule.StartingRemnantType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MischiefOriginsCompat {
    public static final Identifier CONDITION_ID = new Identifier(Mischief.MODID, "enable_rat");
    public static final ConditionFactory<Entity> CONDITION_FACTORY = new ConditionFactory<>(CONDITION_ID, new SerializableData().add("enabled", SerializableDataTypes.BOOLEAN), (instance, entity) -> {
        if (FabricLoader.getInstance().isModLoaded("requiem")) {
            StartingRemnantType startingRemnantType = RequiemSyncedGamerules.get(entity.world).getStartingRemnantType();
            return startingRemnantType.getRemnantType() == StartingRemnantType.CHOOSE.getRemnantType();
        }
        return false;
    });

    public static void init() {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, CONDITION_ID, CONDITION_FACTORY);
    }
}
