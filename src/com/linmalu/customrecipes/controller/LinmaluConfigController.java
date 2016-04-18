package com.linmalu.customrecipes.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.linmalu.customrecipes.Main;
import com.linmalu.library.api.LinmaluYamlConfiguration;

public class LinmaluConfigController
{
	private static final String TYPE = "Type";
	private static final String INPUT = "Input";
	private static final String OUTPUT = "Output";
	private static final File file = new File(Main.getMain().getDataFolder(), "config.yml");
	private static List<LinmaluRecipeController> recipes = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void load()
	{
		recipes.clear();
		if(file.exists())
		{
			LinmaluYamlConfiguration config = LinmaluYamlConfiguration.loadConfiguration(file);
			for(String key : config.getKeys(false))
			{
				recipes.add(LinmaluRecipeController.createLinmaluRecipe(config.getInt(key + "." + TYPE), (List<ItemStack>)config.getList(key + "." + INPUT), (ItemStack)config.get(key + "." + OUTPUT)));
			}
			changeRecipe();
		}
		else
		{
			resetRecipe();
		}
	}
	public void save()
	{
		LinmaluYamlConfiguration config = new LinmaluYamlConfiguration();
		recipes.forEach(recipe ->
		{
			config.set(recipe.hashCode() + "." + TYPE, recipe.getType());
			config.set(recipe.hashCode() + "." + INPUT, recipe.getInput());
			config.set(recipe.hashCode() + "." + OUTPUT, recipe.getOutput());
		});
		try
		{
			config.save(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void clearRecipe()
	{
		recipes = recipes.stream().filter(recipe -> recipe.getType() == LinmaluRecipeController.FURNACE).collect(Collectors.toList());
		changeRecipe();
	}
	public void clearFurnace()
	{
		recipes = recipes.stream().filter(recipe -> recipe.getType() == LinmaluRecipeController.SHAPED || recipe.getType() == LinmaluRecipeController.SHAPELESS).collect(Collectors.toList());
		changeRecipe();
	}
	public void resetRecipe()
	{
		Bukkit.resetRecipes();
		Iterator<Recipe> recipe = Bukkit.recipeIterator();
		while(recipe.hasNext())
		{
			recipes.add(LinmaluRecipeController.createLinmaluRecipe(recipe.next()));
		}
		changeRecipe();
	}
	public void addRecipe(Recipe recipe)
	{
		recipes.add(LinmaluRecipeController.createLinmaluRecipe(recipe));
		changeRecipe();
	}
	public void removeRecipe(Recipe recipe)
	{
		for(LinmaluRecipeController lr : recipes)
		{
			if(lr.equalsRecipe(recipe))
			{
				recipes.remove(lr);
				changeRecipe();
				return;
			}
		}
	}
	private void changeRecipe()
	{
		Bukkit.clearRecipes();
		recipes.forEach(recipe -> Bukkit.addRecipe(recipe.toRecipe()));
		save();
	}
}
