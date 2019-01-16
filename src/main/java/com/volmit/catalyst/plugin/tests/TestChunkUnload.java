package com.volmit.catalyst.plugin.tests;

import org.bukkit.entity.Player;

import com.volmit.catalyst.api.Catalyst;
import com.volmit.catalyst.plugin.Test;

public class TestChunkUnload implements Test
{
	@Override
	public String getName()
	{
		return "unload";
	}

	@Override
	public void execute(Player p, String[] a)
	{
		Catalyst.host.sendPacket(p, Catalyst.host.packetChunkUnload(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ()));
	}
}
