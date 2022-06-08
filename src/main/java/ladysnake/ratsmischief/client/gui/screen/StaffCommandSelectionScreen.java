package ladysnake.ratsmischief.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.ratsmischief.common.Mischief;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class StaffCommandSelectionScreen extends Screen {
    static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
    private static final int field_32310 = 128;
    private static final int field_32311 = 128;
    private static final int field_32312 = 26;
    private static final int field_32313 = 5;
    private static final int field_32314 = 31;
    private static final int field_32315 = 5;
    private static final int UI_WIDTH = StaffCommandSelectionScreen.GameMode.values().length * 31 - 5;
    private static final Text SELECT_NEXT_TEXT;

    static {
        SELECT_NEXT_TEXT = Text.translatable("debug.gamemodes.select_next", new Object[]{(Text.translatable("debug.gamemodes.press_f4")).formatted(Formatting.AQUA)});
    }

    private final Optional<StaffCommandSelectionScreen.GameMode> currentGameMode = StaffCommandSelectionScreen.GameMode.of(this.getPreviousGameMode());
    private final List<StaffCommandSelectionScreen.ButtonWidget> gameModeButtons = Lists.newArrayList();
    private Optional<StaffCommandSelectionScreen.GameMode> gameMode = Optional.empty();
    private int lastMouseX;
    private int lastMouseY;
    private boolean mouseUsedForSelection;

    public StaffCommandSelectionScreen() {
        super(NarratorManager.EMPTY);
    }

    private static void apply(MinecraftClient client, Optional<StaffCommandSelectionScreen.GameMode> gameMode) {
        if (client.interactionManager != null && client.player != null && gameMode.isPresent()) {
            Optional<StaffCommandSelectionScreen.GameMode> optional = StaffCommandSelectionScreen.GameMode.of(client.interactionManager.getCurrentGameMode());
            StaffCommandSelectionScreen.GameMode gameMode2 = (StaffCommandSelectionScreen.GameMode) gameMode.get();
            if (optional.isPresent() && client.player.hasPermissionLevel(2) && gameMode2 != optional.get()) {
                client.player.sendChatMessage(gameMode2.getCommand());
            }

        }
    }

    private net.minecraft.world.GameMode getPreviousGameMode() {
        ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
        net.minecraft.world.GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
        if (gameMode != null) {
            return gameMode;
        } else {
            return clientPlayerInteractionManager.getCurrentGameMode() == net.minecraft.world.GameMode.CREATIVE ? net.minecraft.world.GameMode.SURVIVAL : net.minecraft.world.GameMode.CREATIVE;
        }
    }

    protected void init() {
        super.init();
        this.gameMode = this.currentGameMode.isPresent() ? this.currentGameMode : StaffCommandSelectionScreen.GameMode.of(this.client.interactionManager.getCurrentGameMode());

        for (int i = 0; i < StaffCommandSelectionScreen.GameMode.VALUES.length; ++i) {
            StaffCommandSelectionScreen.GameMode gameMode = StaffCommandSelectionScreen.GameMode.VALUES[i];
            this.gameModeButtons.add(new StaffCommandSelectionScreen.ButtonWidget(gameMode, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
        }

    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.checkForClose()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            matrices.push();
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, TEXTURE);
            int i = this.width / 2 - 62;
            int j = this.height / 2 - 31 - 27;
            drawTexture(matrices, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
            matrices.pop();
            super.render(matrices, mouseX, mouseY, delta);
            this.gameMode.ifPresent((gameMode) -> {
                drawCenteredText(matrices, this.textRenderer, gameMode.getText(), this.width / 2, this.height / 2 - 31 - 20, -1);
            });
            drawCenteredText(matrices, this.textRenderer, SELECT_NEXT_TEXT, this.width / 2, this.height / 2 + 5, 16777215);
            if (!this.mouseUsedForSelection) {
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
                this.mouseUsedForSelection = true;
            }

            boolean bl = this.lastMouseX == mouseX && this.lastMouseY == mouseY;
            Iterator var8 = this.gameModeButtons.iterator();

            while (var8.hasNext()) {
                StaffCommandSelectionScreen.ButtonWidget buttonWidget = (StaffCommandSelectionScreen.ButtonWidget) var8.next();
                buttonWidget.render(matrices, mouseX, mouseY, delta);
                this.gameMode.ifPresent((gameMode) -> {
                    buttonWidget.setSelected(gameMode == buttonWidget.gameMode);
                });
                if (!bl && buttonWidget.isHovered()) {
                    this.gameMode = Optional.of(buttonWidget.gameMode);
                }
            }

        }
    }

    private void apply() {
        apply(this.client, this.gameMode);
    }

    private boolean checkForClose() {
        if (InputUtil.isKeyPressed(this.client.getWindow().getHandle(), GLFW.GLFW_KEY_ENTER)) {
            this.apply();
            this.client.setScreen((Screen) null);
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_F4 && this.gameMode.isPresent()) {
            this.mouseUsedForSelection = false;
            this.gameMode = this.gameMode.get().next();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    private static enum GameMode {
        CREATIVE(Text.translatable("gameMode.creative"), "/gamemode creative", new ItemStack(Mischief.HARVEST_STAFF)),
        SURVIVAL(Text.translatable("gameMode.survival"), "/gamemode survival", new ItemStack(Mischief.COLLECTION_STAFF)),
        ADVENTURE(Text.translatable("gameMode.adventure"), "/gamemode adventure", new ItemStack(Mischief.LOVE_STAFF)),
        SPECTATOR(Text.translatable("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.BARRIER));

        protected static final StaffCommandSelectionScreen.GameMode[] VALUES = values();
        protected static final int field_32316 = 5;
        private static final int field_32317 = 16;
        final Text text;
        final String command;
        final ItemStack icon;

        private GameMode(Text text, String command, ItemStack icon) {
            this.text = text;
            this.command = command;
            this.icon = icon;
        }

        static Optional<StaffCommandSelectionScreen.GameMode> of(net.minecraft.world.GameMode gameMode) {
            switch (gameMode) {
                case SPECTATOR:
                    return Optional.of(SPECTATOR);
                case SURVIVAL:
                    return Optional.of(SURVIVAL);
                case CREATIVE:
                    return Optional.of(CREATIVE);
                case ADVENTURE:
                    return Optional.of(ADVENTURE);
                default:
                    return Optional.empty();
            }
        }

        void renderIcon(ItemRenderer itemRenderer, int x, int y) {
            itemRenderer.renderInGuiWithOverrides(this.icon, x, y);
        }

        Text getText() {
            return this.text;
        }

        String getCommand() {
            return this.command;
        }

        Optional<StaffCommandSelectionScreen.GameMode> next() {
            switch (this) {
                case CREATIVE:
                    return Optional.of(SURVIVAL);
                case SURVIVAL:
                    return Optional.of(ADVENTURE);
                case ADVENTURE:
                    return Optional.of(SPECTATOR);
                default:
                    return Optional.of(CREATIVE);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public class ButtonWidget extends ClickableWidget {
        final StaffCommandSelectionScreen.GameMode gameMode;
        private boolean selected;

        public ButtonWidget(StaffCommandSelectionScreen.GameMode gameMode, int x, int y) {
            super(x, y, 26, 26, gameMode.getText());
            this.gameMode = gameMode;
        }

        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            this.drawBackground(matrices, minecraftClient.getTextureManager());
            this.gameMode.renderIcon(StaffCommandSelectionScreen.this.itemRenderer, this.x + 5, this.y + 5);
            if (this.selected) {
                this.drawSelectionBox(matrices, minecraftClient.getTextureManager());
            }

        }

        public void appendNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        public boolean isHovered() {
            return super.isHovered() || this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private void drawBackground(MatrixStack matrices, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, StaffCommandSelectionScreen.TEXTURE);
            matrices.push();
            matrices.translate((double) this.x, (double) this.y, 0.0D);
            drawTexture(matrices, 0, 0, 0.0F, 75.0F, 26, 26, 128, 128);
            matrices.pop();
        }

        private void drawSelectionBox(MatrixStack matrices, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, StaffCommandSelectionScreen.TEXTURE);
            matrices.push();
            matrices.translate((double) this.x, (double) this.y, 0.0D);
            drawTexture(matrices, 0, 0, 26.0F, 75.0F, 26, 26, 128, 128);
            matrices.pop();
        }
    }
}
