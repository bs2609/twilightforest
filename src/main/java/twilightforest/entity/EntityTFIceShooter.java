package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;

public class EntityTFIceShooter extends EntityMob implements IRangedAttackMob {

	public EntityTFIceShooter(World par1World) {
		super(par1World);
		
        this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 20, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, true, false, null));
        this.setSize(0.8F, 1.8F);
	}

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }
    
    @Override
    public float getEyeHeight()
    {
        return this.height * 0.6F;
    }
 
    @Override
    protected Item getDropItem()
    {
        return Items.SNOWBALL;
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
	protected String getAmbientSound()
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
	public void onDeath(DamageSource par1DamageSource) {
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer) {
			((EntityPlayer)par1DamageSource.getSourceOfDamage()).addStat(TFAchievementPage.twilightHunter);
		}
	}

	@Override
    public int getMaxSpawnedInChunk()
    {
        return 8;
    }

	@Override
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
    {
		EntityTFIceSnowball entitysnowball = new EntityTFIceSnowball(this.worldObj, this);
        double d0 = par1EntityLivingBase.posX - this.posX;
        double d1 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858D - entitysnowball.posY;
        double d2 = par1EntityLivingBase.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d0, d1 + (double)f1, d2, 0.6F, 12.0F);
        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entitysnowball);
    }
}