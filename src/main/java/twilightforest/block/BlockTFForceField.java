package twilightforest.block;

import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import twilightforest.item.TFItems;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTFForceField extends BlockPane {

    private static final List<EnumDyeColor> COLORS = ImmutableList.of(EnumDyeColor.PURPLE, EnumDyeColor.PINK, EnumDyeColor.ORANGE, EnumDyeColor.GREEN, EnumDyeColor.BLUE);
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class, COLORS);
	
    protected BlockTFForceField() {
        super(Material.GRASS, false);
		this.setLightLevel(2F / 15F);
		this.setCreativeTab(TFItems.creativeTab);
        this.setDefaultState(blockState.getBaseState()
            .withProperty(NORTH, false).withProperty(SOUTH, false)
            .withProperty(WEST, false).withProperty(EAST, false)
            .withProperty(COLOR, EnumDyeColor.PURPLE));
	}

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, SOUTH, WEST, EAST, COLOR);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return COLORS.indexOf(state.getValue(COLOR));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COLOR, COLORS.get(meta));
    }

    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
    	return 15 << 20 | 15 << 4;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }
    
	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List)
    {
        for (int i = 0; i < COLOR.getAllowedValues().size(); i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity)
    {
        super.addCollisionBoxToList(state, world, pos, aabb, list, entity);

    	// just fill in the whole bounding box when we connect on all sides
        if (state.getValue(NORTH) && state.getValue(SOUTH) & state.getValue(WEST) && state.getValue(EAST)) {
            addCollisionBoxToList(pos, aabb, list, FULL_BLOCK_AABB);
        }

        // todo 1.9 unneeded?
        // manually add to the list, since super.method is overwritten
        AxisAlignedBB myAABB = this.getCollisionBoundingBox(state, world, pos);

        if (myAABB != null && aabb.intersectsWith(myAABB))
        {
        	list.add(myAABB);
        }

    }

}