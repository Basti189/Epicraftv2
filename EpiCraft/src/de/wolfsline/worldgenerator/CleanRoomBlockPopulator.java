package de.wolfsline.worldgenerator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class CleanRoomBlockPopulator extends BlockPopulator{

	private byte[] layerDataValues;
	
    protected CleanRoomBlockPopulator(byte[] layerDataValues){
        this.layerDataValues = layerDataValues;
    }

	public void populate(World world, Random random, Chunk chunk)
    {
        if (layerDataValues != null)
        {
            int x = chunk.getX() << 4;
            int z = chunk.getZ() << 4;

            for (int y = 0; y < layerDataValues.length ; y++)
            {
                byte dataValue = layerDataValues[y];
                if (dataValue == 0) continue;
                for (int xx = 0; xx < 16; xx++)
                {
                    for (int zz = 0; zz < 16; zz++)
                    {
                        world.getBlockAt(x + xx, y, z + zz).setData(dataValue);
                    }
                }
            }
        }
    }
}
