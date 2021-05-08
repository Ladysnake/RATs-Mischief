package ladysnake.ratsmischief.common.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class PlayerRatComponent implements Component {
    public static final ComponentKey<PlayerRatComponent> KEY = ComponentRegistry.getOrCreate(new Identifier("ratsmischief:ratplayer"), PlayerRatComponent.class);

    public boolean isRat;

    @Override
    public void readFromNbt(CompoundTag compoundTag) {
        this.isRat = compoundTag.getBoolean("isRat");
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
        compoundTag.putBoolean("isRat", isRat);
    }
}
