package zabi.minecraft.fighterspath.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import zabi.minecraft.fighterspath.Events;
import zabi.minecraft.fighterspath.ModConfig;
import zabi.minecraft.fighterspath.PlayerStats;
import zabi.minecraft.fighterspath.lib.Reference;

public class PotionUnlock extends Potion {

	public PotionUnlock() {
		super(false, 0xaaaaaa);
		this.setRegistryName(new ResourceLocation(Reference.ID, "fighters_brew"));
		this.setPotionName("fighters_brew");
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase elb, int amplifier, double health) {
		if (elb instanceof EntityPlayer && !elb.world.isRemote) {
//			PlayerStats st = new PlayerStats();
//			elb.getCapability(PlayerStats.CAP, null).deserialize((NBTTagCompound) st.serialize(new NBTTagCompound()));
			PlayerStats pstat = elb.getCapability(PlayerStats.CAP, null);
			if (!pstat.hasPotion) {
				pstat.hasPotion = true;
			} else if (ModConfig.potionDecreasesScore) {
				pstat.score -= Math.min(20, pstat.score);
				pstat.track = true;
				Events.checkLevelling(pstat, (EntityPlayer) elb);
				pstat.markDirty((byte) 8);
			}
		}
	}

}
