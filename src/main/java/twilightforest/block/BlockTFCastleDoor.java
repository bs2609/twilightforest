package twilightforest.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import twilightforest.TFGenericPacketHandler;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.WorldProviderTwilightForest;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTFCastleDoor extends Block
{

    private IIcon activeIcon;
	private boolean isVanished;

	public BlockTFCastleDoor(boolean isVanished)
    {
        super(isVanished ? Material.GLASS : Material.ROCK);
        
        this.isVanished = isVanished;
        this.lightOpacity = isVanished ? 0 : 255;

        this.setCreativeTab(TFItems.creativeTab);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    	//TODO: this is the opposite of object oriented.
    	
    	if (this.isVanished) {
    		this.blockIcon = register.registerIcon(TwilightForestMod.ID + ":castle_door_vanished");
    		this.activeIcon = register.registerIcon(TwilightForestMod.ID + ":castle_door_vanished_active");
    	} else {
    		this.blockIcon = register.registerIcon(TwilightForestMod.ID + ":castle_door");
    		this.activeIcon = register.registerIcon(TwilightForestMod.ID + ":castle_door_active");
    	}
    }
    

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);

        if (isMetaActive(meta)) {
        	return this.activeIcon;
        } else {
        	return this.blockIcon;
        }
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return !this.isVanished;
    }
    
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World par1World, BlockPos pos) {
		return isVanished ? NULL_AABB : super.getCollisionBoundingBox(state, par1World, pos);
	}
	
    @Override
	public boolean isPassable(IBlockAccess par1IBlockAccess, BlockPos pos)
    {
    	return !this.isVanished;
    }

    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int meta = par1World.getBlockMetadata(x, y, z);

        if (!isMetaActive(meta))
        {
        	if (isBlockLocked(par1World, x, y, z))
        	{
        		par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 1.0F, 0.3F);
        	}
        	else
        	{
        		changeToActiveBlock(par1World, x, y, z, meta);
        	}
            return true;
        }
        else 
        {
        	return false;
        }
    }


	/**
	 * Change this block to be active
	 */
    public static void changeToActiveBlock(World par1World, int x, int y, int z, int meta) 
	{
		changeToBlockMeta(par1World, x, y, z, meta | 8);
		playVanishSound(par1World, x, y, z);

		Block blockAt = par1World.getBlock(x, y, z);
		par1World.scheduleBlockUpdate(x, y, z, blockAt, 2 + par1World.rand.nextInt(5));
	}


	/**
     * Change this block into an different device block
     */
	private static void changeToBlockMeta(World par1World, int x, int y, int z, int meta) 
	{
		Block blockAt = par1World.getBlock(x, y, z);
		
		if (blockAt == TFBlocks.castleDoor || blockAt == TFBlocks.castleDoorVanished)
		{
			par1World.setBlock(x, y, z, blockAt, meta, 3);
			par1World.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			par1World.notifyBlocksOfNeighborChange(x, y, z, blockAt);
		}
	}
    
    
	public static boolean isBlockLocked(World par1World, int x, int y, int z) {
		// check if we are in a structure, and if that structure says that we are locked
		
		int meta = par1World.getBlockMetadata(x, y, z);
		
		if (!par1World.isRemote && par1World.provider instanceof WorldProviderTwilightForest) {
			ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest)par1World.provider).getChunkProvider();

			return chunkProvider.isStructureLocked(x, y, z, meta);
		} else {
			return false;
		}
	}

	public static boolean isMetaActive(int meta) {
		return (meta & 8) != 0;
	}

    @Override
    public int tickRate(World world)
    {
        return 5;
    }

    @Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
    	return TwilightForestMod.proxy.getCastleMagicBlockRenderID();
    }
    
    @Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random)
    {
    	if (!par1World.isRemote)
    	{
    		//System.out.println("Update castle door");
    		
    		int meta = par1World.getBlockMetadata(x, y, z);
    		
    		if (this.isVanished) {
    			if (isMetaActive(meta)) {
                	par1World.setBlock(x, y, z, TFBlocks.castleDoor, meta & 7, 3);
                    par1World.notifyBlocksOfNeighborChange(x, y, z, this);
                    playVanishSound(par1World, x, y, z);

                    //par1World.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
    			} else {
                	changeToActiveBlock(par1World, x, y, z, meta);
    			}
    		} else {

    			// if we have an active castle door, turn it into a vanished door block
    			if (isMetaActive(meta))
    			{
    				par1World.setBlock(x, y, z, getOtherBlock(this), meta & 7, 3);
    				par1World.scheduleBlockUpdate(x, y, z, getOtherBlock(this), 80);

    				par1World.notifyBlocksOfNeighborChange(x, y, z, this);
    				playReappearSound(par1World, x, y, z);
    				par1World.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
    				
            		this.sendAnnihilateBlockPacket(par1World, x, y, z);


    				// activate all adjacent inactive doors
    				checkAndActivateCastleDoor(par1World, x - 1, y, z);
    				checkAndActivateCastleDoor(par1World, x + 1, y, z);
    				checkAndActivateCastleDoor(par1World, x, y + 1, z);
    				checkAndActivateCastleDoor(par1World, x, y - 1, z);
    				checkAndActivateCastleDoor(par1World, x, y, z + 1);
    				checkAndActivateCastleDoor(par1World, x, y, z - 1);

    			}
    			
    			// inactive solid door blocks we don't care about updates
    		}

    	}
    }
    
	private void sendAnnihilateBlockPacket(World world, int x, int y, int z) {
		// send packet
		FMLProxyPacket message = TFGenericPacketHandler.makeAnnihilateBlockPacket(x, y, z);

		NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, 64);
		
		TwilightForestMod.genericChannel.sendToAllAround(message, targetPoint);
	}

	private static void playVanishSound(World par1World, int x, int y, int z) {
		par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 0.125f, par1World.rand.nextFloat() * 0.25F + 1.75F);
//		par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "note.harp", 0.2F, par1World.rand.nextFloat() * 2F);
	}

	private static void playReappearSound(World par1World, int x, int y, int z) {
		par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 0.125f, par1World.rand.nextFloat() * 0.25F + 1.25F);
//		par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "note.harp", 0.2F, par1World.rand.nextFloat() * 2F);
	}

	private static Block getOtherBlock(Block block) {
		return block == TFBlocks.castleDoor ? TFBlocks.castleDoorVanished : TFBlocks.castleDoor;
	}

    /**
     * If the targeted block is a vanishing block, activate it
     */
    public static void checkAndActivateCastleDoor(World world, int x, int y, int z) {
    	Block block = world.getBlock(x, y, z);
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	if (block == TFBlocks.castleDoor && !isMetaActive(meta) && !isBlockLocked(world, x, y, z))
    	{
    		changeToActiveBlock(world, x, y, z, meta);
    	}
//    	if (block == TFBlocks.castleDoorVanished && !isMetaActive(meta) && !isBlockLocked(world, x, y, z))
//    	{
//    		changeToActiveBlock(world, x, y, z, meta);
//    	}
	}
    
    
	@Override
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World par1World, BlockPos pos, Random par5Random)
    {
    	int meta = par1World.getBlockMetadata(x, y, z);

    	if (isMetaActive(meta));
    	{
    		for (int i = 0; i < 1; ++i) {
    			//this.sparkle(par1World, x, y, z, par5Random);
    		}
    	}
    }
	

    /**
     * Shine bright like a DIAMOND! (or actually, sparkle like redstone ore)
     */
    public void sparkle(World world, int x, int y, int z, Random rand)
    {
        double offset = 0.0625D;

        for (int side = 0; side < 6; ++side)
        {
            double rx = x + rand.nextFloat();
            double ry = y + rand.nextFloat();
            double rz = z + rand.nextFloat();

            if (side == 0 && !world.getBlock(x, y + 1, z).isOpaqueCube())
            {
                ry = y + 1 + offset;
            }

            if (side == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube())
            {
                ry = y + 0 - offset;
            }

            if (side == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube())
            {
                rz = z + 1 + offset;
            }

            if (side == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube())
            {
                rz = z + 0 - offset;
            }

            if (side == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube())
            {
                rx = x + 1 + offset;
            }

            if (side == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube())
            {
                rx = x + 0 - offset;
            }

            if (rx < x || rx > x + 1 || ry < 0.0D || ry > y + 1 || rz < z || rz > z + 1)
            {
                world.spawnParticle(EnumParticleTypes.REDSTONE, rx, ry, rz, 0.0D, 0.0D, 0.0D);
            }
        }
    }

}