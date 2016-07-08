package twilightforest.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import twilightforest.block.enums.DeadrockVariant;
import twilightforest.item.TFItems;

public class BlockTFDeadrock extends Block {

	public static final PropertyEnum<DeadrockVariant> VARIANT = PropertyEnum.create("variant", DeadrockVariant.class);

	protected BlockTFDeadrock() {
		super(Material.ROCK);
        this.setHardness(100F);
		this.setResistance(6000000.0F);
		this.setSoundType(SoundType.STONE);
		this.disableStats();
		this.setCreativeTab(TFItems.creativeTab);
		this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, DeadrockVariant.SURFACE));
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
		return getDefaultState().withProperty(VARIANT, DeadrockVariant.values()[meta]);
	}

	@Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List)
    {
    	for (int i = 0; i < VARIANT.getAllowedValues().size(); i++) {
            par3List.add(new ItemStack(par1, 1, i));
    	}
    }

	@Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }
}