package zabi.minecraft.fighterspath;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zabi.minecraft.fighterspath.lib.Reference;

@Config(modid = Reference.ID)
@Mod.EventBusSubscriber
public class ModConfig {
	
	public static int scoreLevel1 = 500;
	public static int scoreLevel2 = 1000;
	public static int scoreLevel3 = 2000;
	public static int scoreLevel4 = 4000;
	public static int scoreLevel5 = 7000;
	
	@Config.Comment(value =  "This value will be multiplied by the hardness of the block. So Obsidian gives 5 times this amount to the score" )
	public static int scorePerBlockMined = 10;
	public static int scorePerDamageDealt = 20;
	public static int scorePerJump = 1;
	public static int scorePerSecondSprinting = 1;
	
	@Config.Comment("When set to true, re-applying the potion will make the player lose progress")
	public static boolean potionDecreasesScore = false;
	
	@Config.Comment("When set to true, creative players won't increase their score")
	public static boolean preventCreativePlayers = false;
	
	@SubscribeEvent
	public static void configReload(ConfigChangedEvent evt) {
		if (evt.getModID().equals(Reference.ID)) {
			ConfigManager.sync(Reference.ID, Type.INSTANCE);
			DimensionManager.getWorld(0).getMinecraftServer().getPlayerList().getPlayers().forEach(p -> Events.checkLevelling(p.getCapability(PlayerStats.CAP, null), p));
		}
	}
	
}
