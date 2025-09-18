package ladysnake.ratsmischief.mixin.client;

import ladysnake.ratsmischief.common.util.PlayerEntityRenderStateWrapper;
import ladysnake.ratsmischief.common.util.PlayerEntityRendererWrapper;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements PlayerEntityRenderStateWrapper {
	@Unique
	private boolean holdingRatInLeftHand = false;
	@Unique
	private boolean holdingRatInRightHand = false;

	@Override
	public boolean mischief$holdingRatInLeftHand() {
		return holdingRatInLeftHand;
	}

	@Override
	public boolean mischief$holdingRatInRightHand() {
		return holdingRatInRightHand;
	}

	@Override
	public void mischief$holdRatInLeftHand(boolean bool) {
		holdingRatInLeftHand = bool;
	}

	@Override
	public void mischief$holdRatInRightHand(boolean bool) {
		holdingRatInRightHand = bool;
	}
}
