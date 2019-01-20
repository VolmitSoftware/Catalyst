package com.volmit.catalyst.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class PacketBuffer
{
	private final List<Object> packets;

	public PacketBuffer()
	{
		packets = new ArrayList<>();
	}

	public PacketBuffer q(Object o)
	{
		packets.add(o);
		return this;
	}

	public PacketBuffer flush(Player p)
	{
		for(Object i : packets)
		{
			Catalyst.host.sendPacket(p, i);
		}

		return this;
	}
}
