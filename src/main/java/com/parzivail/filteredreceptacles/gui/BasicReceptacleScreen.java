package com.parzivail.filteredreceptacles.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.gui.container.BasicReceptacleContainer;
import com.parzivail.filteredreceptacles.util.PlayerUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BasicReceptacleScreen extends HandledScreen<BasicReceptacleContainer>
{
	private static final Identifier TEXTURE = new Identifier(FilteredReceptacles.MODID, "textures/gui/container/receptacle_basic.png");

	public BasicReceptacleScreen(BasicReceptacleContainer container)
	{
		super(container, PlayerUtil.getPlayerInventory(), container.getReceptacle().getDisplayName());
		this.height = 140;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
	{
		this.textRenderer.draw(matrices, this.title.asOrderedText(), 8.0F, 6.0F, 0x404040);
		this.textRenderer.draw(matrices, this.playerInventory.getDisplayName().asOrderedText(), 8.0F, (float)(this.height - 96 + 2), 0x404040);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
