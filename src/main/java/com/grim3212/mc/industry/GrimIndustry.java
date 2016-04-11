package com.grim3212.mc.industry;

import com.grim3212.mc.core.config.GrimConfig;
import com.grim3212.mc.core.manual.ManualRegistry;
import com.grim3212.mc.core.manual.ModSection;
import com.grim3212.mc.core.manual.pages.PageCrafting;
import com.grim3212.mc.core.part.GrimPart;
import com.grim3212.mc.core.proxy.CommonProxy;
import com.grim3212.mc.industry.block.IndustryBlocks;
import com.grim3212.mc.industry.config.IndustryConfig;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = GrimIndustry.modID, name = GrimIndustry.modName, version = GrimIndustry.modVersion, dependencies = "required-after:grimcore")
public class GrimIndustry extends GrimPart {

	@SidedProxy(clientSide = "com.grim3212.mc.industry.IndustryClientProxy", serverSide = COMMON_PROXY)
	public static CommonProxy proxy;

	@Instance(GrimIndustry.modID)
	public static GrimIndustry INSTANCE;

	public static final String modID = "grimindustry";
	public static final String modName = "Grim Industry";
	public static final String modVersion = "1.0.0";

	public GrimIndustry() {
		super(GrimIndustry.modID, GrimIndustry.modName, GrimIndustry.modVersion);
		addItem(new IndustryBlocks());
	}

	@Override
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ModMetadata data = event.getModMetadata();
		data.description = "Grim Industry lets the player mess be a bit more technical.";
		data.url = "http://mods.grim3212.com/mc/" + "my-mods/grim-industry/";
		data.credits = "Thanks to the following authors. Leesgowest, LFalch, mattop101, Nandonalt.";

		proxy.registerModels();
	}

	@Override
	protected Item getCreativeTabIcon() {
		return Item.getItemFromBlock(IndustryBlocks.togglerack);
	}

	@Override
	protected GrimConfig setConfig() {
		return new IndustryConfig();
	}

	@Override
	protected void setupManualPages(ModSection modSection) {
		ManualRegistry.addSection("toggle", modSection).addSubSectionPages(new PageCrafting("recipe", new ItemStack(IndustryBlocks.togglerack)));
	}
}