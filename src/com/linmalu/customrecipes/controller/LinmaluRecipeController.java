package com.linmalu.customrecipes.controller;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinmaluRecipeController
{
	public static final char[] SHAPES = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
	public static final int NONE = 0;
	public static final int FURNACE = 1;
	public static final int SHAPED = 2;
	public static final int SHAPELESS = 3;

	public static LinmaluRecipe createLinmaluRecipe(Recipe recipe)
	{
		return new LinmaluRecipe(recipe);
	}

	public static String[] getShape(List<ItemStack> list)
	{
		if(list == null || list.size() < 9)
		{
			return null;
		}
		boolean type1 = list.get(1) == null && list.get(4) == null && list.get(7) == null;
		boolean type2 = list.get(2) == null && list.get(5) == null && list.get(8) == null;
		boolean type3 = list.get(3) == null && list.get(4) == null && list.get(5) == null;
		boolean type4 = list.get(6) == null && list.get(7) == null && list.get(8) == null;
		StringBuilder shape = new StringBuilder();
		for(char c : LinmaluRecipeController.SHAPES)
		{
			boolean write = true;
			switch(c)
			{
				case 'a':
					break;
				case 'b':
					if(type1 && type2)
					{
						write = false;
					}
					break;
				case 'c':
					if(type2)
					{
						write = false;
					}
					break;
				case 'd':
					shape.append(",");
					if(type3 && type4)
					{
						write = false;
					}
					break;
				case 'e':
					if((type1 && type2) || (type3 && type4))
					{
						write = false;
					}
					break;
				case 'f':
					if(type2 || (type3 && type4))
					{
						write = false;
					}
					break;
				case 'g':
					shape.append(",");
					if(type4)
					{
						write = false;
					}
					break;
				case 'h':
					if((type1 && type2) || type4)
					{
						write = false;
					}
					break;
				case 'i':
					if(type2 || type4)
					{
						write = false;
					}
					break;
			}
			if(write)
			{
				shape.append(c);
			}
		}
		return shape.toString().split(",");
	}

	public static class LinmaluRecipe implements ConfigurationSerializable
	{
		private static final String KEY1 = "Type";
		private static final String KEY2 = "Input";
		private static final String KEY3 = "Output";
		private int type;
		private final List<ItemStack> input;
		private final ItemStack output;

		private LinmaluRecipe(Recipe recipe)
		{
			input = new ArrayList<>();
			if(recipe instanceof FurnaceRecipe)
			{
				type = FURNACE;
				FurnaceRecipe fr = (FurnaceRecipe)recipe;
				input.add(fr.getInput());
				output = fr.getResult();
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
								input.add(item);
								break;
							}
						}
					}
				});
				while(input.size() < 9)
				{
					input.add(null);
				}
				output = sr.getResult();
			}
			else if(recipe instanceof ShapelessRecipe)
			{
				type = SHAPELESS;
				ShapelessRecipe sr = (ShapelessRecipe)recipe;
				sr.getIngredientList().forEach(item -> input.add(item));
				output = sr.getResult();
			}
			else
			{
				type = NONE;
				output = null;
			}
			if(output != null && output.getAmount() == 0)
			{
				type = NONE;
			}
			else
			{
				input.forEach(item ->
				{
					if(item != null && item.getAmount() == 0)
					{
						type = NONE;
					}
				});
			}
		}

		private LinmaluRecipe(int type, List<ItemStack> input, ItemStack output)
		{
			this.type = type;
			this.input = input;
			this.output = output;
		}

		@Override
		public Map<String, Object> serialize()
		{
			Map<String, Object> map = new LinkedHashMap<>();
			map.put(KEY1, type);
			map.put(KEY2, input);
			map.put(KEY3, output);
			return map;
		}

		@SuppressWarnings("unchecked")
		public static LinmaluRecipe deserialize(Map<String, Object> map)
		{
			return new LinmaluRecipe((int)map.get(KEY1), (List<ItemStack>)map.get(KEY2), (ItemStack)map.get(KEY3));
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

		@SuppressWarnings("deprecation")
		public Recipe toRecipe()
		{
			Recipe recipe = null;
			switch(type)
			{
				case FURNACE:
					recipe = new FurnaceRecipe(output, input.get(0).getType(), input.get(0).getDurability());
					break;
				case SHAPED:
					recipe = new ShapedRecipe(output);
					((ShapedRecipe)recipe).shape(getShape(input));
					for(int i = 0; i < SHAPES.length; i++)
					{
						ItemStack item;
						if((item = input.get(i)) != null)
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
			LinmaluRecipe target = createLinmaluRecipe(recipe);
			if(type == target.getType() && output.getType() == target.getOutput().getType() && input.size() == target.getInput().size())
			{
				for(int i = 0; i < input.size(); i++)
				{
					ItemStack item1 = input.get(i);
					ItemStack item2 = target.input.get(i);
					if(!((item1 != null && item2 != null && item1.getType() == item2.getType()) || item1 == null && item2 == null))
					{
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}
}
