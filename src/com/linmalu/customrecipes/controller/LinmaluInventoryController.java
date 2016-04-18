package com.linmalu.customrecipes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.linmalu.customrecipes.Main;
import com.linmalu.library.api.LinmaluItemStack;

public class LinmaluInventoryController
{
	private static final Map<Player, LinmaluInventoryController> map = new HashMap<>();
	private static final int[] INVENTORY_NUMBER = {11, 12, 13, 20, 21, 22, 29, 30, 31};
	private static final ItemStack ITEM0 = LinmaluItemStack.getItemStack(Material.STAINED_GLASS_PANE, 1, 0, true, " ", "");
	private static final ItemStack ITEM1 = LinmaluItemStack.getItemStack(Material.WORKBENCH, 1, 0, true, ChatColor.GREEN + "조합대 조합법 보기", ChatColor.GRAY + "단축키 : 1");
	private static final ItemStack ITEM2 = LinmaluItemStack.getItemStack(Material.FURNACE, 1, 0, true, ChatColor.GREEN + "화로 조합법 보기", ChatColor.GRAY + "단축키 : 2");
	private static final ItemStack ITEM4 = LinmaluItemStack.getItemStack(Material.WORKBENCH, 1, 0, true, ChatColor.GREEN + "조합대 조합법 추가", ChatColor.GRAY + "단축키 : 4");
	private static final ItemStack ITEM5 = LinmaluItemStack.getItemStack(Material.FURNACE, 1, 0, true, ChatColor.GREEN + "화로 조합법 추가", ChatColor.GRAY + "단축키 : 5");
	private static final ItemStack ITEM7 = LinmaluItemStack.getItemStack(Material.SOUL_SAND, 1, 0, true, ChatColor.GREEN + "이전페이지", ChatColor.GRAY + "단축키 : 7");
	private static final ItemStack ITEM8 = LinmaluItemStack.getItemStack(Material.GLOWSTONE, 1, 0, true, ChatColor.GREEN + "다음페이지", ChatColor.GRAY + "단축키 : 8");
	private static final ItemStack ITEM9 = LinmaluItemStack.getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "이전으로", ChatColor.GRAY + "단축키 : 9");
	private static final ItemStack ITEM10 = LinmaluItemStack.getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "조합대 조합법 지우기");
	private static final ItemStack ITEM11 = LinmaluItemStack.getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "화로 조합법 지우기");
	private static final ItemStack ITEM12 = LinmaluItemStack.getItemStack(Material.BOOK, 1, 0, true, ChatColor.GREEN + "조합법 기본으로 되돌리기");
	private static final ItemStack ITEM13 = LinmaluItemStack.getItemStack(Material.DIAMOND_PICKAXE, 1, 0, true, ChatColor.GREEN + "조합법 추가");
	private static final ItemStack ITEM14 = LinmaluItemStack.getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "조합법 삭제");
	private static final ItemStack ITEM15 = LinmaluItemStack.getItemStack(Material.SOUL_SAND, 1, 0, true, ChatColor.GREEN + "무작위순서");
	private static final ItemStack ITEM16 = LinmaluItemStack.getItemStack(Material.GLOWSTONE, 1, 0, true, ChatColor.GREEN + "정확한순서");

	public static void InventoryClickEvent(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equals(Main.INVENTORY_TITLE))
		{
			LinmaluInventoryController lic = map.get(event.getWhoClicked());
			if(lic == null)
			{
				event.setCancelled(true);
				return;
			}
			Bukkit.broadcastMessage((event.getRawSlot() / 9 < 1) + " / " + (event.getRawSlot() / 9 > 3) + " / " + (event.getRawSlot() % 9 < 2) + " / " + (event.getRawSlot() % 9 > 4));
			if(!lic.player.isOp() || lic.stat == 0 || lic.stat % 10 == 1 || (lic.stat == 2 && event.getRawSlot() != 24 && (event.getRawSlot() / 9 < 1 || event.getRawSlot() / 9 > 3 || event.getRawSlot() % 9 < 2 || event.getRawSlot() % 9 > 4)) || (lic.stat == 12 && event.getRawSlot() != 20 && event.getRawSlot() != 24))
			{
				if(event.getRawSlot() < 54 || event.getClick() == ClickType.SHIFT_LEFT || event.isShiftClick())
				{
					event.setCancelled(true);
				}
			}
			if(event.getHotbarButton() != -1)
			{
				switch(event.getHotbarButton() + 1)
				{
					case 1:
						lic.changeRecipeList(0);
						break;
					case 2:
						lic.changeFurnaceList(0);
						break;
					case 4:
						if(lic.player.isOp())
						{
							lic.changeRecipeView(null);
						}
						break;
					case 5:
						if(lic.player.isOp())
						{
							lic.changeFurnaceView(null);
						}
						break;
					case 7:
						lic.beforePage();
						break;
					case 8:
						lic.nextPage();
						break;
					case 9:
						lic.backPage();
						break;
				}
			}
			else
			{
				if(event.getRawSlot() < 0)
				{
					return;
				}
				else if(event.getRawSlot() < lic.list.size())
				{
					lic.changeView(event.getRawSlot());
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM1, LinmaluItemStack.values()))
				{
					lic.changeRecipeList(0);
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM2, LinmaluItemStack.values()))
				{
					lic.changeFurnaceList(0);
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM4, LinmaluItemStack.values()))
				{
					lic.changeRecipeView(null);
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM5, LinmaluItemStack.values()))
				{
					lic.changeFurnaceView(null);
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM7, LinmaluItemStack.values()))
				{
					lic.beforePage();
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM8, LinmaluItemStack.values()))
				{
					lic.nextPage();
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM9, LinmaluItemStack.values()))
				{
					lic.backPage();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM10, LinmaluItemStack.values()))
				{
					Main.getMain().getRecipeConfig().clearRecipe();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM11, LinmaluItemStack.values()))
				{
					Main.getMain().getRecipeConfig().clearFurnace();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM12, LinmaluItemStack.values()))
				{
					Main.getMain().getRecipeConfig().resetRecipe();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM13, LinmaluItemStack.values()))
				{
					Recipe recipe = lic.toRecipe();
					if(recipe != null)
					{
						Main.getMain().getRecipeConfig().addRecipe(recipe);
						lic.backPage();
					}
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM14, LinmaluItemStack.values()))
				{
					Recipe recipe = lic.toRecipe();
					if(recipe != null)
					{
						Main.getMain().getRecipeConfig().removeRecipe(recipe);
						lic.backPage();
					}
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM15, LinmaluItemStack.values()))
				{
					lic.inv.setItem(event.getRawSlot(), ITEM16);
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM16, LinmaluItemStack.values()))
				{
					lic.inv.setItem(event.getRawSlot(), ITEM15);
				}
			}
		}
	}
	public static void InventoryDragEvent(InventoryDragEvent event)
	{
		if(event.getInventory().getTitle().equals(Main.INVENTORY_TITLE))
		{
			Bukkit.broadcastMessage("작동");
		}
	}
	public static void InventoryCloseEvent(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equals(Main.INVENTORY_TITLE))
		{
			map.remove(event.getPlayer());
		}
	}

	private final Player player;
	private final Inventory inv;
	private int stat = 0;
	private int page = 0;
	private List<Recipe> list = new ArrayList<>();

	public LinmaluInventoryController(Player player)
	{
		this.player = player;
		map.put(player, this);
		inv = Bukkit.createInventory(null, 54, Main.INVENTORY_TITLE);
		player.openInventory(inv);
		changeBase();
	}
	public void changeBase()
	{
		inv.clear();
		list.clear();
		stat = 0;
		for(int i = 0; i < inv.getSize(); i++)
		{
			ItemStack item = ITEM0;
			if(player.isOp())
			{
				if(i == 6)
				{
					item = ITEM10;
				}
				else if(i == 7)
				{
					item = ITEM11;
				}
				else if(i == 8)
				{
					item = ITEM12;
				}
			}
			inv.setItem(i, item);
		}
		baseItemStack();
	}
	@SuppressWarnings("deprecation")
	public void changeRecipeList(int page)
	{
		inv.clear();
		list.clear();
		stat = 1;
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(Bukkit.recipeIterator(), Spliterator.ORDERED), false).filter(r -> r instanceof ShapedRecipe || r instanceof ShapelessRecipe).sorted((a, b) ->
		{
			int compare = Integer.compare(a.getResult().getTypeId(), b.getResult().getTypeId());
			if(compare == 0)
			{
				compare = Integer.compare(a.getResult().getDurability(), b.getResult().getDurability());
			}
			return compare;
		}).skip(page * 45).limit(45).forEach(r ->
		{
			list.add(r);
			inv.setItem(inv.firstEmpty(), r.getResult());
		});
		baseItemStack();
	}
	public void changeRecipeView(Recipe recipe)
	{
		inv.clear();
		list.clear();
		stat = 2;
		for(int i = 0; i < inv.getSize(); i++)
		{
			if(i != 24 && !((1 <= i / 9 && i / 9 <= 3) && (2 <= i % 9 && i % 9 <= 4)))
			{
				ItemStack item = ITEM0;
				if(i == 41)
				{
					item = ITEM16;
				}
				else if(player.isOp())
				{
					if(i == 42)
					{
						item = ITEM13;
					}
					else if(i == 43)
					{
						item = ITEM14;
					}
				}
				inv.setItem(i, item);
			}
		}
		if(recipe != null && recipe instanceof ShapedRecipe)
		{
			ShapedRecipe sr = (ShapedRecipe)recipe;
			inv.setItem(24, sr.getResult());
			for(int y = 0; y < sr.getShape().length; y++)
			{
				for(int x = 0; x < sr.getShape()[y].length(); x++)
				{
					ItemStack item = sr.getIngredientMap().get(sr.getShape()[y].charAt(x));
					inv.setItem(INVENTORY_NUMBER[3 * y + x], item != null ? new ItemStack(item.getType(), item.getAmount(), item.getDurability()) : null);
				}
			}
		}
		else if(recipe != null && recipe instanceof ShapelessRecipe)
		{
			ShapelessRecipe sr = (ShapelessRecipe)recipe;
			inv.setItem(24, sr.getResult());
			for(int i = 0; i < sr.getIngredientList().size(); i++)
			{
				ItemStack item = sr.getIngredientList().get(i);
				inv.setItem(INVENTORY_NUMBER[i], item != null ? new ItemStack(item.getType(), item.getAmount(), item.getDurability()) : null);
			}
			inv.setItem(41, ITEM15);
		}
		baseItemStack();
	}
	@SuppressWarnings("deprecation")
	public void changeFurnaceList(int page)
	{
		inv.clear();
		list.clear();
		stat = 11;
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(Bukkit.recipeIterator(), Spliterator.ORDERED), false).filter(r -> r instanceof FurnaceRecipe).sorted((a, b) ->
		{
			int compare = Integer.compare(a.getResult().getTypeId(), b.getResult().getTypeId());
			if(compare == 0)
			{
				compare = Integer.compare(a.getResult().getDurability(), b.getResult().getDurability());
			}
			return compare;
		}).skip(page * 45).limit(45).forEach(r ->
		{
			list.add(r);
			inv.setItem(inv.firstEmpty(), r.getResult());
		});
		baseItemStack();
	}
	public void changeFurnaceView(Recipe recipe)
	{
		inv.clear();
		list.clear();
		stat = 12;
		for(int i = 0; i < inv.getSize(); i++)
		{
			if(i != 20 && i != 24)
			{
				ItemStack item = ITEM0;
				if(player.isOp())
				{
					if(i == 42)
					{
						item = ITEM13;
					}
					else if(i == 43)
					{
						item = ITEM14;
					}
				}
				inv.setItem(i, item);
			}
		}
		if(recipe != null && recipe instanceof FurnaceRecipe)
		{
			FurnaceRecipe fr = (FurnaceRecipe)recipe;
			inv.setItem(20, new ItemStack(fr.getInput().getType()));
			inv.setItem(24, fr.getResult());
		}
		baseItemStack();
	}
	public void beforePage()
	{
		if(page != 0 && stat % 10 == 1)
		{
			page--;
			changePage();
		}
	}
	public void nextPage()
	{
		if(inv.firstEmpty() == -1 && stat % 10 == 1)
		{
			page++;
			changePage();
		}
	}
	public void backPage()
	{
		if(stat % 10 == 2)
		{
			stat -= 1;
			changePage();
		}
		else if(stat != 0)
		{
			changeBase();
		}
		else
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), () -> player.closeInventory());
		}
	}
	public Recipe toRecipe()
	{
		Recipe recipe = null;
		ItemStack output = inv.getItem(24);
		if(output != null)
		{
			if(stat == 2)
			{
				if(LinmaluItemStack.equalsItemStack(inv.getItem(41), ITEM16, LinmaluItemStack.values()))
				{
					recipe = new ShapedRecipe(output);
					StringBuilder shape = new StringBuilder();
					for(int i = 0; i < LinmaluRecipeController.SHAPES.length; i++)
					{
						if(i > 0 && i % 3 == 0)
						{
							shape.append(",");
						}
						shape.append(inv.getItem(INVENTORY_NUMBER[i]) != null ? LinmaluRecipeController.SHAPES[i] : " ");
					}
					Bukkit.broadcastMessage(shape.toString());
					((ShapedRecipe)recipe).shape(shape.toString().split(","));
					for(int i = 0; i < INVENTORY_NUMBER.length; i++)
					{
						ItemStack item;
						if((item = inv.getItem(INVENTORY_NUMBER[i])) != null)
						{
							((ShapedRecipe)recipe).setIngredient(LinmaluRecipeController.SHAPES[i], item.getData());
						}
					}
					if(((ShapedRecipe)recipe).getIngredientMap().values().stream().filter(item -> item != null).count() == 0)
					{
						recipe = null;
					}
				}
				else
				{
					recipe = new ShapelessRecipe(output);
					for(int i : INVENTORY_NUMBER)
					{
						ItemStack item;
						if((item = inv.getItem(i)) != null)
						{
							((ShapelessRecipe)recipe).addIngredient(item.getAmount(), item.getData());
						}
					}
					if(((ShapelessRecipe)recipe).getIngredientList().stream().filter(item -> item != null).count() == 0)
					{
						recipe = null;
					}
				}
			}
			else if(stat == 12)
			{
				ItemStack item;
				if((item = inv.getItem(20)) != null)
				{
					recipe = new FurnaceRecipe(output, item.getData());
				}
			}
		}
		return recipe;
	}
	private void changePage()
	{
		switch(stat)
		{
			case 1:
				changeRecipeList(page);
				break;
			case 11:
				changeFurnaceList(page);
				break;
		}
	}
	private void changeView(int number)
	{
		switch(stat)
		{
			case 1:
				changeRecipeView(list.get(number));
				break;
			case 11:
				changeFurnaceView(list.get(number));
				break;
		}
	}
	private void baseItemStack()
	{
		for(int i = 45; i < inv.getSize(); i++)
		{
			ItemStack item = ITEM0;
			if(i == 45)
			{
				item = ITEM1;
			}
			else if(i == 46)
			{
				item = ITEM2;
			}
			else if(player.isOp() && i == 48)
			{
				item = ITEM4;
			}
			else if(player.isOp() && i == 49)
			{
				item = ITEM5;
			}
			else if(i == 51 && (stat % 10 == 1))
			{
				item = ITEM7;
			}
			else if(i == 52 && (stat % 10 == 1))
			{
				item = ITEM8;
			}
			else if(i == 53)
			{
				item = ITEM9;
			}
			inv.setItem(i, item);
		}
	}
}
