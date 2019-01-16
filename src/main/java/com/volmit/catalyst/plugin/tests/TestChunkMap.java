package com.volmit.catalyst.plugin.tests;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.volmit.catalyst.api.AbstractChunk;
import com.volmit.catalyst.api.Catalyst;
import com.volmit.catalyst.api.ChunkSection;
import com.volmit.catalyst.plugin.Test;

import net.md_5.bungee.api.ChatColor;

public class TestChunkMap implements Test
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
		AbstractChunk ac = new AbstractChunk(c);

		for(ChunkSection i : ac.getSections())
		{
			if(i != null)
			{
				for(int x = 0; x < 16; x++)
				{
					for(int y = 0; y < 16; y++)
					{
						for(int z = 0; z < 16; z++)
						{
							if(i.getTypeId(x, y, z) == 2)
							{
								i.setType(x, y, z, 3, (byte) 0);
							}
						}
					}
				}
			}
		}

		String v = "";
		int m = 0;

		for(int i = 0; i < 16; i++)
		{
			if(ac.hasSection(i))
			{
				m++;
				v += ", " + i;
			}
		}

		if(v.length() > 2)
		{
			v = v.substring(2);
		}

		p.sendMessage("Sections: " + m + " -> " + ChatColor.GREEN + v + (ac.isContinuous() ? ChatColor.RED + " [GUC]" : ""));
		ac.setDebug(true);
		Catalyst.host.sendPacket(p, Catalyst.host.packetChunkMap(ac, c));
	}
}
