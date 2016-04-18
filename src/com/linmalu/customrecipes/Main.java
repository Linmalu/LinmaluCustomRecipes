package com.linmalu.customrecipes;

import com.linmalu.customrecipes.controller.LinmaluConfigController;
import com.linmalu.library.api.LinmaluMain;

public class Main extends LinmaluMain
{
	public static final String INVENTORY_TITLE = "LinmaluCustomRecipes - 린마루";

	public static Main getMain()
	{
		return (Main)LinmaluMain.getMain();
	}

	private LinmaluConfigController config;

	@Override
	public void onEnable()
	{
		super.onEnable();
		registerCommand(new Main_Command());
		registerEvents(new Main_Event());
		config = new LinmaluConfigController();
		config.load();
	}
	public LinmaluConfigController getRecipeConfig()
	{
		return config;
	}
}
