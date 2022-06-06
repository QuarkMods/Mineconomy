package me.uquark.mineconomy.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.uquark.mineconomy.Mineconomy;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class TradingPostScreen extends HandledScreen<TradingPostScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Mineconomy.modid, "textures/gui/container/trading_post.png");
    private static final Identifier TEXTURE_READY = new Identifier(Mineconomy.modid, "textures/gui/container/trading_post_ready.png");
    private CheckboxWidget checkboxWidget;

    public TradingPostScreen(TradingPostScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 114 + 6 * 18;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        checkboxWidget = new CheckboxWidget(x+183, y+197, 20, 20, new TranslatableText("block.mineconomy.trading_post.ready"), false);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        if (checkboxWidget.isChecked() && !handler.isUserReady())
            checkboxWidget.onPress();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        RenderSystem.setShaderTexture(0, TEXTURE_READY);
        if (handler.isUserReady())
            drawTexture(matrices, x+7, y+17, 7, 17, 72, 108);
        if (handler.isPartnerReady())
            drawTexture(matrices, x+97, y+17, 97, 17, 72, 108);
        if (handler.isUserReady() && handler.isPartnerReady()) {
            drawTextWithShadow(matrices, client.textRenderer, new LiteralText(String.valueOf(handler.getCountdown())), x+85, y+77, 0xFFFFFF);
        }
        checkboxWidget.renderButton(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean update = checkboxWidget.mouseClicked(mouseX, mouseY, button);
        if (update)
            if (checkboxWidget.isChecked())
                handler.onUserReady(client.player);
            else
                handler.onUserNotReady(client.player);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
