//package ladysnake.ratsmischief.common;
//
//import ladysnake.ratsmischief.common.command.PlayerRatifyCommand;
//import ladysnake.ratsmischief.common.command.PlayerUnratifyCommand;
//import ladysnake.ratsmischief.common.entity.RatEntity;
//import ladysnake.requiem.api.v1.RequiemPlugin;
//import ladysnake.requiem.api.v1.event.requiem.HumanityCheckCallback;
//import ladysnake.requiem.api.v1.event.requiem.PossessionStartCallback;
//import ladysnake.requiem.api.v1.remnant.RemnantComponent;
//import ladysnake.requiem.api.v1.remnant.RemnantType;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.registry.Registry;
//
//import java.util.function.Consumer;
//
//public class MischiefRequiemPlugin implements RequiemPlugin {
//    public static final RatifiedRemnantType RATIFIED_REMNANT_TYPE = new RatifiedRemnantType();
//
//    @Override
//    public void onRequiemInitialize() {
//        // allow rat possession for rat players
//        PossessionStartCallback.EVENT.register(new Identifier("ratsmischief:possession"), (mobEntity, playerEntity, b) -> {
//            if (mobEntity instanceof RatEntity && RemnantComponent.get(playerEntity).getRemnantType() == RATIFIED_REMNANT_TYPE) {
//                return PossessionStartCallback.Result.ALLOW;
//            }
//            return PossessionStartCallback.Result.PASS;
//        });
//
//        HumanityCheckCallback.EVENT.register(livingEntity -> {
//            if (livingEntity instanceof RatEntity) {
//                return 9999;
//            }
//            return 0;
//        });
//    }
//
//    @Override
//    public void registerRemnantStates(Registry<RemnantType> registry) {
//        Registry.register(registry, new Identifier("ratsmischief:ratified"), RATIFIED_REMNANT_TYPE);
//        PlayerRatifyCommand.action = changeState(RATIFIED_REMNANT_TYPE);
//        PlayerUnratifyCommand.action = changeState(registry.get((Identifier) null));
//    }
//
//    public Consumer<PlayerEntity> changeState(RemnantType type) {
//        return player -> RemnantComponent.get(player).become(type, true);
//    }
//}
