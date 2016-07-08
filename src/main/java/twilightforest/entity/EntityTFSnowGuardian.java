package twilightforest.entity;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class EntityTFSnowGuardian extends EntityMob {

	public EntityTFSnowGuardian(World par1World) {
		super(par1World);
		
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, null));
        this.setSize(0.6F, 1.8F);
	}

	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }
    
    @Override
    protected String getLivingSound()
    {
    	return TwilightForestMod.ID + ":mob.ice.noise";
    }

    @Override
    protected String getHurtSound()
    {
    	return TwilightForestMod.ID + ":mob.ice.hurt";
    }

    @Override
    protected String getDeathSound()
    {
    	return TwilightForestMod.ID + ":mob.ice.death";
    }
    
    @Override
    protected float getSoundPitch()
    {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.8F;
    }

    protected void addRandomArmor()
    {
        // always random armor
    	
    	// random armor type
    	int type = rand.nextInt(4);
    	
    	this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(this.makeItemForSlot(EntityEquipmentSlot.MAINHAND, type)));
    	
    	this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(this.makeItemForSlot(EntityEquipmentSlot.CHEST, type)));
    	this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(this.makeItemForSlot(EntityEquipmentSlot.HEAD, type)));
    }
    
    protected Item makeItemForSlot(EntityEquipmentSlot slot, int type) {
    	switch (slot) {
		case MAINHAND:
    	default:
    		switch (type) {
    		case 0:
    		default:
    			return TFItems.ironwoodSword;
    		case 1:
    			return TFItems.steeleafSword;
    		case 2:
    			return TFItems.knightlySword;
    		case 3:
    			return TFItems.knightlySword;
    		}
    	case FEET:
    		switch (type) {
    		case 0:
    		default:
    			return TFItems.ironwoodBoots;
    		case 1:
    			return TFItems.steeleafBoots;
    		case 2:
    			return TFItems.knightlyBoots;
    		case 3:
    			return TFItems.arcticBoots;
    		}
    	case LEGS:
    		switch (type) {
    		case 0:
    		default:
    			return TFItems.ironwoodLegs;
    		case 1:
    			return TFItems.steeleafLegs;
    		case 2:
    			return TFItems.knightlyLegs;
    		case 3:
    			return TFItems.arcticLegs;
    		}
    	case CHEST:
    		switch (type) {
    		case 0:
    		default:
    			return TFItems.ironwoodPlate;
    		case 1:
    			return TFItems.steeleafPlate;
    		case 2:
    			return TFItems.knightlyPlate;
    		case 3:
    			return TFItems.arcticPlate;
    		}
    	case HEAD:
    		switch (type) {
    		case 0:
    		default:
    			return TFItems.ironwoodHelm;
    		case 1:
    			return TFItems.steeleafHelm;
    		case 2:
    			return TFItems.knightlyHelm;
    		case 3:
    			return TFItems.arcticHelm;
    		}
    	}
    }
    
    @Override
    protected Item getDropItem()
    {
        return Items.SNOWBALL;
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
    	IEntityLivingData data = super.onSpawnWithEgg(par1EntityLivingData);
        
    	this.addRandomArmor();
    	//this.enchantEquipment();
    	
        return data;

    }
    
    @Override
    public void onLivingUpdate()
    {
    	super.onLivingUpdate();
    	// make snow particles
    	for (int i = 0; i < 3; i++) {
	    	float px = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
	    	float py = this.getEyeHeight() + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
	    	float pz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
	    	
			TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowguardian", this.lastTickPosX + px, this.lastTickPosY + py, this.lastTickPosZ + pz, 0, 0, 0);
    	}

    }
    
    @Override
    public int getMaxSpawnedInChunk()
    {
        return 8;
    }
}