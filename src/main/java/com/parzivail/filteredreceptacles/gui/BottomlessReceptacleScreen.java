package com.parzivail.filteredreceptacles.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import com.parzivail.filteredreceptacles.gui.container.BottomlessReceptacleContainer;
import com.parzivail.filteredreceptacles.util.PlayerUtil;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.util.Identifier;

public class BottomlessReceptacleScreen extends AbstractContainerScreen<BottomlessReceptacleContainer>
{
	private static final Identifier TEXTURE = new Identifier(FilteredReceptacles.MODID, "textures/gui/container/receptacle_bottomless.png");

	public BottomlessReceptacleScreen(BottomlessReceptacleContainer container)
	{
		super(container, PlayerUtil.getPlayerInventory(), container.getReceptacle().getDisplayName());
		this.containerHeight = 140;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY)
	{
		this.font.draw(this.title.asFormattedString(), 8.0F, 6.0F, 0x404040);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 0x404040);

		BottomlessReceptacleEntity bre = this.container.getReceptacle();

		long numItemsStored = bre.getNumItemsStored();

		String numStr = formatNumber(numItemsStored);
		int width = this.font.getStringWidth(numStr);
		int xMin = (this.containerWidth - width) / 2;
		int xMax = xMin + width;
		this.font.draw(numStr, xMin, 26, 0x404040);

		int mX = mouseX - this.x;
		int mY = mouseY - this.y;

		if (mX >= xMin && mX <= xMax && mY >= 25 && mY <= 25 + this.font.fontHeight)
			this.renderTooltip(String.valueOf(String.format("%,d", numItemsStored)), mX, mY + this.font.fontHeight);
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
	protected void drawBackground(float delta, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		this.blit(i, j + 7 * 18, 0, 126, this.containerWidth, 126);
	}
}
