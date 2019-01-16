package com.volmit.catalyst.plugin.tests;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.volmit.catalyst.api.NMA;
import com.volmit.catalyst.plugin.Test;

public class TestChunkRefresh implements Test
{
	@Override
	public String getName()
	{
		return "refresh";
	}

	@Override
	public void execute(Player p, String[] a)
	{
		Chunk c = p.getLocation().getChunk();
		NMA.CHUNK.refresh(p, c);
	}
}
