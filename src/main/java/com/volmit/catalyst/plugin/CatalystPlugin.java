package com.volmit.catalyst.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.catalyst.api.Catalyst;

public class CatalystPlugin extends JavaPlugin implements Listener
{
	public static CatalystPlugin plugin;
	private List<Test> tests = new ArrayList<>();

	@Override
	public void onEnable()
	{
		plugin = this;
		JarScannerSpecial s = new JarScannerSpecial(getFile(), "com.volmit.catalyst.plugin.tests");

		try
		{
			s.scan();

			for(Class<?> i : s.getClasses())
			{
				if(Test.class.isAssignableFrom(i))
				{
					try
					{
						tests.add((Test) i.getConstructor().newInstance());
					}

					catch(Throwable e)
					{

					}
				}
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		Bukkit.getPluginManager().registerEvents(this, this);

		// ------------------------ DO NOT FUCKING TOUCH
		/* DO NOT FUCKING TOUCH */ Catalyst.host.start(); // DO NOT FUCKING TOUCH
		// ------------------------ DO NOT FUCKING TOUCH
	}

	@Override
	public void onDisable()
	{
		try
		{
			Catalyst.host.stop();
		}

		catch(Throwable e)
		{

		}

		HandlerList.unregisterAll((Listener) this);
	}

	@EventHandler
	public void on(PlayerCommandPreprocessEvent e)
	{
		if(e.getMessage().toLowerCase().startsWith("/tests"))
		{
			for(Test i : tests)
			{
				e.getPlayer().sendMessage(i.getName());
			}

			e.setCancelled(true);
		}

		else if(e.getMessage().toLowerCase().startsWith("/test "))
		{
			try
			{
				e.setCancelled(true);
				List<String> arg = new ArrayList<String>();

				for(String i : e.getMessage().split(" "))
				{
					arg.add(i);
				}

				arg.remove(0);
				String type = arg.get(0);
				arg.remove(0);

				for(Test i : tests)
				{
					if(i.getName().equalsIgnoreCase(type))
					{
						try
						{
							i.execute(e.getPlayer(), arg.toArray(new String[arg.size()]));
						}

						catch(Throwable ev)
						{
							ev.printStackTrace();
							e.getPlayer().sendMessage("Either there was an error, or you diddnt use the command properly.");
						}

						return;
					}
				}
			}

			catch(Throwable ex)
			{
				e.getPlayer().sendMessage("/test <test> [args...]");
			}
		}
	}
}
