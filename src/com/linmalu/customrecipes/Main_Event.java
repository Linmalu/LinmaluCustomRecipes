package com.linmalu.customrecipes;

import com.linmalu.customrecipes.controller.LinmaluInventoryController;
import com.linmalu.library.api.LinmaluEvent;
import com.linmalu.library.api.LinmaluMain;
import com.linmalu.library.api.LinmaluServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Main_Event extends LinmaluEvent
{
	public Main_Event(LinmaluMain main)
	{
		super(main);
	}

	@EventHandler
	public void Event(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(player.isOp())
		{
			LinmaluServer.version(Main.getMain(), player);
		}
	}

	@EventHandler
	public void Event(InventoryClickEvent event)
	{
		LinmaluInventoryController.InventoryClickEvent(event);
	}

	@EventHandler
	public void Event(InventoryCloseEvent event)
	{
		LinmaluInventoryController.InventoryCloseEvent(event);
	}
}
