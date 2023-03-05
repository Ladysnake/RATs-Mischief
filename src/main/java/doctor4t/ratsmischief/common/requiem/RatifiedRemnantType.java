package doctor4t.ratsmischief.common.requiem;

import doctor4t.ratsmischief.common.entity.RatEntity;
import doctor4t.ratsmischief.common.init.ModEntities;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.MobResurrectable;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.RemnantState;
import ladysnake.requiem.api.v1.remnant.RemnantType;
import ladysnake.requiem.common.remnant.PlayerBodyTracker;
import ladysnake.requiem.common.remnant.RemnantTypes;
import ladysnake.requiem.core.entity.SoulHolderComponent;
import ladysnake.requiem.core.record.GlobalRecordImpl;
import ladysnake.requiem.core.remnant.MutableRemnantState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RatifiedRemnantType implements RemnantType {
	private final Text name = Text.translatable("ratsmischief:remnant_type.name");
	private final Function<PlayerEntity, RemnantState> stateFactory;

	public RatifiedRemnantType(Function<PlayerEntity, RemnantState> stateFactory) {
		this.stateFactory = stateFactory;
	}

	@Override
	public @NotNull RemnantState create(PlayerEntity player) {
		return stateFactory.apply(player);
	}

	@Override
	public boolean isDemon() {
		return false;
	}

	@Override
	public @NotNull Text getName() {
		return this.name;
	}

	public static class RatifiedRemnantState implements RemnantState {
		public static final AbilitySource SOUL_STATE = Pal.getAbilitySource(new Identifier("mischief", "soul_state"));

		private final PlayerEntity player;
		private boolean removed;

		public RatifiedRemnantState(PlayerEntity player) {
			this.player = player;
		}

		@Override
		public void setup(RemnantState oldHandler) {
			if (!player.world.isClient) {
				Pal.grantAbility(this.player, VanillaAbilities.INVULNERABLE, SOUL_STATE);
				if (this.isInWorld()) {
					PossessionComponent.get(this.player).stopPossessing(false);
					RatEntity rat = new RatEntity(ModEntities.RAT, this.player.world);
					SoulHolderComponent.get(rat).removeSoul();
					rat.copyPositionAndRotation(this.player);
					this.player.world.spawnEntity(rat);
					PossessionComponent.get(this.player).startPossessing(rat);
				}
			}
		}

		private boolean isInWorld() {
			return this.player.world.getEntityById(this.player.getId()) == this.player;
		}

		@Override
		public void teardown(RemnantState newHandler) {
			this.removed = true;

			if (!player.world.isClient) {
				Pal.revokeAbility(this.player, VanillaAbilities.INVULNERABLE, SOUL_STATE);
				if (this.isInWorld()) {
					PossessionComponent possessionComponent = PossessionComponent.get(this.player);
					MobEntity rat = possessionComponent.getHost();

					if (rat != null) {
						rat.remove(Entity.RemovalReason.DISCARDED);
						possessionComponent.stopPossessing(false);
						player.setHealth(player.getHealth());
					}
				}
			}
		}

		@Override
		public boolean isIncorporeal() {
			return !PossessionComponent.get(this.player).isPossessionOngoing();
		}

		@Override
		public boolean isVagrant() {
			return !this.removed;
		}

		@Override
		public boolean setVagrant(boolean vagrant) {
			return false;
		}

		@Override
		public boolean canDissociateFrom(MobEntity possessed) {
			return false;   // you're in with this rat, forever
		}

		@Override
		public void prepareRespawn(ServerPlayerEntity original, boolean lossless) {
			((MobResurrectable) player).setResurrectionEntity(new RatEntity(ModEntities.RAT, player.world));
		}

		@Override
		public void serverTick() {
			MobEntity possessedEntity = PossessionComponent.get(this.player).getHost();
			if (possessedEntity instanceof RatEntity rat) {
				rat.setEating(this.player.isUsingItem());
			} else {  // make them respawn on death, or kill them if they're humans
				this.player.kill(); // haha get fucked nerd
			}
		}
	}

	public static class SpyingRatRemnantState extends MutableRemnantState {
		public SpyingRatRemnantState(PlayerEntity player) {
			super(player);
		}

		@Override
		public void teardown(RemnantState newHandler) {
			super.teardown(newHandler);
			// Absolutely massive brain fix for the requiem bug where damage effects display after the player merged with their shell
			// FIXME Must be removed after it's been properly fixed upstream
			PlayerBodyTracker.get(player).setAnchor(new GlobalRecordImpl(null, null));
		}

		@Override
		public boolean canDissociateFrom(MobEntity possessed) {
			return false;   // we use a custom fracture handler for dissociation
		}

		@Override
		public boolean canSplit(boolean forced) {
			// Allow splitting so that the mirror can work as intended
			return true;
		}

		@Override
		public void prepareRespawn(ServerPlayerEntity original, boolean lossless) {
			this.setVagrant(true);
		}

		@Override
		protected void regenerateBody(LivingEntity body) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void onRespawnAfterDeath() {
			// NO-OP
		}

		@Override
		public void serverTick() {
			MobEntity possessedEntity = PossessionComponent.get(this.player).getHost();
			if (possessedEntity instanceof RatEntity rat) {
				rat.setEating(this.player.isUsingItem());
			} else if (!this.isVagrant()) {
				RemnantComponent.get(this.player).become(RemnantTypes.MORTAL);
			} else {
				RatsMischiefRequiemPlugin.goBackToBody((ServerPlayerEntity) this.player);
			}
		}
	}
}
