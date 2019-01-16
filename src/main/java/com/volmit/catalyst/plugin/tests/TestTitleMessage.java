package com.volmit.catalyst.plugin.tests;

import org.bukkit.entity.Player;

import com.volmit.catalyst.api.Catalyst;
import com.volmit.catalyst.plugin.Test;

public class TestTitleMessage implements Test
{
	@Override
	public String getName()
	{
		return "title";
	}

	@Override
	public void execute(Player p, String[] a)
	{
		Catalyst.host.sendPacket(p, Catalyst.host.packetTitleMessage("A Title"));
		Catalyst.host.sendPacket(p, Catalyst.host.packetTimes(20, 20, 20));
	}
}
