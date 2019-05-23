package zabi.minecraft.fighterspath.proxy;

import net.minecraftforge.common.MinecraftForge;
import zabi.minecraft.fighterspath.client.RenderCache;
import zabi.minecraft.fighterspath.client.ScoreHud;
import zabi.minecraft.minerva.client.hud.EnumHudAnchor;
import zabi.minecraft.minerva.client.hud.HudController;

public class ClientProxy extends CommonProxy {

	@Override
	public void setupHud() {
		HudController.registerNewComponent(ScoreHud.INSTANCE, 0, 52, 181, 5, EnumHudAnchor.CENTER_ABSOLUTE, EnumHudAnchor.END_ABSOLUTE, true);
	}
	
	@Override
	public void registerSidedHandlers() {
		MinecraftForge.EVENT_BUS.register(RenderCache.class);
	}
	
}
