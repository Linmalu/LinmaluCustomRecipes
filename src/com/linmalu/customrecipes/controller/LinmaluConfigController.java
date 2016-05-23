package com.linmalu.customrecipes.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;

import com.linmalu.customrecipes.Main;
import com.linmalu.customrecipes.controller.LinmaluRecipeController.LinmaluRecipe;

public class LinmaluConfigController
{
	private static final File file = new File(Main.getMain().getDataFolder(), "config.yml");
	private static List<LinmaluRecipe> recipes = new ArrayList<>();

	public void load()
	{
		LinmaluRecipeController.createLinmaluRecipe(null);
		recipes.clear();
		if(file.exists())
		{
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			for(String key : config.getKeys(false))
			{
				recipes.add((LinmaluRecipe)config.get(key));
			}
			changeRecipe();
		}
		else
		{
			Iterator<Recipe> recipe = Bukkit.recipeIterator();
			while(recipe.hasNext())
			{
				recipes.add(LinmaluRecipeController.createLinmaluRecipe(recipe.next()));
			}
			changeRecipe();
		}
	}
	public void save()
	{
		YamlConfiguration config = new YamlConfiguration();
		recipes.forEach(recipe -> config.set(String.valueOf(recipe.hashCode()), recipe));
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
		recipes.clear();
		Bukkit.resetRecipes();
		for(Iterator<Recipe> recipe = Bukkit.recipeIterator(); recipe.hasNext();)
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
		for(LinmaluRecipe lr : recipes)
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
		recipes.stream().filter(recipe -> recipe.getType() != LinmaluRecipeController.NONE).forEach(recipe -> Bukkit.addRecipe(recipe.toRecipe()));
		save();
	}
}
