package ladysnake.ratsmischief.common.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import ladysnake.ratsmischief.common.RatsMischief;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class RatHornsComponent implements AutoSyncedComponent {
    public static final ComponentKey<RatHornsComponent> KEY = ComponentRegistry.getOrCreate(RatsMischief.id("rathorns"), RatHornsComponent.class);
    public final PlayerEntity player;
    private boolean horns;

    public RatHornsComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public boolean hasHorns() {
        return this.horns;
    }

    public void setHorns(boolean horns) {
        this.horns = horns;
        this.sync();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbtCompound) {
        this.horns = nbtCompound.getBoolean("horns");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbtCompound) {
        nbtCompound.putBoolean("horns", this.horns);
    }
}