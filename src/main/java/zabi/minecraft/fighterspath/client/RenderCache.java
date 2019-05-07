package zabi.minecraft.fighterspath.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zabi.minecraft.fighterspath.ModConfig;
import zabi.minecraft.fighterspath.PlayerStats;
import zabi.minecraft.fighterspath.lib.Reference;

public class RenderCache {
	
	static int semiLevelScore = Integer.MAX_VALUE;
	static int[] levelToRequiredScore = new int[] {0, ModConfig.scoreLevel1, ModConfig.scoreLevel2, ModConfig.scoreLevel3, ModConfig.scoreLevel4, ModConfig.scoreLevel5};
	
	public static void recalculate() {
		PlayerStats ps = Minecraft.getMinecraft().player.getCapability(PlayerStats.CAP, null);
		if (ps.level < 5) {
			semiLevelScore = levelToRequiredScore[ps.level + 1] - levelToRequiredScore[ps.level];
		} else {
			semiLevelScore = Integer.MAX_VALUE;
		}
	}
	
	public static void onConfigReload() {
		levelToRequiredScore[1] = ModConfig.scoreLevel1;
		levelToRequiredScore[2] = ModConfig.scoreLevel2;
		levelToRequiredScore[3] = ModConfig.scoreLevel3;
		levelToRequiredScore[4] = ModConfig.scoreLevel4;
		levelToRequiredScore[5] = ModConfig.scoreLevel5;
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onConfigChanged(ConfigChangedEvent evt) {
		if (evt.getModID().equals(Reference.ID)) {
			onConfigReload();
		}
	}
	
}
