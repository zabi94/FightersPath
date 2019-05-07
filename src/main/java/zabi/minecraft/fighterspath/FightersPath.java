package zabi.minecraft.fighterspath;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zabi.minecraft.fighterspath.lib.Log;
import zabi.minecraft.fighterspath.lib.Reference;
import zabi.minecraft.fighterspath.proxy.CommonProxy;
import zabi.minecraft.minerva.common.capability.SimpleCapability;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VER, certificateFingerprint = Reference.FINGERPRINT, dependencies = Reference.DEPS)
public class FightersPath {
	
	@SidedProxy(clientSide = "zabi.minecraft.fighterspath.proxy.ClientProxy", serverSide = "zabi.minecraft.fighterspath.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		SimpleCapability.preInit(PlayerStats.class);
		MinecraftForge.EVENT_BUS.register(Events.class);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		SimpleCapability.init(PlayerStats.class, Reference.ID, PlayerStats.CAP, new PlayerStats());
		proxy.setupHud();
	}
	
	@EventHandler
	public void onFingerprintError(FMLFingerprintViolationEvent evt) {
		Log.e(" -------------------- ");
		Log.e("!! WARNING:");
		Log.e("| The mod "+Reference.NAME+" has an invalid signature.");
		Log.e("| This might mean that someone messed with the files, and might contain viruses!");
		Log.e("| Be sure to ONLY download mods from their official source, likely curseforge!");
		Log.e(" -------------------- ");
	}
	
}
