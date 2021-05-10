package ladysnake.ratsmischief.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.function.Consumer;

public class PlayerUnratifyCommand {
    /**Set by {@link ladysnake.ratsmischief.common.MischiefRequiemPlugin}, avoids referencing optional classes*/
    public static Consumer<PlayerEntity> action = (p) -> {};

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("unratify")
                .requires((source) -> source.hasPermissionLevel(2))
                .executes((commandContext) -> execute(commandContext.getSource(), ImmutableList.of(commandContext.getSource().getPlayer())))
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .executes((commandContext) -> execute(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets")))
                )
        );
    }

    private static int execute(ServerCommandSource source, Collection<? extends PlayerEntity> targets) {
        if (FabricLoader.getInstance().isModLoaded("requiem")) {

            for (PlayerEntity player : targets) {
                action.accept(player);
            }

            if (targets.size() == 1) {
                source.sendFeedback(new TranslatableText("commands.unratify.success.single", (targets.iterator().next()).getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslatableText("commands.unratify.success.multiple", targets.size()), true);
            }
        } else {
            source.sendError(new TranslatableText("commands.unratify.error.requiresRequiem"));
        }

        return targets.size();
    }
}
