package com.linmalu.customrecipes;

import com.linmalu.customrecipes.controller.LinmaluConfigController;
import com.linmalu.library.api.LinmaluMain;

public class Main extends LinmaluMain
{
	public static final String INVENTORY_TITLE = "LinmaluCustomRecipes - 린마루";

	public static Main getMain()
	{
		return (Main)LinmaluMain.getInstance();
	}

	private LinmaluConfigController config;

	@Override
	public void onEnable()
	{
		super.onEnable();
		config = new LinmaluConfigController();
		config.load();
		new Main_Command(this);
		new Main_Event(this);
	}

	public LinmaluConfigController getRecipeConfig()
	{
		return config;
	}
}
