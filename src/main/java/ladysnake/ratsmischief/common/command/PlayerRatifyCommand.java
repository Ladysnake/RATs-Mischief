package ladysnake.ratsmischief.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ladysnake.ratsmischief.common.cca.PlayerRatComponent;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Iterator;

public class PlayerRatifyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ratify").requires((serverCommandSource) -> {
            return serverCommandSource.hasPermissionLevel(2);
        })).executes((commandContext) -> {
            return execute((ServerCommandSource)commandContext.getSource(), ImmutableList.of(((ServerCommandSource)commandContext.getSource()).getPlayer()));
        })).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes((commandContext) -> {
            return execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"));
        })));
    }

    private static int execute(ServerCommandSource source, Collection<? extends PlayerEntity> targets) {
        if (FabricLoader.INSTANCE.isModLoaded("requiem")) {
            Iterator var2 = targets.iterator();

            while (var2.hasNext()) {
                PlayerEntity player = (PlayerEntity) var2.next();
                PlayerRatComponent.KEY.get(player).isRat = true;
            }

            if (targets.size() == 1) {
                source.sendFeedback(new TranslatableText("commands.ratify.success.single", new Object[]{(targets.iterator().next()).getDisplayName()}), true);
            } else {
                source.sendFeedback(new TranslatableText("commands.ratify.success.multiple", new Object[]{targets.size()}), true);
            }
        } else {
            source.sendError(new TranslatableText("commands.ratify.error.requiresRequiem"));
        }

        return targets.size();
    }
}
