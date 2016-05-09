package com.grim3212.mc.core.part;

import java.util.ArrayList;
import java.util.List;

import com.grim3212.mc.core.GrimCore;
import com.grim3212.mc.core.config.GrimConfig;
import com.grim3212.mc.core.manual.ManualRegistry;
import com.grim3212.mc.core.manual.ModSection;
import com.grim3212.mc.core.part.IPartEntities.IPartTileEntities;
import com.grim3212.mc.core.util.GrimLog;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class GrimPart {

	public static final String COMMON_PROXY = "com.grim3212.mc.core.proxy.CommonProxy";

	public static final List<GrimPart> loadedParts = new ArrayList<GrimPart>();

	private String modid;
	private String name;
	private String version;
	private GrimConfig config;
	private GrimPartCreativeTab creativeTab;
	private ModSection modSection;
	private List<IPartItems> items;
	private List<IPartEntities> entities;
	private List<IPartTileEntities> tileentities;

	public GrimPart(String modid, String name, String version) {
		this(modid, name, version, true);
	}

	public GrimPart(String modid, String name, String version, boolean creativeTab) {
		GrimLog.info(GrimCore.modName, "Registered Grim Part: { " + name + " }");

		this.modid = modid;
		this.name = name;
		this.version = version;
		this.config = setConfig();
		if (creativeTab)
			this.creativeTab = new GrimPartCreativeTab(this);
		ManualRegistry.registerMod(modSection = new ModSection(getName(), getModid()));
		loadedParts.add(this);
		this.items = new ArrayList<IPartItems>();
		this.entities = new ArrayList<IPartEntities>();
		this.tileentities = new ArrayList<IPartTileEntities>();
	}

	public ModSection getModSection() {
		return modSection;
	}

	public void addEntity(IPartEntities entity) {
		this.entities.add(entity);
	}

	public void addItem(IPartItems item) {
		this.items.add(item);
	}

	public void addTileEntity(IPartTileEntities te) {
		this.tileentities.add(te);
	}

	/**
	 * Make sure to add @EventHandler when overridden as well as call super
	 * 
	 * Registers the config change event
	 * 
	 * @param FMLPreInitializationEvent
	 *            event
	 */
	public void preInit(FMLPreInitializationEvent event) {
		ModMetadata data = event.getModMetadata();
		data.autogenerated = false;
		data.version = getVersion();
		data.name = getName();
		data.authorList.add("Grim3212");
		data.logoFile = "assets/" + getModid() + "/" + getModid() + ".png";
		data.url = "http://mods.grim3212.com/mc/my-mods/" + getModid();

		MinecraftForge.EVENT_BUS.register(this);

		// Initialize all items and blocks first
		for (int i = 0; i < this.items.size(); i++) {
			this.items.get(i).initItems();
		}

		// Then render and create recipes
		for (int i = 0; i < this.items.size(); i++) {
			this.items.get(i).addRecipes();
		}

		for (int i = 0; i < this.entities.size(); i++) {
			this.entities.get(i).initEntities();
		}
	}

	/**
	 * Make sure to add @EventHandler when overridden
	 * 
	 * @param FMLInitializationEvent
	 *            event
	 */
	public void init(FMLInitializationEvent event) {
		for (int i = 0; i < this.tileentities.size(); i++) {
			this.tileentities.get(i).initTileEntities();
		}
	}

	/**
	 * Make sure to add @EventHandler when overridden
	 * 
	 * @param FMLPostInitializationEvent
	 *            event
	 */
	public void postInit(FMLPostInitializationEvent event) {
	}

	protected Item getCreativeTabIcon() {
		return null;
	}

	protected abstract GrimConfig setConfig();

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.modID.equals(this.modid)) {
			this.config.syncConfig();
		}
	}

	public String getModid() {
		return modid;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public Configuration getConfig() {
		return config.config;
	}

	public GrimPartCreativeTab getCreativeTab() {
		return creativeTab;
	}
}
