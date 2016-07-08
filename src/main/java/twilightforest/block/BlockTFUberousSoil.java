package twilightforest.block;

import java.util.Random;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import twilightforest.item.TFItems;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class BlockTFUberousSoil extends Block implements IGrowable {
	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);

	protected BlockTFUberousSoil() {
		super(Material.GROUND);
        this.setLightOpacity(255);
        this.setHardness(0.6F);
        this.setSoundType(SoundType.GROUND);
        this.setTickRandomly(true);
        
		this.setCreativeTab(TFItems.creativeTab);
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return AABB;
	}

	@Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

	@Override
    public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_)
    {
        return Blocks.DIRT.getItemDropped(Blocks.DIRT.getDefaultState(), p_149650_2_, p_149650_3_);
    }

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
    	Material aboveMaterial = world.getBlockState(pos.up()).getMaterial();
    	if (aboveMaterial.isSolid()) {
    		world.setBlockState(pos, Blocks.DIRT.getDefaultState());
    	}    
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
    {
        EnumPlantType plantType = plantable.getPlantType(world, pos.up());
        return plantType == EnumPlantType.Crop ||  plantType == EnumPlantType.Plains ||  plantType == EnumPlantType.Cave;
    }

	@Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbor) {
        IBlockState above = world.getBlockState(pos.up());
        Material aboveMaterial = above.getMaterial();

        if (aboveMaterial.isSolid())
        {
        	world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }

        if (above instanceof IPlantable) {
        	IPlantable plant = (IPlantable)above;
        	// revert to farmland or grass
        	if (plant.getPlantType(world, pos.up()) == EnumPlantType.Crop) {
        		world.setBlockState(pos, Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 2), 2);
        	} else if (plant.getPlantType(world, pos.up()) == EnumPlantType.Plains) {
        		world.setBlockState(pos, Blocks.GRASS.getDefaultState());
        	} else {
        		world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        	}
        	// apply bonemeal
        	ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos.up(), null);
        	ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos.up(), null);
        	ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos.up(), null);
        	ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos.up(), null);
        	// green sparkles
        	if (!world.isRemote) {
        		world.playEvent(2005, pos.up(), 0);
        	}
        }
    }

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean var5) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		if (rand.nextBoolean()) {
			pos = pos.offset(EnumFacing.EAST, rand.nextBoolean() ? 1 : -1);
		} else {
			pos = pos.offset(EnumFacing.SOUTH, rand.nextBoolean() ? 1 : -1);
		}

		Block blockAt = world.getBlockState(pos).getBlock();
		if (world.isAirBlock(pos.up()) && (blockAt == Blocks.DIRT || blockAt == Blocks.GRASS || blockAt == Blocks.FARMLAND)) {
			world.setBlockState(pos, this.getDefaultState());
		}
	}
}