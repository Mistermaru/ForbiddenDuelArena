package com.forbiddenduelarena;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WorldChanged;
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
import net.runelite.client.plugins.screenmarkers.ui.ScreenMarkerPluginPanel;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;

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
		loadWorld();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("FDA stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			// client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Yolo Swag " + config.TurnOnFDA(), null);


			checkIfPlayerInDuelArena();
		}

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
			log.info("Player location: " + playerPoint);

			if (playerPoint.getX() >= 3312 && playerPoint.getX() <= 3400 && playerPoint.getY() >= 3201 && playerPoint.getY() <= 3286)
			{
				log.info("This player is about to stake...");
				client.hopToWorld(rsWorld);

			}
		}
	}

	public void loadWorld()
	{
		worldTypeCheck = true;
		WorldType[] bannedWorldTypes = {WorldType.PVP, WorldType.HIGH_RISK, WorldType.DEADMAN_TOURNAMENT, WorldType.BOUNTY, WorldType.DEADMAN, WorldType.SKILL_TOTAL, WorldType.TOURNAMENT, WorldType.LEAGUE};
		worldToHopTo = 548;
		while (worldTypeCheck)
		{
			worldResult = worldService.getWorlds();
			world = worldResult.findWorld(worldToHopTo);
			for (WorldType worldTypeElement : bannedWorldTypes)
			{
				log.info("WorldType: " + worldTypeElement);
				if (world == null || WorldUtil.toWorldTypes(world.getTypes()).contains(worldTypeElement))
				{
					worldToHopTo = worldToHopTo + 1;
					world = worldResult.findWorld(worldToHopTo);
					log.info("WorldNumber: " + worldToHopTo);
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



	@Provides
	FDAConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FDAConfig.class);
	}
}
