package com.parzivail.filteredreceptacles.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.gui.container.WasteReceptacleScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WasteReceptacleScreen extends HandledScreen<WasteReceptacleScreenHandler>
{
	private static final Identifier TEXTURE = new Identifier(FilteredReceptacles.MODID, "textures/gui/container/receptacle_waste.png");

	public WasteReceptacleScreen(WasteReceptacleScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
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
		this.textRenderer.draw(matrices, this.playerInventoryTitle, 8.0F, (float)(this.height - 96 + 2), 0x404040);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
