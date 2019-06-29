package zabi.minecraft.fighterspath;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import zabi.minecraft.fighterspath.lib.Reference;
import zabi.minecraft.fighterspath.potion.PotionUnlock;

public class Events {

	private static PotionUnlock potion = null;
	public static PotionType potionType = null;

	private static final UUID ARMOR = UUID.fromString("ad1e1594-3f45-4455-a9b6-26d18d169d19");
	private static final UUID MOVEMENT_SPEED = UUID.fromString("455b75b7-afc8-4482-872e-1882056c08b9");
	private static final UUID STRENGTH = UUID.fromString("455baab7-afc8-4482-872e-1882056c08b9");

	@SubscribeEvent
	public static void registerPotion(RegistryEvent.Register<Potion> evt) {
		potion = new PotionUnlock();
		evt.getRegistry().register(potion);
	}

	@SubscribeEvent
	public static void registerPotionType(RegistryEvent.Register<PotionType> evt) {
		potionType = new PotionType(new PotionEffect(potion));
		potionType.setRegistryName(new ResourceLocation(Reference.ID, "fighters_brew"));
		evt.getRegistry().register(potionType);
	}

	@SubscribeEvent
	public static void onJump(LivingJumpEvent evt) {
		if (evt.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) evt.getEntityLiving();
			PlayerStats ps = player.getCapability(PlayerStats.CAP, null);
			if (isArmorless(player)) {
				if (!evt.getEntityLiving().world.isRemote && ps.hasPotion && ps.track && applies(player)) {
					ps.score+= ModConfig.scorePerJump;
					ps.markDirty((byte) 3);
					checkLevelling(ps, player);
				}
				player.motionY += 0.125f * ps.level / 5f;
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone evt) {
		if (!evt.getEntityPlayer().world.isRemote) {
			PlayerStats old = evt.getOriginal().getCapability(PlayerStats.CAP, null);
			PlayerStats ply = evt.getEntityPlayer().getCapability(PlayerStats.CAP, null);
			ply.hasPotion = old.hasPotion;
			ply.level = old.level;
			ply.score = old.score;
			ply.ticksSprinting = old.ticksSprinting;
			ply.track = old.track;
			checkLevelling(ply, evt.getEntityPlayer());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onDamageDealt(LivingHurtEvent evt) {
		if (!evt.getEntityLiving().world.isRemote && evt.getSource().getTrueSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) evt.getSource().getTrueSource();
			PlayerStats ps = player.getCapability(PlayerStats.CAP, null);
			if (ps.hasPotion && ps.track && player.getHeldItemMainhand().isEmpty() && applies(player)) {
				ps.score+= (ModConfig.scorePerDamageDealt * evt.getAmount());
				ps.markDirty((byte) 3);
				checkLevelling(ps, player);
			}
		}
	}
	
	@SubscribeEvent
	public static void setCrit(CriticalHitEvent evt) {
		if (!evt.getEntityLiving().world.isRemote) {
			PlayerStats ps = evt.getEntityPlayer().getCapability(PlayerStats.CAP, null);
			if (ps.hasPotion && evt.getEntityPlayer().getHeldItemMainhand().isEmpty() && applies(evt.getEntityPlayer())) {
				if (evt.getEntityPlayer().getRNG().nextInt(20) < ps.level) {
					evt.setResult(Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	public static void mineSpeed(BreakSpeed evt) {
		if (evt.getEntityPlayer().getHeldItemMainhand().isEmpty()) {
			int level = evt.getEntityPlayer().getCapability(PlayerStats.CAP, null).level;
			double pc = 1 + level * 0.7;
			evt.setNewSpeed((float) (evt.getNewSpeed() * pc));
		}
	}

	@SubscribeEvent
	public static void mineLevel(HarvestCheck evt) {
		if (evt.getEntityPlayer().getHeldItemMainhand().isEmpty()) {
			int level = evt.getEntityPlayer().getCapability(PlayerStats.CAP, null).level;
			int required = 2 * evt.getTargetBlock().getBlock().getHarvestLevel(evt.getTargetBlock());
			if (level > required) {
				evt.setCanHarvest(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBlockDug(HarvestDropsEvent evt) {
		if (evt.getHarvester() instanceof EntityPlayer && !evt.getHarvester().world.isRemote) {
			PlayerStats ps = evt.getHarvester().getCapability(PlayerStats.CAP, null);
			if (ps.hasPotion && ps.track && evt.getHarvester().getHeldItemMainhand().isEmpty() && applies(evt.getHarvester())) {
				ps.score += (ModConfig.scorePerBlockMined * evt.getState().getBlockHardness(evt.getWorld(), evt.getPos()));
				ps.markDirty((byte) 3);
				checkLevelling(ps, evt.getHarvester());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerTick(PlayerTickEvent evt) {
		if (!evt.player.world.isRemote) {
			PlayerStats ps = evt.player.getCapability(PlayerStats.CAP, null);
			if (ps.hasPotion && ps.track) {
				if (isArmorless(evt.player) && evt.player.isSprinting() && applies(evt.player)) {
					ps.ticksSprinting++;
					if (ps.ticksSprinting >= 20) {
						ps.ticksSprinting -= 20;
						ps.score += ModConfig.scorePerSecondSprinting;
						checkLevelling(ps, evt.player);
					}
				}
				if (evt.player.ticksExisted % 100 == 0) {
					ps.markDirty((byte) 3);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onArmorChanged(LivingEquipmentChangeEvent evt) {
		if (evt.getEntityLiving() instanceof EntityPlayer) {
			updateArmorDependentValues((EntityPlayer) evt.getEntityLiving(), evt.getEntityLiving().getCapability(PlayerStats.CAP, null).level, evt);
		}
	}
	
	@SubscribeEvent
	public static void onDifferentItemSelected(LivingEquipmentChangeEvent evt) {
		if (evt.getEntityLiving() instanceof EntityPlayer && evt.getSlot().getSlotType() == Type.HAND) {
			EntityPlayer p = (EntityPlayer) evt.getEntityLiving();
			p.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(STRENGTH);
			if (p.getHeldItemMainhand().isEmpty() && p.getHeldItemOffhand().isEmpty()) {
				applyPunchModifiers(p);
			}
		}
	}

	private static void applyPunchModifiers(EntityPlayer p) {
		int level = p.getCapability(PlayerStats.CAP, null).level;
		p.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier(STRENGTH, "fighterspath_dmg", level, 0));
	}

	public static void checkLevelling(PlayerStats ps, EntityPlayer p) {
		ps.level = getLevelFromScore(ps.score);
		if (ps.level >= 5) {
			ps.track = false;
		}
		ps.markDirty((byte) 1);
		updateArmorDependentValues(p, ps.level, null);
		IAttributeInstance strength_attr = p.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
		strength_attr.removeModifier(STRENGTH);
		if (p.getHeldItemMainhand().isEmpty() && p.getHeldItemOffhand().isEmpty()) {
			applyPunchModifiers(p);
		}
	}


	public static int getLevelFromScore(int score) {
		int level = 0;
		if (score >= ModConfig.scoreLevel5) {
			level = 5;
		} else if (score >= ModConfig.scoreLevel4) {
			level = 4;
		} else if (score >= ModConfig.scoreLevel3) {
			level = 3;
		} else if (score >= ModConfig.scoreLevel2) {
			level = 2;
		} else if (score >= ModConfig.scoreLevel1) {
			level = 1;
		} 
		return level;
	}

	private static void updateArmorDependentValues(EntityPlayer p, int level, LivingEquipmentChangeEvent evtIn) {
		LivingEquipmentChangeEvent evt = evtIn == null ? new LivingEquipmentChangeEvent(p, null, ItemStack.EMPTY, ItemStack.EMPTY) : evtIn;
		IAttributeInstance armor = p.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR);
		IAttributeInstance speed = p.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		armor.removeModifier(ARMOR);
		speed.removeModifier(MOVEMENT_SPEED);
		if (isArmorless(evt, p)) {
			armor.applyModifier(new AttributeModifier(ARMOR, "fighterspath:armor", 3f*level, 0));
			speed.applyModifier(new AttributeModifier(MOVEMENT_SPEED, "fighterspath:speed", 0.05d*level, 2));
		}
	}

	public static boolean isArmorless(EntityPlayer player) {
		return checkArmor(player.inventory.armorInventory.get(0), player.inventory.armorInventory.get(1), player.inventory.armorInventory.get(2), player.inventory.armorInventory.get(3));
	}

	public static boolean isArmorless(LivingEquipmentChangeEvent evt, EntityPlayer p) {
		if (!evt.getTo().isEmpty() && evt.getSlot().getSlotType() == Type.ARMOR) {
			return false;
		}
		return isArmorless(p);
	}

	public static boolean checkArmor(ItemStack head, ItemStack chest, ItemStack pants, ItemStack shoes) {
		return head.isEmpty() && chest.isEmpty() && pants.isEmpty() && shoes.isEmpty();
	}

	private static boolean applies(EntityPlayer player) {
		if (player.isCreative() && ModConfig.preventCreativePlayers) {
			return false;
		}
		return true;
	}
}
