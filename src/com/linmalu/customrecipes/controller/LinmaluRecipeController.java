package com.linmalu.customrecipes.controller;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.linmalu.library.api.LinmaluItemStack;

public class LinmaluRecipeController
{
	public static final char[] SHAPES = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
	public static final int NONE = 0;
	public static final int FURNACE = 1;
	public static final int SHAPED = 2;
	public static final int SHAPELESS = 3;

	public static LinmaluRecipeController createLinmaluRecipe(Recipe recipe)
	{
		return new LinmaluRecipeController(recipe);
	}
	public static LinmaluRecipeController createLinmaluRecipe(int type, List<ItemStack> input, ItemStack output)
	{
		return new LinmaluRecipeController(type, input, output);
	}

	private final int type;
	private final List<ItemStack> input;
	private final ItemStack output;

	private LinmaluRecipeController(Recipe recipe)
	{
		input = new ArrayList<>();
		if(recipe instanceof FurnaceRecipe)
		{
			type = FURNACE;
			FurnaceRecipe fr = (FurnaceRecipe)recipe;
			input.add(checkItemStack(fr.getInput()));
			output = checkItemStack(fr.getResult());
		}
		else if(recipe instanceof ShapedRecipe)
		{
			type = SHAPED;
			ShapedRecipe sr = (ShapedRecipe)recipe;
			sr.getIngredientMap().forEach((key, item) ->
			{
				String[] shape = sr.getShape();
				for(int n1 = 0; n1 < shape.length; n1++)
				{
					for(int n2 = 0; n2 < shape[n1].length(); n2++)
					{
						if(key.equals(shape[n1].charAt(n2)))
						{
							while(input.size() < (n1 * 3) + n2)
							{
								input.add(null);
							}
							input.add(checkItemStack(item));
							break;
						}
					}
				}
			});
			while(input.size() < 9)
			{
				input.add(null);
			}
			output = checkItemStack(sr.getResult());
		}
		else if(recipe instanceof ShapelessRecipe)
		{
			type = SHAPELESS;
			ShapelessRecipe sr = (ShapelessRecipe)recipe;
			sr.getIngredientList().forEach(item -> input.add(checkItemStack(item)));
			output = checkItemStack(sr.getResult());
		}
		else
		{
			type = NONE;
			output = null;
		}
	}
	private LinmaluRecipeController(int type, List<ItemStack> input, ItemStack output)
	{
		this.type = type;
		this.input = input;
		this.output = output;
	}
	private ItemStack checkItemStack(ItemStack item)
	{
		if(item != null && item.getDurability() == Short.MAX_VALUE)
		{
			item.setDurability((short)0);
		}
		return item;
	}
	public int getType()
	{
		return type;
	}
	public List<ItemStack> getInput()
	{
		return input;
	}
	public ItemStack getOutput()
	{
		return output;
	}
	public Recipe toRecipe()
	{
		Recipe recipe = null;
		switch(type)
		{
			case FURNACE:
				recipe = new FurnaceRecipe(output, input.get(0).getData());
				break;
			case SHAPED:
				recipe = new ShapedRecipe(output);
				StringBuilder shape = new StringBuilder();
				for(int i = 0; i < SHAPES.length; i++)
				{
					if(i > 0 && i % 3 == 0)
					{
						shape.append(",");
					}
					shape.append(input.get(i) != null ? SHAPES[i] : " ");
				}
				((ShapedRecipe)recipe).shape(shape.toString().split(","));
				for(int i = 0; i < SHAPES.length; i++)
				{
					ItemStack item = input.get(i);
					if(item != null)
					{
						((ShapedRecipe)recipe).setIngredient(SHAPES[i], item.getData());
					}
				}
				break;
			case SHAPELESS:
				recipe = new ShapelessRecipe(output);
				for(ItemStack item : input)
				{
					((ShapelessRecipe)recipe).addIngredient(item.getAmount(), item.getData());
				}
				break;
		}
		return recipe;
	}
	public boolean equalsRecipe(Recipe recipe)
	{
		LinmaluRecipeController target = createLinmaluRecipe(recipe);
		if(type == target.getType() && input.size() == target.input.size() && LinmaluItemStack.equalsItemStack(output, target.output, LinmaluItemStack.TYPE, LinmaluItemStack.DURABILITY))
		{
			for(int i = 0; i < input.size(); i++)
			{
				ItemStack item1 = input.get(i);
				ItemStack item2 = target.input.get(i);
				if(!(item1 != null && item2 != null && LinmaluItemStack.equalsItemStack(item1, item2, LinmaluItemStack.TYPE, LinmaluItemStack.DURABILITY) || item1 == null && item2 == null))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
