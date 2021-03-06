/*******************************************************************************
 * Copyright 2014-2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package biomesoplenty.common.block;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import biomesoplenty.api.item.BOPItems;
import biomesoplenty.common.item.ItemBOPBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBOPMud extends Block implements IBOPBlock, ISustainsPlantType
{
	protected static final AxisAlignedBB MUD_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

    // add properties
    public enum MudType implements IStringSerializable
    {
        MUD;
        @Override
        public String getName()
        {
            return this.name().toLowerCase(Locale.ENGLISH);
        }
        @Override
        public String toString()
        {
            return this.getName();
        }
    }

    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", MudType.class);
    @Override
    protected BlockStateContainer createBlockState() {return new BlockStateContainer(this, VARIANT);}
    
    
    // implement IBOPBlock
    @Override
    public Class<? extends ItemBlock> getItemClass() { return ItemBOPBlock.class; }
    @Override
    public IProperty[] getPresetProperties() { return new IProperty[] {VARIANT}; }
    @Override
    public IProperty[] getNonRenderingProperties() { return null; }
    @Override
    public String getStateName(IBlockState state)
    {
        return ((MudType) state.getValue(VARIANT)).getName();
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IBlockColor getBlockColor() { return null; }
    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() { return null; }
    
    public BlockBOPMud() {
        
        // TODO: use a custom material and sount type for mud? A squelching sound?
        super(Material.GROUND);
        
        // set some defaults
        this.setHarvestLevel("shovel", 0);
        this.setHardness(0.6F);
        this.setSoundType(new SoundType(1.0F, 0.5F, SoundEvents.BLOCK_SLIME_BREAK, SoundEvents.BLOCK_SLIME_STEP, SoundEvents.BLOCK_SLIME_PLACE, SoundEvents.BLOCK_SLIME_HIT, SoundEvents.BLOCK_SLIME_FALL));
        this.setDefaultState( this.blockState.getBaseState().withProperty(VARIANT, MudType.MUD) );
    }    
    
    // map from state to meta and vice verca
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, MudType.values()[meta]);
    }
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((MudType) state.getValue(VARIANT)).ordinal();
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return MUD_AABB;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        entity.motionX *= 0.4D;
        entity.motionZ *= 0.4D;
    }
    
    // drop 4 balls of mud instead of one block
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return BOPItems.mudball;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 4;
    }    
    
    @Override
    public boolean canSustainPlantType(IBlockAccess world, BlockPos pos, EnumPlantType plantType)
    {  
        return false;
    }
    
    
    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
    {
        return this.canSustainPlantType(world, pos, plantable.getPlantType(world, pos.offset(direction)));
    }
    
}