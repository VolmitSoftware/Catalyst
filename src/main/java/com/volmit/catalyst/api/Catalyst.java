package com.volmit.catalyst.api;

import com.volmit.catalyst.hosts.v10.Catalyst10;
import com.volmit.catalyst.hosts.v11.Catalyst11;
import com.volmit.catalyst.hosts.v12.Catalyst12;
import com.volmit.catalyst.hosts.v13.Catalyst13;
import com.volmit.catalyst.hosts.v8.Catalyst8;
import com.volmit.catalyst.hosts.v92.Catalyst92;
import com.volmit.catalyst.hosts.v94.Catalyst94;

public class Catalyst
{
	public static final CatalystHost host = getHost();

	private static CatalystHost getHost()
	{
		switch(NMSVersion.current())
		{
			case R1_10:
				return new Catalyst10();
			case R1_11:
				return new Catalyst11();
			case R1_12:
				return new Catalyst12();
			case R1_13:
				return new Catalyst13();
			case R1_8:
				return new Catalyst8();
			case R1_9_2:
				return new Catalyst92();
			case R1_9_4:
				return new Catalyst94();
			default:
				return null;
		}
	}
}
