package com.forbiddenduelarena;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.World;
import net.runelite.client.game.WorldService;

@Slf4j
@PluginDescriptor(
	name = "Forbidden Duel Arena"
)
public class FDAPlugin extends Plugin
{
	private int worldNumber;
	private int worldToHopTo;
	private WorldPoint playerPoint;
	private WorldResult worldResult;
	private World world;
	private boolean worldTypeCheck;
	private net.runelite.api.World rsWorld;
	private AntiStakeQuoteManager antiStakeQuoteManager;
	private Random rand;

	@Inject
	private WorldService worldService;

	@Inject
	private Client client;

	@Inject
	private FDAConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;


	@Override
	protected void startUp() throws Exception
	{
		log.info("FDA started!");
		antiStakeQuoteManager = new AntiStakeQuoteManager();
		rand = new Random();
		loadWorld();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("FDA stopped!");
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		checkIfPlayerInDuelArena();
	}

	public void checkIfPlayerInDuelArena()
	{
		worldNumber = client.getWorld();

		if (worldNumber == 302)
		{
			playerPoint = client.getLocalPlayer().getWorldLocation();

			if (playerPoint.getX() >= 3312 && playerPoint.getX() <= 3400 && playerPoint.getY() >= 3201 && playerPoint.getY() <= 3286)
			{
				log.info("This player is about to stake...");
				client.hopToWorld(rsWorld);
				showAntiStakeQuote();
			}
		}
	}

	public void loadWorld()
	{
		worldResult = worldService.getWorlds();
		ArrayList<Integer> worldList = new ArrayList<Integer>();
		for (World w : worldResult.getWorlds())
		{
			worldList.add(w.getId());
		}
		Collections.shuffle(worldList);
		worldToHopTo = worldList.get(0);
		worldTypeCheck = true;

		WorldType[] bannedWorldTypes = {WorldType.PVP, WorldType.HIGH_RISK, WorldType.DEADMAN_TOURNAMENT, WorldType.BOUNTY, WorldType.DEADMAN, WorldType.SKILL_TOTAL, WorldType.TOURNAMENT, WorldType.LEAGUE};
		while (worldTypeCheck)
		{
			world = worldResult.findWorld(worldToHopTo);
			for (WorldType bannedWorldType : bannedWorldTypes)
			{
				log.info("World and World Type:" + world.getId() + " || " + WorldUtil.toWorldTypes(world.getTypes()));
				if (world == null || WorldUtil.toWorldTypes(world.getTypes()).contains(bannedWorldType) || !WorldUtil.toWorldTypes(world.getTypes()).contains(WorldType.MEMBERS))
				{
					Collections.shuffle(worldList);
					worldToHopTo = worldList.get(0);
					world = worldResult.findWorld(worldToHopTo);
				}
				else
				{
					worldTypeCheck = false;
				}
			}
		}
		rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
	}

	public void showAntiStakeQuote()
	{
		String chatMsg = antiStakeQuoteManager.getRandomQuote();
		final String message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(chatMsg)
				.build();
		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}

	@Provides
	FDAConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FDAConfig.class);
	}
}
