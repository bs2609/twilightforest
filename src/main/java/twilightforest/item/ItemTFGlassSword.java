package twilightforest.item;

import net.minecraft.util.EnumParticleTypes;
import twilightforest.TwilightForestMod;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemTFGlassSword extends ItemSword {

	public ItemTFGlassSword(Item.ToolMaterial par2EnumToolMaterial) {
		super(par2EnumToolMaterial);
		this.setCreativeTab(TFItems.creativeTab);
	}

    @Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
    
    @Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLiving, EntityLivingBase par3EntityLiving) {
		boolean result = super.hitEntity(par1ItemStack, par2EntityLiving, par3EntityLiving);
		if (result) {
	    	par1ItemStack.damageItem(1000, par3EntityLiving);
		}
		
		return result;
	}
    
    @Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
    	if (player.worldObj.isRemote) {
			// snow animation!
	        for (int var1 = 0; var1 < 20; ++var1) {
	    		double px = entity.posX + itemRand.nextFloat() * entity.width * 2.0F - entity.width;
				double py = entity.posY + itemRand.nextFloat() * entity.height;
				double pz = entity.posZ + itemRand.nextFloat() * entity.width * 2.0F - entity.width;
				entity.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, px, py, pz, 0, 0, 0, Block.getStateId(Blocks.STAINED_GLASS.getDefaultState()));
	        }
	        
	        player.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1F, 0.5F); // todo 1.9 clientsided only
    	}
        return false;
    }

}