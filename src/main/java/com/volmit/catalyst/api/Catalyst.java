package com.volmit.catalyst.api;

import com.volmit.catalyst.hosts.v12.Catalyst12;

public class Catalyst
{
	public static final CatalystHost host = getHost();

	private static CatalystHost getHost()
	{
		switch(NMSVersion.current())
		{
			case R1_10:
				return null;
			case R1_11:
				return null;
			case R1_12:
				return new Catalyst12();
			case R1_13:
				return null;
			case R1_8:
				return null;
			case R1_9_2:
				return null;
			case R1_9_4:
				return null;
			default:
				return null;
		}
	}
}
