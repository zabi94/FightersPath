package zabi.minecraft.fighterspath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import zabi.minecraft.fighterspath.PlayerStats;
import zabi.minecraft.fighterspath.lib.Reference;
import zabi.minecraft.minerva.client.hud.IHudComponent;

public class ScoreHud implements IHudComponent {

	private static final ResourceLocation id = new ResourceLocation(Reference.ID, "textures/hud/bar.png");
	
	@Override
	public void drawAt(int x, int y, int w, int h, RenderMode mode) {
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(id);
		PlayerStats ps = Minecraft.getMinecraft().player.getCapability(PlayerStats.CAP, null);
		float pc = mode == RenderMode.NORMAL?getPercentageFilled(ps):System.currentTimeMillis()%5000/5000f;
		String level = mode == RenderMode.NORMAL?ps.level+"":""+(int)(pc*6);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, 181, 10);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 5f, (int) (w*pc), h, 181, 10);
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		int px = x + 1 + (w - fr.getStringWidth(level)) / 2;
		int py = y + 9;
		RenderHelper.enableStandardItemLighting();
		fr.drawString(level, (float)(px + 1), (float)py, 0, false);
        fr.drawString(level, (float)(px - 1), (float)py, 0, false);
        fr.drawString(level, (float)px, (float)(py + 1), 0, false);
        fr.drawString(level, (float)px, (float)(py - 1), 0, false);
        fr.drawString(level, (float)px, (float)py, 0x00FFFF, false);
        GlStateManager.color(1, 1, 1);
	}
	
	public static float getPercentageFilled(PlayerStats ps) {
		RenderCache.recalculate();
		int semiScore = ps.score - RenderCache.levelToRequiredScore[ps.level];
		return ((float) semiScore) / (float) RenderCache.semiLevelScore;
	}

	@Override
	public ResourceLocation getIdentifier() {
		return id;
	}

	@Override
	public boolean isShown() {
		if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.isCreative()) {
			return false;
		}
		PlayerStats ps = Minecraft.getMinecraft().player.getCapability(PlayerStats.CAP, null);
		return ps != null && ps.hasPotion && ps.level < 5;
	}
	
	@Override
	public String getTitleTranslationKey() {
//		HudStatus hs = HudController.INSTANCE.components.get(0).getStatus();
//		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//		return ""+(sr.getScaledHeight()-hs.getVerticalAnchor().dataToPixel(hs.getY(), hs.getHeight(), sr.getScaledHeight()));
		return "fighterspath.bar_name";
	}

}
