package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twilightforest.block.enums.TowerWoodVariant;
import twilightforest.entity.EntityTFTowerTermite;
import twilightforest.item.TFItems;

/**
 * 
 * Tower wood is a type of plank block that forms the walls of Dark Towers
 * 
 * @author Ben
 *
 */
public class BlockTFTowerWood extends Block {
	
	public static final PropertyEnum<TowerWoodVariant> VARIANT = PropertyEnum.create("variant", TowerWoodVariant.class);
	
    public BlockTFTowerWood()
    {
        super(Material.WOOD);
        this.setHardness(40F);
        this.setResistance(10F);
        this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(TFItems.creativeTab);
		this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, TowerWoodVariant.PLAIN));
    }

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, TowerWoodVariant.values()[meta]);
	}
    
    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @Override
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
    	int meta = par1IBlockAccess.getBlockMetadata(x, y, z);

    	if (meta == 0 || meta == 2 || meta == 3 || meta == META_INFESTED) 
    	{
       		// stripes!
        	int value = x * 31 + y * 15 + z * 33;
        	if ((value & 256) != 0)
        	{
        		value = 255 - (value & 255);
        	}
        	value &= 255;
        	value = value >> 1;
        	value |= 128;
        	
        	return value << 16 | value << 8 | value;
    	}
    	else
    	{ 
    		return 16777215;
    	}
    }

    @Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
        par3List.add(new ItemStack(par1, 1, 4));
    }
    
    @Override
	public int damageDropped(IBlockState state) {
    	return getMetaFromState(state);
	}

    @Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
    {
    	if (state.getValue(VARIANT) == TowerWoodVariant.INFESTED)
    	{
    		return 0;
    	}
    	else
    	{
    		return super.quantityDropped(state, fortune, random);
    	}
    }
    
    @Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
    {
    	if (state.getValue(VARIANT) == TowerWoodVariant.INFESTED)
    	{
    		return 0.75F;
    	}
    	else
    	{
    		return super.getBlockHardness(state, world, pos);
    	}
    }
    
    @Override
	public void dropBlockAsItemWithChance(World par1World, BlockPos pos, IBlockState state, float chance, int something)
    {
        if (!par1World.isRemote && state.getValue(VARIANT) == TowerWoodVariant.INFESTED)
        {
            EntityTFTowerTermite termite = new EntityTFTowerTermite(par1World);
            termite.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            par1World.spawnEntityInWorld(termite);
            termite.spawnExplosionParticle();
        }

        super.dropBlockAsItemWithChance(par1World, pos, state, chance, something);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing side) {
    	return 1;
    }

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return 0;
	}

}