package ladysnake.ratsmischief.common.util;

import net.minecraft.client.render.entity.EntityRendererFactory;

public interface PlayerEntityRenderStateWrapper {
	boolean mischief$holdingRatInLeftHand();
	boolean mischief$holdingRatInRightHand();
	void mischief$holdRatInLeftHand(boolean bool);
	void mischief$holdRatInRightHand(boolean bool);

}
