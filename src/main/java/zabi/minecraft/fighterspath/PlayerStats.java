package zabi.minecraft.fighterspath;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import zabi.minecraft.minerva.common.capability.SimpleCapability;
import zabi.minecraft.minerva.common.utils.annotation.DontSync;

public class PlayerStats extends SimpleCapability {
	
	@CapabilityInject(PlayerStats.class)
	public static final Capability<PlayerStats> CAP = null;
	
	@DontSync public int ticksSprinting = 0;
	@DontSync public boolean track = true;
	
	public boolean hasPotion = false;
	public int score = 0;
	public int level = 0;

	@Override
	public SimpleCapability getNewInstance() {
		return new PlayerStats();
	}

	@Override
	public boolean isRelevantFor(Entity arg0) {
		return arg0 instanceof EntityPlayer;
	}
	
}
