package com.linmalu.customrecipes;

import com.linmalu.customrecipes.controller.LinmaluInventoryController;
import com.linmalu.library.api.LinmaluCommand;
import com.linmalu.library.api.LinmaluMain;
import com.linmalu.library.api.LinmaluServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Main_Command extends LinmaluCommand
{
	public Main_Command(LinmaluMain main)
	{
		super(main);
	}

	@Override
	protected List<String> TabCompleter(CommandSender sender, Command command, String alias, String[] args)
	{
		return null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			new LinmaluInventoryController((Player)sender);
			sender.sendMessage(ChatColor.GREEN + " = = = = = [ Linmalu Custom Recipes ] = = = = =");
			sender.sendMessage(ChatColor.YELLOW + "제작자 : " + ChatColor.AQUA + "린마루(Linmalu)" + ChatColor.WHITE + " - http://blog.linmalu.com");
			if(sender.isOp())
			{
				LinmaluServer.version(Main.getMain(), sender);
			}
		}
		return true;
	}
}
