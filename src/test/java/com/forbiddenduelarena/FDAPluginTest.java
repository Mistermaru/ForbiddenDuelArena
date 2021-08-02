package com.forbiddenduelarena;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FDAPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FDAPlugin.class);
		RuneLite.main(args);
	}
}