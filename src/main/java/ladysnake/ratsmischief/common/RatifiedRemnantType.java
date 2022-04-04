//package ladysnake.ratsmischief.common;
//
//import io.github.ladysnake.pal.AbilitySource;
//import io.github.ladysnake.pal.Pal;
//import io.github.ladysnake.pal.VanillaAbilities;
//import ladysnake.ratsmischief.common.entity.RatEntity;
//import ladysnake.requiem.api.v1.possession.PossessionComponent;
//import ladysnake.requiem.api.v1.remnant.MobResurrectable;
//import ladysnake.requiem.api.v1.remnant.RemnantState;
//import ladysnake.requiem.api.v1.remnant.RemnantType;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.mob.MobEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
//import net.minecraft.util.Identifier;
//import org.jetbrains.annotations.NotNull;
//
//public class RatifiedRemnantType implements RemnantType {
//    private final Text name = new TranslatableText("ratsmischief:remnant_type.name");
//
//    @Override
//    public @NotNull RemnantState create(PlayerEntity player) {
//        return new RatifiedRemnantState(player);
//    }
//
//    @Override
//    public boolean isDemon() {
//        return false;
//    }
//
//    @Override
//    public @NotNull Text getName() {
//        return this.name;
//    }
//
//    public static class RatifiedRemnantState implements RemnantState {
//        public static final AbilitySource SOUL_STATE = Pal.getAbilitySource(new Identifier("mischief", "soul_state"));
//
//        private final PlayerEntity player;
//
//        public RatifiedRemnantState(PlayerEntity player) {
//            this.player = player;
//        }
//
//        @Override
//        public void setup(RemnantState oldHandler) {
//            if (!player.world.isClient) {
//                Pal.grantAbility(this.player, VanillaAbilities.INVULNERABLE, SOUL_STATE);
//                if (this.isInWorld()) {
//                    PossessionComponent.get(this.player).stopPossessing(false);
//                    RatEntity rat = new RatEntity(Mischief.RAT, this.player.world);
//                    rat.copyPositionAndRotation(this.player);
//                    this.player.world.spawnEntity(rat);
//                    PossessionComponent.get(this.player).startPossessing(rat);
//                }
//            }
//        }
//
//        private boolean isInWorld() {
//            return this.player.world.getEntityById(this.player.getId()) == this.player;
//        }
//
//        @Override
//        public void teardown(RemnantState newHandler) {
//            if (!player.world.isClient) {
//                Pal.revokeAbility(this.player, VanillaAbilities.INVULNERABLE, SOUL_STATE);
//                if (this.isInWorld()) {
//                    PossessionComponent possessionComponent = PossessionComponent.get(this.player);
//                    MobEntity rat = possessionComponent.getHost();
//
//                    if (rat != null) {
//                        rat.remove(Entity.RemovalReason.DISCARDED);
//                        possessionComponent.stopPossessing(false);
//                    }
//                }
//            }
//        }
//
//        @Override
//        public boolean isIncorporeal() {
//            return !PossessionComponent.get(this.player).isPossessionOngoing();
//        }
//
//        @Override
//        public boolean isVagrant() {
//            return true;
//        }
//
//        @Override
//        public boolean setVagrant(boolean vagrant) {
//            return false;   // you're in with this rat, forever
//        }
//
//        @Override
//        public boolean canDissociateFrom(MobEntity possessed) {
//            return false;
//        }
//
//        @Override
//        public void prepareRespawn(ServerPlayerEntity original, boolean lossless) {
//            ((MobResurrectable) player).setResurrectionEntity(new RatEntity(Mischief.RAT, player.world));
//        }
//
//        @Override
//        public void serverTick() {
//            MobEntity possessedEntity = PossessionComponent.get(this.player).getHost();
//            if (possessedEntity instanceof RatEntity rat) {
//                rat.setEating(this.player.isUsingItem());
//            } else {  // make them respawn on death, or kill them if they're humans
//                this.player.kill(); // haha get fucked nerd
//            }
//        }
//    }
//}
