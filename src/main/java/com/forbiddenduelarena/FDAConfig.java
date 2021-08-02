package com.forbiddenduelarena;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface FDAConfig extends Config
{
	@ConfigItem(
		keyName = "FDA",
		name = "Forbidden Duel Arena",
		description = "Don't go staking today!"
	)
	default String greeting()
	{
		return "Stop staking!";
	}
}
