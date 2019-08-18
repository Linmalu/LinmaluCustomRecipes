package com.linmalu.customrecipes;

import com.linmalu.customrecipes.controller.LinmaluRecipeController;
import com.linmalu.library.api.LinmaluItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.StreamSupport;

public class RecipeController
{
	private static final Map<Player, RecipeController> map = new HashMap<>();
	private static NamespacedKey key = new NamespacedKey(Main.getMain(), Main.getMain().getDescription().getName());
	private static final int[] RECIPE_NUMBER = {11, 12, 13, 20, 21, 22, 29, 30, 31};
	private static final ItemStack ITEM0 = getItemStack(Material.WHITE_STAINED_GLASS_PANE, 1, 0, true, " ", "");
	private static final ItemStack ITEM1 = getItemStack(Material.CRAFTING_TABLE, 1, 0, true, ChatColor.GREEN + "조합대 조합법 보기", ChatColor.GRAY + "단축키 : 1");
	private static final ItemStack ITEM2 = getItemStack(Material.FURNACE, 1, 0, true, ChatColor.GREEN + "화로 조합법 보기", ChatColor.GRAY + "단축키 : 2");
	private static final ItemStack ITEM4 = getItemStack(Material.CRAFTING_TABLE, 1, 0, true, ChatColor.GREEN + "조합대 조합법 추가", ChatColor.GRAY + "단축키 : 4");
	private static final ItemStack ITEM5 = getItemStack(Material.FURNACE, 1, 0, true, ChatColor.GREEN + "화로 조합법 추가", ChatColor.GRAY + "단축키 : 5");
	private static final ItemStack ITEM7 = getItemStack(Material.SOUL_SAND, 1, 0, true, ChatColor.GREEN + "이전페이지", ChatColor.GRAY + "단축키 : 7");
	private static final ItemStack ITEM8 = getItemStack(Material.GLOWSTONE, 1, 0, true, ChatColor.GREEN + "다음페이지", ChatColor.GRAY + "단축키 : 8");
	private static final ItemStack ITEM9 = getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "이전으로", ChatColor.GRAY + "단축키 : 9");
	private static final ItemStack ITEM10 = getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "조합대 조합법 지우기");
	private static final ItemStack ITEM11 = getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "화로 조합법 지우기");
	private static final ItemStack ITEM12 = getItemStack(Material.BOOK, 1, 0, true, ChatColor.GREEN + "조합법 기본으로 되돌리기");
	private static final ItemStack ITEM13 = getItemStack(Material.DIAMOND_PICKAXE, 1, 0, true, ChatColor.GREEN + "조합법 추가");
	private static final ItemStack ITEM14 = getItemStack(Material.BARRIER, 1, 0, true, ChatColor.GREEN + "조합법 삭제");
	private static final ItemStack ITEM15 = getItemStack(Material.SOUL_SAND, 1, 0, true, ChatColor.GREEN + "무작위순서");
	private static final ItemStack ITEM16 = getItemStack(Material.GLOWSTONE, 1, 0, true, ChatColor.GREEN + "정확한순서");

	public static ItemStack getItemStack(Material type, int amount, int damage, boolean hideFlag, String name, String... lore)
	{
		ItemStack item = new ItemStack(type, amount);
		ItemMeta im = item.getItemMeta();
		if(im instanceof Damageable)
		{
			((Damageable)im).setDamage(damage);
		}
		if(hideFlag)
		{
			im.addItemFlags(ItemFlag.values());
		}
		im.setDisplayName(name);
		if(!(lore.length == 1 && lore[0].equals("")))
		{
			im.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(im);
		return item;
	}

	public static void InventoryClickEvent(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equals(Main.getInstance().getTitle()))
		{
			RecipeController lic = map.get(event.getWhoClicked());
			if(lic == null)
			{
				event.setCancelled(true);
				return;
			}
			if(!lic.player.isOp() || lic.stat == 0 || lic.stat % 10 == 1 || (lic.stat == 2 && !lic.isRecipeItemNumber(event.getRawSlot())) || (lic.stat == 12 && event.getRawSlot() != 20 && event.getRawSlot() != 24))
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
				event.setCancelled(true);
			}
			else
			{
				if(event.getRawSlot() < 0 || event.getClick() == ClickType.DOUBLE_CLICK)
				{
					return;
				}
				else if(event.getRawSlot() < lic.list.size())
				{
					lic.changeView(event.getRawSlot());
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM1))
				{
					lic.changeRecipeList(0);
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM2))
				{
					lic.changeFurnaceList(0);
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM4))
				{
					lic.changeRecipeView(null);
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM5))
				{
					lic.changeFurnaceView(null);
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM7))
				{
					lic.beforePage();
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM8))
				{
					lic.nextPage();
				}
				else if(LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM9))
				{
					lic.backPage();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM10))
				{
					Main.getMain().getRecipeConfig().clearRecipe();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM11))
				{
					Main.getMain().getRecipeConfig().clearFurnace();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM12))
				{
					Main.getMain().getRecipeConfig().resetRecipe();
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM13))
				{
					Recipe recipe = lic.toRecipe();
					if(recipe != null)
					{
						Main.getMain().getRecipeConfig().addRecipe(recipe);
						lic.backPage();
					}
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM14))
				{
					Recipe recipe = lic.toRecipe();
					if(recipe != null)
					{
						Main.getMain().getRecipeConfig().removeRecipe(recipe);
						lic.backPage();
					}
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM15))
				{
					lic.inv.setItem(event.getRawSlot(), ITEM16);
				}
				else if(lic.player.isOp() && LinmaluItemStack.equalsItemStack(event.getCurrentItem(), ITEM16))
				{
					lic.inv.setItem(event.getRawSlot(), ITEM15);
				}
			}
		}
	}

	public static void InventoryCloseEvent(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equals(Main.getInstance().getTitle()))
		{
			map.remove(event.getPlayer());
		}
	}

	private final Player player;
	private final Inventory inv;
	private int stat = 0;
	private int page = 0;
	private List<Recipe> list = new ArrayList<>();

	public RecipeController(Player player)
	{
		this.player = player;
		map.put(player, this);
		inv = Bukkit.createInventory(null, 54, Main.getInstance().getTitle());
		player.openInventory(inv);
		changeBase();
	}

	public void changeBase()
	{
		inv.clear();
		list.clear();
		stat = 0;
		page = 0;
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

	public void changeRecipeList(int page)
	{
		inv.clear();
		list.clear();
		stat = 1;
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(Bukkit.recipeIterator(), Spliterator.ORDERED), false).filter(r -> r instanceof ShapedRecipe || r instanceof ShapelessRecipe).sorted((a, b) ->
		{
			int compare = Integer.compare(a.getResult().getType().getId(), b.getResult().getType().getId());
			if(compare == 0)
			{
				compare = Integer.compare(a.getResult().getDurability(), b.getResult().getDurability());
			}
			return compare;
		}).skip(page * 45).limit(45).forEach(r ->
		{
			list.add(r);
			inv.setItem(inv.firstEmpty(), checkItemStack(r.getResult()));
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
			if(!isRecipeItemNumber(i))
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
					inv.setItem(RECIPE_NUMBER[3 * y + x], item != null ? checkItemStack(new ItemStack(item.getType(), item.getAmount(), item.getDurability())) : null);
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
				inv.setItem(RECIPE_NUMBER[i], item != null ? checkItemStack(new ItemStack(item.getType(), item.getAmount(), item.getDurability())) : null);
			}
			inv.setItem(41, ITEM15);
		}
		baseItemStack();
	}

	public void changeFurnaceList(int page)
	{
		inv.clear();
		list.clear();
		stat = 11;
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(Bukkit.recipeIterator(), Spliterator.ORDERED), false).filter(r -> r instanceof FurnaceRecipe).sorted((a, b) ->
		{
			int compare = Integer.compare(a.getResult().getType().getId(), b.getResult().getType().getId());
			if(compare == 0)
			{
				compare = Integer.compare(a.getResult().getDurability(), b.getResult().getDurability());
			}
			return compare;
		}).skip(page * 45).limit(45).forEach(r ->
		{
			list.add(r);
			inv.setItem(inv.firstEmpty(), checkItemStack(r.getResult()));
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
			inv.setItem(20, checkItemStack(fr.getInput()));
			inv.setItem(24, checkItemStack(fr.getResult()));
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
				List<ItemStack> list = new ArrayList<>();
				for(int number : RECIPE_NUMBER)
				{
					list.add(inv.getItem(number));
				}
				if(list.stream().filter(item -> item != null).count() != 0)
				{
					if(LinmaluItemStack.equalsItemStack(inv.getItem(41), ITEM16))
					{
						recipe = new ShapedRecipe(key, output);
						((ShapedRecipe)recipe).shape(LinmaluRecipeController.getShape(list));
						for(int i = 0; i < LinmaluRecipeController.SHAPES.length; i++)
						{
							ItemStack item;
							if((item = list.get(i)) != null)
							{
								((ShapedRecipe)recipe).setIngredient(LinmaluRecipeController.SHAPES[i], item.getData());
							}
						}
					}
					else
					{
						recipe = new ShapelessRecipe(output);
						for(ItemStack item : list)
						{
							if(item != null)
							{
								((ShapelessRecipe)recipe).addIngredient(item.getAmount(), item.getData());
							}
						}
					}
				}
			}
			else if(stat == 12)
			{
				ItemStack item;
				if((item = inv.getItem(20)) != null)
				{
					recipe = new FurnaceRecipe(output, item.getType(), item.getDurability());
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

	private boolean isRecipeItemNumber(int number)
	{
		if(number == 24)
		{
			return true;
		}
		else
		{
			for(int i : RECIPE_NUMBER)
			{
				if(i == number)
				{
					return true;
				}
			}
			return false;
		}
	}

	private ItemStack checkItemStack(ItemStack item)
	{
		if(item != null && item.getDurability() == Short.MAX_VALUE)
		{
			item.setDurability((short)0);
		}
		return item;
	}
}
