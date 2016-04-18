package com.linmalu.customrecipes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.linmalu.customrecipes.controller.LinmaluInventoryController;
import com.linmalu.library.api.LinmaluVersion;

public class Main_Event implements Listener
{
	@EventHandler
	public void Event(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(player.isOp())
		{
			LinmaluVersion.check(Main.getMain(), player);
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
