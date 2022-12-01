package com.mujmajnkraft.bettersurvival;

import java.io.File;

import com.mujmajnkraft.bettersurvival.capabilities.extendedarrowproperties.ArrowProperties;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.spearsinentity.SpearsIn;
import com.mujmajnkraft.bettersurvival.config.ConfigHandler;
import com.mujmajnkraft.bettersurvival.eventhandlers.CommonEventHandler;
import com.mujmajnkraft.bettersurvival.init.*;
import com.mujmajnkraft.bettersurvival.integration.InspirationsCauldronCompat;
import com.mujmajnkraft.bettersurvival.integration.RLCombatCompatEventHandler;
import com.mujmajnkraft.bettersurvival.eventhandlers.TickEventHandler;
import com.mujmajnkraft.bettersurvival.integration.RLCombatCompat;
import com.mujmajnkraft.bettersurvival.packet.BetterSurvivalPacketHandler;
import com.mujmajnkraft.bettersurvival.proxy.CommonProxy;

import com.mujmajnkraft.bettersurvival.tileentities.TileEntityCustomCauldron;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, 
	version = Reference.MOD_VERSION, 
	acceptedMinecraftVersions = Reference.MC_VERSION,
		dependencies = "after-required:reachfix;" +
				"after:bettercombatmod;" +
				"after:iceandfire;" +
				"after:somanyenchantments;" +
				"after:inspirations")
public class BetterSurvival {
	
	@Instance
	public static BetterSurvival instance;
	
	public static File config;

	public static Logger LOG = LogManager.getLogger("bettersurvival");
	
	public static boolean isIafLoaded;
	public static boolean isRLCombatLoaded;
	public static boolean isSMELoaded;
	public static boolean isInspirationsLoaded;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		isIafLoaded = Loader.isModLoaded("iceandfire");
		isRLCombatLoaded = Loader.isModLoaded("bettercombatmod") && RLCombatCompat.isCorrectVersion();
		isSMELoaded = Loader.isModLoaded("somanyenchantments");
		isInspirationsLoaded = Loader.isModLoaded("inspirations") && InspirationsCauldronCompat.inspirationsExtendedCauldron();
		
		proxy.preInit();
		
		ArrowProperties.Register();
		NunchakuCombo.Register();
		SpearsIn.Register();
		
		config = new File(event.getModConfigurationDirectory() + File.separator + Reference.MOD_ID);
		config.mkdirs();
		ConfigHandler.init(new File(config.getPath(), Reference.MOD_ID + ".cfg"));
		
		ModPotions.init();
		
		MinecraftForge.EVENT_BUS.register(new ModItems());
		
		if(!isInspirationsLoaded) MinecraftForge.EVENT_BUS.register(new ModBlocks());
		MinecraftForge.EVENT_BUS.register(new ModPotionTypes());
		MinecraftForge.EVENT_BUS.register(new ModPotions());
		
		MinecraftForge.EVENT_BUS.register(new ModEnchantments());
		
		ModEntities.registerEntities();
	}

	@EventHandler
	public static void Init(FMLInitializationEvent event)
	{
		proxy.init();
		BetterSurvivalPacketHandler.init();
		
		ModItems.setRepairMaterials();

		MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
		if(isRLCombatLoaded) MinecraftForge.EVENT_BUS.register(new RLCombatCompatEventHandler());
		FMLCommonHandler.instance().bus().register(new TickEventHandler());	
		
		if(!isInspirationsLoaded) GameRegistry.registerTileEntity(TileEntityCustomCauldron.class, new ResourceLocation(Reference.MOD_ID, "customcauldron"));
		else InspirationsCauldronCompat.initCauldronRecipes();

		ModCrafting.register();
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
}
