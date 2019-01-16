package com.volmit.catalyst.plugin.tests;

import org.bukkit.entity.Player;

import com.volmit.catalyst.api.Catalyst;
import com.volmit.catalyst.plugin.Test;

public class TestSubtitleMessage implements Test
{
	@Override
	public String getName()
	{
		return "subtitle";
	}

	@Override
	public void execute(Player p, String[] a)
	{
		Catalyst.host.sendPacket(p, Catalyst.host.packetTitleMessage(""));
		Catalyst.host.sendPacket(p, Catalyst.host.packetSubtitleMessage("A Subtitle"));
		Catalyst.host.sendPacket(p, Catalyst.host.packetTimes(20, 20, 20));
	}
}
