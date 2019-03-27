package com.volmit.catalyst.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.volmit.catalyst.api.NMP;
import com.volmit.catalyst.nmp.Catalyst;
import com.volmit.catalyst.nmp.NMSVersion;

public class CatalystPlugin extends JavaPlugin implements Listener
{
	public static CatalystPlugin plugin;
	private List<Test> tests = new ArrayList<>();

	@Override
	public void onEnable()
	{
		plugin = this;
		Bukkit.getPluginManager().registerEvents(this, this);
		Catalyst.host.start();
		NMP.host = Catalyst.host;
		NMSVersion v = NMSVersion.current();

		if(v != null)
		{
			getLogger().info("Selected " + NMSVersion.current().name());
		}

		else
		{
			getLogger().info("Could not find a suitable binder for this server version!");
		}
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
