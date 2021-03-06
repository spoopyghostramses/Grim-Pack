package com.grim3212.mc.pack.tools.items;

import java.util.ArrayList;
import java.util.List;

import com.grim3212.mc.pack.core.item.ItemManual;
import com.grim3212.mc.pack.core.manual.pages.Page;
import com.grim3212.mc.pack.core.util.NBTHelper;
import com.grim3212.mc.pack.tools.client.ManualTools;
import com.grim3212.mc.pack.tools.config.ToolsConfig;
import com.grim3212.mc.pack.tools.entity.EntityBlockPushPull;
import com.grim3212.mc.pack.tools.util.EnumPowerStaffModes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPowerStaff extends ItemManual {

	public static ArrayList<Block> allowedBlocks = new ArrayList<Block>();

	protected ItemPowerStaff() {
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public Page getPage(ItemStack stack) {
		return ManualTools.powerstaff_page;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String mode = NBTHelper.getString(stack, "Mode");
		tooltip.add(I18n.format("tooltip.powerstaff.currentMode") + I18n.format("grimtools.powerstaff." + mode));
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		NBTHelper.setString(stack, "Mode", EnumPowerStaffModes.FLOAT_PUSH.getUnlocalized());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return stack.getItemDamage() == 0 ? super.getUnlocalizedName() + "_push" : super.getUnlocalizedName() + "_pull";
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);

		EnumPowerStaffModes mode = EnumPowerStaffModes.getFromString(NBTHelper.getString(stack, "Mode"));
		int xMov = 0;
		int zMov = 0;
		int yMov = 0;
		BlockPos blockpos = new BlockPos(playerIn);
		switch (facing) {
		// Intentionally verbose for clarity. May benefit from optimization.
		case UP:
			if (stack.getItemDamage() == 0) {
				yMov = -1;
			} else {
				yMov = 1;
			}
			if (blockpos.getX() == pos.getX() && blockpos.getY() == pos.getY() + 1 && blockpos.getZ() == pos.getZ())
				playerIn.setPosition(playerIn.posX, playerIn.posY + yMov, playerIn.posZ);
			break;
		case DOWN:
			// yMov = -1;
			if (stack.getItemDamage() == 0) {
				yMov = 1;
			} else {
				yMov = -1;
			}
			if (blockpos.getX() == pos.getX() && blockpos.getY() == pos.getY() + 1 && blockpos.getZ() == pos.getZ())
				playerIn.setPosition(playerIn.posX, playerIn.posY + yMov, playerIn.posZ);
			break;
		case EAST:
			// xMov = 1;
			if (stack.getItemDamage() == 0) {
				xMov = -1;
			} else {
				xMov = 1;
			}
			// To ride horizontally, player needs to sneak on the edge of the
			// block which seems to make the blockpos and pos values off by 1.
			if (Math.abs(blockpos.getX() - pos.getX()) <= 1 && blockpos.getY() == pos.getY() + 1 && Math.abs(blockpos.getZ() - pos.getZ()) <= 1)
				playerIn.setPosition(playerIn.posX + xMov, playerIn.posY, playerIn.posZ);
			break;
		case WEST:
			// xMov = -1;
			if (stack.getItemDamage() == 0) {
				xMov = 1;
			} else {
				xMov = -1;
			}

			if (Math.abs(blockpos.getX() - pos.getX()) <= 1 && blockpos.getY() == pos.getY() + 1 && Math.abs(blockpos.getZ() - pos.getZ()) <= 1)
				playerIn.setPosition(playerIn.posX + xMov, playerIn.posY, playerIn.posZ);
			break;
		case SOUTH:
			// zMov = 1;
			if (stack.getItemDamage() == 0) {
				zMov = -1;
			} else {
				zMov = 1;
			}

			if (Math.abs(blockpos.getX() - pos.getX()) <= 1 && blockpos.getY() == pos.getY() + 1 && Math.abs(blockpos.getZ() - pos.getZ()) <= 1)
				playerIn.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ + zMov);
			break;
		case NORTH:
			// zMov = -1;
			if (stack.getItemDamage() == 0) {
				zMov = 1;
			} else {
				zMov = -1;
			}

			if (Math.abs(blockpos.getX() - pos.getX()) <= 1 && blockpos.getY() == pos.getY() + 1 && Math.abs(blockpos.getZ() - pos.getZ()) <= 1)
				playerIn.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ + zMov);
			break;
		default:
			return EnumActionResult.FAIL;
		}

		IBlockState state = worldIn.getBlockState(pos);
		// Not-allowed blocks no matter what
		if (state.getBlock() == null || state.getMaterial().isLiquid() || state.getBlock() == Blocks.FIRE || state.getBlock() == Blocks.SNOW_LAYER || state.getBlock() instanceof BlockDoublePlant || state.getBlock() instanceof BlockContainer)
			return EnumActionResult.FAIL;

		if (ToolsConfig.restrictPowerStaffBlocks && allowedBlocks.contains(state.getBlock())) {
			onPower(mode, state, worldIn, pos, xMov, zMov, yMov);
		} else if (!ToolsConfig.restrictPowerStaffBlocks) {
			// Default not-allowed blocks
			if (state.getBlock() != Blocks.BEDROCK || state.getBlock() != Blocks.OBSIDIAN)
				onPower(mode, state, worldIn, pos, xMov, zMov, yMov);
		}

		return EnumActionResult.SUCCESS;
	}

	private void onPower(EnumPowerStaffModes mode, IBlockState state, World worldIn, BlockPos pos, int xMov, int zMov, int yMov) {
		if (mode == EnumPowerStaffModes.FLOAT_PUSH || mode == EnumPowerStaffModes.FLOAT_PULL) {
			worldIn.setBlockToAir(pos);
			worldIn.setBlockState(pos.east(xMov).south(zMov).up(yMov), state);
		} else if (mode == EnumPowerStaffModes.GRAVITY_PUSH || mode == EnumPowerStaffModes.GRAVITY_PULL)
			if (state.getBlock() instanceof BlockFalling) {
				worldIn.setBlockToAir(pos);
				worldIn.setBlockState(pos.east(xMov).south(zMov).up(yMov), state);
			} else {
				EntityBlockPushPull blockpushpull = new EntityBlockPushPull(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, worldIn.getBlockState(pos));
				blockpushpull.motionX = 0.29999999999999999D * (double) xMov;
				blockpushpull.motionZ = 0.29999999999999999D * (double) zMov;
				worldIn.spawnEntity(blockpushpull);
			}
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		ItemStack self = new ItemStack(itemIn, 1, 0);
		subItems.add(NBTHelper.setStringItemStack(self, "Mode", EnumPowerStaffModes.FLOAT_PUSH.getUnlocalized()));
		// subItems.add(new ItemStack(itemIn, 1, 0));
		// subItems.add(new ItemStack(itemIn, 1, 1));
	}
}
