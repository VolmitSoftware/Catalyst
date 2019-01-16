package com.volmit.catalyst.plugin.tests;

import org.bukkit.entity.Player;

import com.volmit.catalyst.api.Catalyst;
import com.volmit.catalyst.plugin.Test;

import net.minecraft.server.v1_12_R1.PacketPlayOutGameStateChange;

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
		Catalyst.host.sendPacket(p, new PacketPlayOutGameStateChange(8, Float.valueOf(a[0])));
		Catalyst.host.sendPacket(p, new PacketPlayOutGameStateChange(7, Float.valueOf(a[1])));
	}
}
