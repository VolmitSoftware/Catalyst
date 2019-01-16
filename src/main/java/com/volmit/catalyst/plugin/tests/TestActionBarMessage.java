package com.volmit.catalyst.plugin.tests;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.volmit.catalyst.api.Catalyst;
import com.volmit.catalyst.plugin.Test;

public class TestActionBarMessage implements Test
{
	@Override
	public String getName()
	{
		return "t";
	}

	@Override
	public void execute(Player p, String[] a)
	{
		Catalyst.host.sendPacket(p, Catalyst.host.packetActionBarMessage("Action bar with " + ChatColor.STRIKETHROUGH + "format"));
	}
}
