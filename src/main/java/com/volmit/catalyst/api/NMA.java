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

		public static void data(Player p, Chunk at, AbstractChunk c)
		{
			if(Catalyst.host.canSee(at, p))
			{
				Catalyst.host.sendPacket(p, Catalyst.host.packetChunkMap(c));
			}
		}

		public static void data(Chunk at, AbstractChunk c)
		{
			Catalyst.host.sendViewDistancedPacket(at, Catalyst.host.packetChunkMap(c));
		}
	}
}
