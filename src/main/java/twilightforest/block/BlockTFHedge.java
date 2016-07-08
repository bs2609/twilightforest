package twilightforest.block;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twilightforest.block.enums.HedgeVariant;
import twilightforest.item.TFItems;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTFHedge extends BlockLeaves {

	public static final PropertyEnum<HedgeVariant> VARIANT = PropertyEnum.create("variant", HedgeVariant.class);
	private static final AxisAlignedBB HEDGE_BB = new AxisAlignedBB(0, 0, 0, 1, 0.9375, 1);

	public int damageDone; 

	protected BlockTFHedge() {
		// todo 1.9 cactus material
		this.damageDone = 3;
		this.setHardness(2F);
		this.setResistance(10F);
		this.setCreativeTab(TFItems.creativeTab);
	}
	
    @Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
		if (state.getValue(VARIANT) == HedgeVariant.HEDGE) {
			return HEDGE_BB;
		} else {
			return FULL_BLOCK_AABB;
		}
    }
    
    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return true;
    }

	@Override
	public BlockPlanks.EnumType getWoodType(int meta) {
		return BlockPlanks.EnumType.DARK_OAK;
	}

	@SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess par1IBlockAccess, BlockPos pos, EnumFacing side)
    {
        Block i1 = state.getBlock();
        return !this.leavesFancy && i1 == this ? false : super.shouldSideBeRendered(state, par1IBlockAccess, pos, side);
    }

    @Override
	public int damageDropped(IBlockState state) {
		if (state.getValue(VARIANT) == HedgeVariant.DARKWOOD_LEAVES) {
			// Darkwood sapling
			return 3;
		} else {
			return getMetaFromState(state);
		}
	}
    
    @Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
		if (state.getValue(VARIANT) == HedgeVariant.HEDGE && shouldDamage(entity)) {
			entity.attackEntityFrom(DamageSource.cactus, damageDone);
		}
    }

    @Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity)
    {
		if (world.getBlockState(pos).getValue(VARIANT) == HedgeVariant.HEDGE && shouldDamage(entity)) {
			entity.attackEntityFrom(DamageSource.cactus, damageDone);
		}
    }

    @Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer entityplayer)
    {
		if (!world.isRemote && world.getBlockState(pos).getValue(VARIANT) == HedgeVariant.HEDGE) {
			world.scheduleUpdate(pos, this, 10);
		}
    }
    
    @Override
	public void harvestBlock(World world, EntityPlayer entityplayer, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
    {
    	super.harvestBlock(world, entityplayer, pos, state, te, stack);
    	if (state.getValue(VARIANT) == HedgeVariant.HEDGE) {
    		entityplayer.attackEntityFrom(DamageSource.cactus, damageDone);
    	}
    }
    
    
	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
    	double range = 4.0; // do we need to get this with a better method than hardcoding it?

    	// find players within harvest range
    	List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(range, range, range));

    	// are they swinging?
    	for (EntityPlayer player : nearbyPlayers) {
    		if (player.isSwingInProgress) {
     			// are they pointing at this block?
    			RayTraceResult mop = getPlayerPointVec(world, player, range);

    			if (mop != null && mop.getBlockPos() != null
					&& world.getBlockState(mop.getBlockPos()).getBlock() == this) {
    				// prick them!  prick them hard!
    				player.attackEntityFrom(DamageSource.cactus, damageDone);

    				// trigger this again!
    				world.scheduleUpdate(pos, this, 10);
    			}
    		}
    	}
    }

	
	/**
	 * [VanillaCopy] Exact copy of Entity.rayTrace
	 * todo 1.9 update it
	 */
	private RayTraceResult getPlayerPointVec(World worldObj, EntityPlayer player, double range) {
        Vec3d position = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d look = player.getLook(1.0F);
        Vec3d dest = position.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
        return worldObj.rayTraceBlocks(position, dest);
	}
	
//	/**
//	 * Is the player swinging, server version.  Ugggh.  Okay, this sucks and we don't really need it
//	 */
//	private boolean isPlayerSwinging(EntityPlayer player) {
//		if (player instanceof EntityPlayerMP) {
//			ItemInWorldManager iiwm = ((EntityPlayerMP)player).itemInWorldManager;
//			// curblockDamage > initialDamage
//			return ((Integer)ModLoader.getPrivateValue(ItemInWorldManager.class, iiwm, 9)).intValue() > ((Integer)ModLoader.getPrivateValue(ItemInWorldManager.class, iiwm, 5)).intValue();
//			
////			for (int i = 0; i < ItemInWorldManager.class.getDeclaredFields().length; i++) {
////				// if we find a boolean in here, just assume that's it for the time being
////				if (ModLoader.getPrivateValue(ItemInWorldManager.class, iiwm, i) instanceof Boolean) {
////					return ((Boolean)ModLoader.getPrivateValue(ItemInWorldManager.class, iiwm, i)).booleanValue();
////				}
////			}
//		}
//		// we didn't find it
//		return false;
//	}

    
    private boolean shouldDamage(Entity entity) {
    	return !(entity instanceof EntitySpider) && !(entity instanceof EntityItem) && !entity.doesEntityNotTriggerPressurePlate();
    }

    @Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
		return state.getValue(VARIANT) == HedgeVariant.DARKWOOD_LEAVES ? 1 : 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return 0;
	}

    @Override
	public int quantityDropped(Random par1Random)
    {
    	return par1Random.nextInt(40) == 0 ? 1 : 0;
    }

    @Override
	public Item getItemDropped(IBlockState state, Random par2Random, int par3)
    {
    	if (state.getValue(VARIANT) == HedgeVariant.DARKWOOD_LEAVES)
    	{
    		return Item.getItemFromBlock(TFBlocks.sapling);
    	}
    	else
    	{
    		return null;
    	}
    }

    @Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(this, 1, getMetaFromState(state));
    }

    @Override
	public void dropBlockAsItemWithChance(World par1World, BlockPos pos, IBlockState state, float par6, int fortune)
    {
    	if (!par1World.isRemote && state.getValue(VARIANT) == HedgeVariant.DARKWOOD_LEAVES)
    	{
    		if (par1World.rand.nextInt(40) == 0)
    		{
    			this.dropBlockAsItem(par1World, pos, state, fortune);
    		}
    	}
    }

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(); // todo 1.9 disable shearing or implement this
	}
}
