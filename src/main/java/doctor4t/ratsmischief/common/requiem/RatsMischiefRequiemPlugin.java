package doctor4t.ratsmischief.common.requiem;

import baritone.api.fakeplayer.FakeServerPlayerEntity;
import doctor4t.ratsmischief.common.entity.RatEntity;
import ladysnake.requiem.api.v1.RequiemPlugin;
import ladysnake.requiem.api.v1.event.requiem.HumanityCheckCallback;
import ladysnake.requiem.api.v1.event.requiem.InitiateFractureCallback;
import ladysnake.requiem.api.v1.event.requiem.PossessionStartCallback;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.RemnantType;
import ladysnake.requiem.common.RequiemRecordTypes;
import ladysnake.requiem.common.entity.PlayerShellEntity;
import ladysnake.requiem.common.remnant.PlayerBodyTracker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RatsMischiefRequiemPlugin implements RequiemPlugin {
	public static final RatifiedRemnantType RATIFIED_REMNANT_TYPE = new RatifiedRemnantType(RatifiedRemnantType.RatifiedRemnantState::new);
	public static final RatifiedRemnantType SPYING_RAT_REMNANT_TYPE = new RatifiedRemnantType(RatifiedRemnantType.SpyingRatRemnantState::new);

	@Override
	public void onRequiemInitialize() {
		// always allow rat possession for rat players
		PossessionStartCallback.EVENT.register(new Identifier("ratsmischief:possession"), (mobEntity, playerEntity, b) -> {
			RemnantType remnantType = RemnantComponent.get(playerEntity).getRemnantType();
			if (mobEntity instanceof RatEntity && remnantType == RATIFIED_REMNANT_TYPE || remnantType == SPYING_RAT_REMNANT_TYPE) {
				return PossessionStartCallback.Result.ALLOW;
			}
			return PossessionStartCallback.Result.PASS;
		});

		InitiateFractureCallback.EVENT.register(player -> {
			if (RemnantComponent.get(player).getRemnantType() == SPYING_RAT_REMNANT_TYPE) {
				if (PlayerBodyTracker.get(player).getAnchor().flatMap(a -> a.get(RequiemRecordTypes.ENTITY_REF)).flatMap(ptr -> ptr.resolve(player.server)).orElse(null) instanceof PlayerShellEntity body) {
					RemnantComponent.get(player).merge(body);
					return true;
				}
			}
			return false;
		});

		HumanityCheckCallback.EVENT.register(livingEntity -> {
			if (livingEntity instanceof RatEntity) {
				return 2;
			}
			return 0;
		});
	}

	@Override
	public void registerRemnantStates(Registry<RemnantType> registry) {
		Registry.register(registry, new Identifier("ratsmischief:ratified"), RATIFIED_REMNANT_TYPE);
		Registry.register(registry, new Identifier("ratsmischief:spying_rat"), SPYING_RAT_REMNANT_TYPE);
	}
}
