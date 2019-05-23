package zabi.minecraft.fighterspath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import zabi.minecraft.fighterspath.PlayerStats;
import zabi.minecraft.fighterspath.lib.Reference;
import zabi.minecraft.minerva.client.hud.IHudComponent;

public class ScoreHud implements IHudComponent {

	public static final ScoreHud INSTANCE = new ScoreHud();

	private static final ResourceLocation id = new ResourceLocation(Reference.ID, "textures/hud/bar.png");
	private static final int HOLD_TIME = 80;
	private static final int FADE_TIME = 20;
	private static final int FADE_IN_INCREASE = 2; 

	private int alpha = FADE_TIME;
	private int current_time = HOLD_TIME;

	private float lastPc = 0;

	private ScoreHud() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clientTick(ClientTickEvent evt) {
		if (evt.phase == Phase.END) {
			if (current_time > 0) {
				current_time--;
				if (alpha < FADE_TIME) {
					alpha += FADE_IN_INCREASE;
				}
			} else if (alpha > 0) {
				alpha--;
			}
			if (Minecraft.getMinecraft().player != null) {
				PlayerStats ps = Minecraft.getMinecraft().player.getCapability(PlayerStats.CAP, null);
				float exp = getPercentageFilled(ps);
				if (Math.abs(exp - lastPc) > 0.01) {
					current_time = HOLD_TIME;
					lastPc = exp;
				}
			}
		}
	}

	@Override
	public void drawAt(int x, int y, int w, int h, RenderMode mode) {
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(id);
		PlayerStats ps = Minecraft.getMinecraft().player.getCapability(PlayerStats.CAP, null);
		float ft = alpha / (float) FADE_TIME;
		if (mode == RenderMode.NORMAL) {
			GlStateManager.color(1, 1, 1, ft);
		}
		float pc = mode == RenderMode.NORMAL?getPercentageFilled(ps):System.currentTimeMillis()%5000/5000f;
		String level = mode == RenderMode.NORMAL?ps.level+"":""+(int)(pc*6);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, 181, 10);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 5f, (int) (w*pc), h, 181, 10);
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		int px = x + 1 + (w - fr.getStringWidth(level)) / 2;
		int py = y + 9;
		RenderHelper.enableStandardItemLighting();
		int black = 0x0;
		int color = 0x00FFFF;
		if (mode == RenderMode.NORMAL) {
			GlStateManager.color(1, 1, 1, ft);
			int fade = (0xFF & (int) (0xFF * ft)) << 24;
			black = black|fade;
			color = color|fade;
		}
		fr.drawString(level, (float)(px + 1), (float)py, black, false);
		fr.drawString(level, (float)(px - 1), (float)py, black, false);
		fr.drawString(level, (float)px, (float)(py + 1), black, false);
		fr.drawString(level, (float)px, (float)(py - 1), black, false);
		fr.drawString(level, (float)px, (float)py, color, false);
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
		return ps != null && alpha > 0 && ps.hasPotion && ps.level < 5;
	}

	@Override
	public String getTitleTranslationKey() {
		//		HudStatus hs = HudController.INSTANCE.components.get(0).getStatus();
		//		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		//		return ""+(sr.getScaledHeight()-hs.getVerticalAnchor().dataToPixel(hs.getY(), hs.getHeight(), sr.getScaledHeight()));
		return "fighterspath.bar_name";
	}



}
