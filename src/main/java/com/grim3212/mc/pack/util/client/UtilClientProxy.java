package com.grim3212.mc.pack.util.client;

import com.grim3212.mc.pack.core.manual.ManualRegistry;
import com.grim3212.mc.pack.core.manual.ModSection;
import com.grim3212.mc.pack.core.manual.pages.PageImageText;
import com.grim3212.mc.pack.core.proxy.ClientProxy;
import com.grim3212.mc.pack.util.client.event.AutoItemTickHandler;
import com.grim3212.mc.pack.util.client.event.RenderTickHandler;
import com.grim3212.mc.pack.util.config.UtilConfig;

import net.minecraftforge.common.MinecraftForge;

public class UtilClientProxy extends ClientProxy {

	@Override
	public void registerModels() {
		MinecraftForge.EVENT_BUS.register(new AutoItemTickHandler());
		MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
		MinecraftForge.EVENT_BUS.register(new KeyBindHelper());
	}

	@Override
	public void registerManual(ModSection modSection) {
		if (UtilConfig.enableAutoReplace)
			ManualRegistry.addSection("autoitem", modSection).addSubSectionPages(new PageImageText("info", "autoitem.png"));
		if (UtilConfig.enableFusRoDah)
			ManualRegistry.addSection("fusrodah", modSection).addSubSectionPages(new PageImageText("info", "fusrodah.png"));
		ManualRegistry.addSection("time", modSection).addSubSectionPages(new PageImageText("info", "time.png"));
		if (UtilConfig.spawnGraves)
			ManualRegistry.addSection("grave", modSection).addSubSectionPages(new PageImageText("info", "grave.png"));
		if (UtilConfig.doubleDoors)
			ManualRegistry.addSection("doors", modSection).addSubSectionPages(new PageImageText("info", "doors.png"));
		if (UtilConfig.infiniteLava)
			ManualRegistry.addSection("lava", modSection).addSubSectionPages(new PageImageText("info", "lava.png"));
	}

}
