package com.linmalu.customrecipes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.linmalu.customrecipes.controller.LinmaluInventoryController;
import com.linmalu.library.api.LinmaluVersion;

public class Main_Command implements CommandExecutor
{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			new LinmaluInventoryController((Player)sender);
			sender.sendMessage(ChatColor.GREEN + " = = = = = [ Linmalu Custom Recipes ] = = = = =");
			sender.sendMessage(ChatColor.YELLOW + "제작자 : " + ChatColor.AQUA + "린마루(Linmalu)" + ChatColor.WHITE + " - http://blog.linmalu.com");
			if(sender.isOp())
			{
				LinmaluVersion.check(Main.getMain(), sender);
			}
		}
		return true;
	}
}
