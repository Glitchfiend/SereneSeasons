/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.asm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import cpw.mods.fml.common.asm.transformers.ModAccessTransformer;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import sereneseasons.asm.crops.PlantGrowthTransformer;

public class SSLoadingPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {
            "sereneseasons.asm.transformer.ColorTransformer",
            "sereneseasons.asm.transformer.WeatherTransformer",
            "sereneseasons.asm.crops.PlantGrowthTransformer"
		};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

	private boolean isAppleCore(String pathJar)
	{
		File modsDir = null;
		try
		{
			Field field = CoreModManager.class.getDeclaredField("mcDir");
			field.setAccessible(true);
			modsDir = (File)field.get(null);
			modsDir = new File(modsDir, "mods");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		JarFile jar = null;
		try
		{
			String jarPath = new File(modsDir, pathJar).getAbsolutePath();
			jar = new JarFile(jarPath);
			if (jar.getManifest() == null)
				return false;
			ModAccessTransformer.addJar(jar);
			Attributes mfAttributes = jar.getManifest().getMainAttributes();
			String plugin = mfAttributes.getValue("FMLCorePlugin");
			if (plugin == null)
				return false;
			return plugin.equals("squeek.applecore.AppleCore");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jar != null)
			{
				try
				{
					jar.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return false;
	}

    @Override
    public void injectData(Map<String, Object> data) 
    {
		boolean hasAppleCore = false;
		for (String mod : CoreModManager.getReparseableCoremods())
		{
			if (isAppleCore(mod))
				hasAppleCore = true;
		}
		PlantGrowthTransformer.HasAppleCore = hasAppleCore;
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
