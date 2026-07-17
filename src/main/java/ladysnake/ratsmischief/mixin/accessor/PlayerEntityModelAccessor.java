package ladysnake.ratsmischief.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerEntityModel.class)
public interface PlayerEntityModelAccessor {
	@Accessor("thinArms")
	boolean ratsmischief$thinArms();

	@Accessor("parts")
	List<ModelPart> ratsmischief$parts();
}
