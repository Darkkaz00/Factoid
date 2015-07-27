/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */ 
package me.tabinol.factoid.minecraft.sponge;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.sponge.ConfigSponge;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.CallEvents;
import me.tabinol.factoid.minecraft.ChatPaginator;
import me.tabinol.factoid.minecraft.DependPlugin;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.FSign;
import me.tabinol.factoid.minecraft.Server;
import me.tabinol.factoid.minecraft.Task;
import me.tabinol.factoid.utilities.FactoidRunnable;

/**
 * Main class for Sponge
 * @author Tabinol
 *
 */
@Plugin(id = "Factoid", name = "Factoid", version = "1.3.0-SNAPSHOT")
public class ServerSponge implements Server {
    
    @Inject
    private Logger logger;
    
    @Inject 
    private Game game;
    
    @Inject
    private PluginContainer plugin;
    
	@Inject
	@DefaultConfig(sharedRoot = false)
	private File defaultConfig;

	@Inject 
    @ConfigDir(sharedRoot = false)
    private File configDir;

	private Factoid factoid;

    private CallEvents callEvents;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        
        factoid = new Factoid(ServerType.SPONGE, this);

        // Start Listener
        game.getEventManager().register(plugin, new ListenerSponge());
    }
    
    public void initServer() {
        
        callEvents = new CallEventsSponge(game);

        // Register commands
        OnCommand onCommand = new OnCommand();
        CommandsSponge commandsFactoid = new CommandsSponge(onCommand,     
        		"factoid",
        		"factoid.use",
        		Optional.of((Text) Texts.of("Factoid Command.")),
        		Optional.of((Text) Texts.of("Factoid Command. Use \"/fd help\" for details")),
        		Texts.of("<argument> <...>"),
        		"help");
        game.getCommandDispatcher().register(plugin, 
        		commandsFactoid, "factoid", "fd", "claim");

        CommandsSponge commandsFaction = new CommandsSponge(onCommand,     
        		"faction",
        		"factoid.faction.use",
        		Optional.of((Text) Texts.of("Factions Command.")),
        		Optional.of((Text) Texts.of("Factions Command. Use \"/fn help\" for details")),
        		Texts.of("<argument> <...>"),
        		"help");
        game.getCommandDispatcher().register(plugin, 
        		commandsFaction, "faction", "fn");

        // Add loaded worlds
        for(World world : game.getServer().getWorlds()) {
            Factoid.getServerCache().addWorld(new FWorldSponge(world));
        }

        // Add players (in case of reload)
        for(Player player : game.getServer().getOnlinePlayers()) {
            Factoid.getServerCache().addPlayer(new FPlayerSponge(player));
        }
    }
    
    @Override
    public Config newConfig() {
        
        return new ConfigSponge(defaultConfig, configDir);
    }
    
    @Override
    public DependPlugin newDependPlugin() {
        
        return new DependPluginSponge(game);
    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {
        
        factoid.serverStop();
    }

    @Override
    public Factoid getFactoid() {
        
        return factoid;
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public Task createTask(FactoidRunnable runnable, Long tick, boolean multiple) {
        
        org.spongepowered.api.service.scheduler.Task task;
        
        runnable.stopNextRun();
        
        if(multiple) {
            task = game.getScheduler()
            		.getTaskBuilder()
            		.execute(runnable)
            		.delay(tick)
            		.interval(tick)
            		.submit(plugin);
        } else {
            task = game.getScheduler()
            		.getTaskBuilder()
            		.execute(runnable)
            		.delay(tick)
            		.submit(plugin);
        }
        
        return new TaskSponge(task);
    }

    @Override
    public void callTaskNow(Runnable runnable) {
        
        game.getScheduler()
        	.getTaskBuilder()
        	.execute(runnable)
        	.submit(plugin);
    }

    @Override
    public File getDataFolder() {

        return configDir;
    }
    
    @Override
    public InputStream getResource(String path) {
        
        return this.getClass().getResourceAsStream(path);
    }

    @Override
    public String getVersion() {
        
        return plugin.getVersion();
    }

    @Override
    public CallEvents CallEvents() {

        return callEvents;
    }

    @Override
    public String getOfflinePlayerName(UUID uuid) {
        
        Optional<Player> playerOp = game.getServer().getPlayer(uuid);
        
        if(playerOp.isPresent()) {
            return playerOp.get().getName();
        } else {
            return null;
        }
    }

    @Override
    public FPlayer getOfflinePlayer(UUID uuid) {

        Optional<Player> playerOp = game.getServer().getPlayer(uuid);
        
        if(playerOp.isPresent()) {
            return new FPlayerSponge(playerOp.get());
        } else {
            return null;
        }
    }

    @Override
    public ChatPaginator getChatPaginator(String text, int pageNumber) {
        
        return new ChatPaginatorSponge(text, pageNumber, game);
    }

    @Override
    public String[] getMaterials() {
        
        Field[] materials = ItemTypes.class.getDeclaredFields();
        String[] names = new String[materials.length];
        
        for (int i = 0; i < materials.length; i++) {
        	names[i] = materials[i].getName();
        }
        return names;
    }

    @Override
    public me.tabinol.factoid.minecraft.Item getBlockItem(Point point) {
        
        World world = ((FWorldSponge) point.getWorld()).getWorld();
        
        return new ItemSponge(world.getBlock(SpongeUtils.toLocationVectorI(point)).getType());
    }

    @Override
    public byte getByteItem(Point point) {
        
        //  TODO Block type in sponge
    	return 0;
    }

    @Override
    public void removeBlockAndDropSign(Point point) {
        
        World world = ((FWorldSponge) point.getWorld()).getWorld();
        Location loc = SpongeUtils.toLocation(world, point);
        
        // Remove block
        BlockState state = BlockTypes.AIR.getDefaultState();
        loc.setBlock(state);
        
        // Create entity (item drop)
        Optional<Entity> optional = world.createEntity(EntityTypes.DROPPED_ITEM, loc.getPosition());
        if (optional.isPresent()) {
            ItemStack itemStack = game.getRegistry()
                    .getItemBuilder().itemType(ItemTypes.SIGN).build();
            Item item = (Item) optional.get();
            item.getItemData().setValue(itemStack);
            world.spawnEntity(item);
        }
    }

    @Override
    public void loadChunk(Point point) {
        
        World world = ((FWorldSponge) point.getWorld()).getWorld();
        
        world.loadChunk(SpongeUtils.toLocationVectorI(point), false);
    }

    @Override
    public FSign getSign(Point point) throws SignException {
        
        World world = ((FWorldSponge) point.getWorld()).getWorld();
        Location loc = SpongeUtils.toLocation(world, point);
        
        if(loc.getBlockType() != BlockTypes.WALL_SIGN && loc.getBlockType() != BlockTypes.STANDING_SIGN) {
            throw new SignException();
        }

        return new FSignSponge(loc);
    }

    @Override
    public FSign createSign(Point point, float yaw, String[] lines, Land land,
            boolean isWallSign) throws SignException {
    
        return new FSignSponge(game, point, yaw, lines, land, isWallSign);
    }
}
