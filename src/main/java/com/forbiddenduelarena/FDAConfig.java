package com.forbiddenduelarena;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("forbiddenduelarena")
public interface FDAConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "FDA",
			name = "Forbidden Duel Arena",
			description = "Stop staking. Turn this option on!"
	)
	default boolean TurnOnFDA()
	{
		return true;
	}
}
