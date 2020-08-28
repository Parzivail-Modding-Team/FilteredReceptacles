package com.parzivail.filteredreceptacles.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import com.parzivail.filteredreceptacles.gui.container.BottomlessReceptacleContainer;
import com.parzivail.filteredreceptacles.util.PlayerUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BottomlessReceptacleScreen extends HandledScreen<BottomlessReceptacleContainer>
{
	private static final Identifier TEXTURE = new Identifier(FilteredReceptacles.MODID, "textures/gui/container/receptacle_bottomless.png");

	public BottomlessReceptacleScreen(BottomlessReceptacleContainer container)
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

		BottomlessReceptacleEntity bre = this.handler.getReceptacle();

		long numItemsStored = bre.getNumItemsStored();

		String numStr = formatNumber(numItemsStored);
		int width = this.textRenderer.getWidth(numStr);
		int xMin = (this.backgroundWidth - width) / 2;
		int xMax = xMin + width;
		this.textRenderer.draw(matrices, numStr, xMin, 26, 0x404040);

		int mX = mouseX - this.x;
		int mY = mouseY - this.y;

		if (mX >= xMin && mX <= xMax && mY >= 25 && mY <= 25 + this.textRenderer.fontHeight)
			this.renderTooltip(matrices, Text.of(String.valueOf(String.format("%,d", numItemsStored))), mX, mY + this.textRenderer.fontHeight);
	}

	public String formatNumber(double d)
	{
		if (d < 1000)
			return String.valueOf((int)d);

		char[] incPrefixes = new char[] { ' ', 'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y' };

		int degree = (int)Math.floor(Math.log10(Math.abs(d)) / 3);
		double scaled = d * Math.pow(1000, -degree);

		return (String.format("%.2f", scaled) + incPrefixes[degree]).trim();
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
