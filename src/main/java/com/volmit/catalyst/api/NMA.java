package com.volmit.catalyst.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class NMA
{
	public static class CHUNK
	{
		public static void refresh(Player p, Chunk at)
		{
			unload(p, at);

			if(Catalyst.host.canSee(at, p))
			{
				Catalyst.host.sendPacket(p, Catalyst.host.packetChunkFullSend(at));
			}
		}

		public static void refresh(Chunk at)
		{
			unload(at);
			Catalyst.host.sendViewDistancedPacket(at, Catalyst.host.packetChunkFullSend(at));
		}

		public static void unload(Player p, Chunk at)
		{
			if(Catalyst.host.canSee(at, p))
			{
				Catalyst.host.sendPacket(p, Catalyst.host.packetChunkUnload(at.getX(), at.getZ()));
			}
		}

		public static void unload(Chunk at)
		{
			Catalyst.host.sendViewDistancedPacket(at, Catalyst.host.packetChunkUnload(at.getX(), at.getZ()));
		}

		public static void data(Player p, ShadowChunk chunk)
		{
			if(Catalyst.host.canSee(chunk.getSource(), p))
			{
				for(Object i : chunk.flush())
				{
					Catalyst.host.sendPacket(p, i);
				}
			}

			else
			{
				chunk.flush();
			}
		}

		public static void data(ShadowChunk chunk)
		{
			for(Object i : chunk.flush())
			{
				Catalyst.host.sendViewDistancedPacket(chunk.getSource(), i);
			}
		}

		public static ShadowChunk shadow(Chunk at)
		{
			return Catalyst.host.shadowCopy(at);
		}
	}
}
