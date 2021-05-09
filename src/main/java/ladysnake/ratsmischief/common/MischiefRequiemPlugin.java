package ladysnake.ratsmischief.common;

import ladysnake.ratsmischief.common.cca.PlayerRatComponent;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.requiem.api.v1.RequiemPlugin;
import ladysnake.requiem.api.v1.event.minecraft.PlayerRespawnCallback;
import ladysnake.requiem.api.v1.event.requiem.HumanityCheckCallback;
import ladysnake.requiem.api.v1.event.requiem.PossessionStartCallback;
import ladysnake.requiem.api.v1.internal.StatusEffectReapplicator;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.RemnantType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MischiefRequiemPlugin implements RequiemPlugin {
    @Override
    public void onRequiemInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // kill soul players that are supposed to be rats cause being a rat is so much better than trying to find a new body requiem is cool look I love requiem but being a rat is so much better michael it's your birthday today
            server.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                if (PlayerRatComponent.KEY.get(serverPlayerEntity).isRat) {
                    // turn into a remnant else they won't be able to possess rats
                    if (RemnantComponent.get(serverPlayerEntity).getRemnantType() != (RemnantType) Registry.REGISTRIES.get(new Identifier("requiem:remnant_states")).get(new Identifier("requiem:remnant"))) {
                        RemnantComponent.get(serverPlayerEntity).become((RemnantType) Registry.REGISTRIES.get(new Identifier("requiem:remnant_states")).get(new Identifier("requiem:remnant")));
                    }

                    // make them respawn on death, or kill them if they're humans
                    if ((RemnantComponent.get(serverPlayerEntity).isIncorporeal() || !RemnantComponent.get(serverPlayerEntity).isVagrant()) && serverPlayerEntity.age > 20) {
                        serverPlayerEntity.kill(); // haha get fucked nerd
                    }

                    if (PossessionComponent.get(serverPlayerEntity).getPossessedEntity() instanceof RatEntity) {
                        if (serverPlayerEntity.isUsingItem()) {
                            ((RatEntity) PossessionComponent.get(serverPlayerEntity).getPossessedEntity()).setEating(true);
                        } else {
                            ((RatEntity) PossessionComponent.get(serverPlayerEntity).getPossessedEntity()).setEating(false);
                        }
                    }
                }
            });
        });

        // remove attrition when respawning
        PlayerRespawnCallback.EVENT.register((serverPlayerEntity, returnFromEnd) -> {
            if (PlayerRatComponent.KEY.get(serverPlayerEntity).isRat && !returnFromEnd) {
                serverPlayerEntity.clearStatusEffects();
                StatusEffectReapplicator.KEY.get(serverPlayerEntity).definitivelyClear();
                RatEntity rat = new RatEntity(Mischief.RAT, serverPlayerEntity.getServerWorld());
                rat.copyPositionAndRotation(serverPlayerEntity);
                serverPlayerEntity.getServerWorld().spawnEntity(rat);
                PossessionComponent.get(serverPlayerEntity).startPossessing(rat);
            }
        });

        // allow rat possession for rat players
        PossessionStartCallback.EVENT.register(new Identifier("ratsmischief:possession"), (mobEntity, playerEntity, b) -> {
            if (mobEntity instanceof RatEntity && PlayerRatComponent.KEY.get(playerEntity).isRat) {
                return PossessionStartCallback.Result.ALLOW;
            }
            return PossessionStartCallback.Result.PASS;
        });

        HumanityCheckCallback.EVENT.register(livingEntity -> {
            if (livingEntity instanceof RatEntity) {
                return 9999;
            }
            return 0;
        });
    }
}
