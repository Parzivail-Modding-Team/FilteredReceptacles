package com.parzivail.filteredreceptacles.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.gui.container.BasicReceptacleContainer;
import com.parzivail.filteredreceptacles.util.PlayerUtil;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.util.Identifier;

public class BasicReceptacleScreen extends AbstractContainerScreen<BasicReceptacleContainer>
{
	private static final Identifier TEXTURE = new Identifier(FilteredReceptacles.MODID, "textures/gui/container/receptacle_basic.png");

	public BasicReceptacleScreen(BasicReceptacleContainer container)
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
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		this.blit(i, j + 7 * 18, 0, 126, this.containerWidth, this.containerHeight);
	}
}
