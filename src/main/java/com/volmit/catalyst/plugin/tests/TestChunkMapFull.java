package com.volmit.catalyst.plugin.tests;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.volmit.catalyst.api.NMA;
import com.volmit.catalyst.api.ShadowChunk;
import com.volmit.catalyst.plugin.Test;

public class TestChunkMapFull implements Test
{
	@Override
	public String getName()
	{
		return "chunk";
	}

	@Override
	public void execute(Player p, String[] a)
	{
		Chunk c = p.getLocation().getChunk();
		ShadowChunk shadow = NMA.CHUNK.shadow(c);

		for(int i = 0; i < 16; i++)
		{
			for(int k = 0; k < 16; k++)
			{
				shadow.setBiome(i, k, Biome.values()[(int) ((Biome.values().length - 1) * Math.random())]);

				for(int j = 0; j < 255; j += 3)
				{
					shadow.setBlockLight(i, j, k, (int) (Math.random() * 15));
					shadow.setSkyLight(i, j, k, (int) (Math.random() * 15));
					shadow.setBlockLight(i, j + 1, k, (int) (Math.random() * 15));
					shadow.setSkyLight(i, j + 1, k, (int) (Math.random() * 15));

					if(!c.getBlock(i, j, k).isEmpty())
					{
						shadow.setBlock(i, j, k, Material.GRASS, 0);
					}
				}
			}
		}

		p.sendMessage("Mask: " + shadow.getEntireMask() + " Modified: " + shadow.getModifiedMask());

		NMA.CHUNK.data(p, shadow);
	}
}
