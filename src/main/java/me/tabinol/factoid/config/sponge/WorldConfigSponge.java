package me.tabinol.factoid.config.sponge;

import static me.tabinol.factoid.config.Config.GLOBAL;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.types.Type;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.FlagValue;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.FileCopy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class WorldConfigSponge extends WorldConfig {

	public static final String FILE_LANDDEFAULT = "/landdefault.conf"; 
	public static final String FILE_WORLDCONFIG = "/worldconfig.conf"; 
	
	/** The land default. */
	ConfigurationNode landDefault;
    
    /** The world config. */
	ConfigurationNode worldConfig;

	public WorldConfigSponge(File configDir) {
		
    	try {
            // Create files (if not exist) and load
    		File landDefaultFile = new File(configDir, FILE_LANDDEFAULT);
            if (!landDefaultFile.exists()) {
            	FileCopy.copyTextFromJav(Factoid.getServer().getResource(FILE_LANDDEFAULT), landDefaultFile);
            }
            File worldConfigFile = new File(configDir, FILE_WORLDCONFIG);
            if (!worldConfigFile.exists()) {
            	FileCopy.copyTextFromJav(Factoid.getServer().getResource(FILE_WORLDCONFIG), worldConfigFile);
            }
            ConfigurationLoader<CommentedConfigurationNode> landDefaultLoader = HoconConfigurationLoader
    				.builder().setFile(landDefaultFile).build();
            landDefault = landDefaultLoader.load();
            ConfigurationLoader<CommentedConfigurationNode> worldConfigLoader = HoconConfigurationLoader
    				.builder().setFile(worldConfigFile).build();
            worldConfig = worldConfigLoader.load();
            
            // Create default (whitout type)
            defaultConfNoType = getLandDefaultConf();
   		} catch (IOException e) {
    		e.printStackTrace();
   		}
	}

    @Override
	public TreeMap<String, DummyLand> getLandOutsideArea() {

        TreeMap<String, DummyLand> landList = new TreeMap<String, DummyLand>();
        
        // We have to take _global_ first then others
        for (Entry<Object, ? extends ConfigurationNode> worldNodes : worldConfig.getChildrenMap().entrySet()) {
        	if(worldNodes.getKey().toString().equals(GLOBAL)) {
            	createConfForWorld(worldNodes, landList, false);
        	}
        }
        
        // The none-global
        for (Entry<Object, ? extends ConfigurationNode> worldNodes : worldConfig.getChildrenMap().entrySet()) {
        	if(!worldNodes.getKey().toString().equals(GLOBAL)) {
            	createConfForWorld(worldNodes, landList, true);
        	}
        }

        return landList;
    }

    private void createConfForWorld(Entry<Object, ? extends ConfigurationNode> worldNodes, 
    		TreeMap<String, DummyLand> landList, 
    		boolean copyFromGlobal) {
    	
        String worldName = worldNodes.getKey().toString();
        Factoid.getFactoidLog().write("Create conf for World: " + worldName);
        DummyLand dl = new DummyLand(worldName);
        if(copyFromGlobal) {
        	landList.get(GLOBAL).copyPermsFlagsTo(dl);
        }
        landList.put(worldName, landModify(dl, worldConfig, 
        		worldName + ".ContainerPermissions", worldName + ".ContainerFlags"));
    }
    
    /**
     * Gets the land default conf.
     *
     * @return the land default conf
     */
    private DummyLand getLandDefaultConf() {

        Factoid.getFactoidLog().write("Create default conf for lands");
        return landModify(new DummyLand(GLOBAL), landDefault, "ContainerPermissions", "ContainerFlags");
    }

    @Override
    public TreeMap<Type, DummyLand> getTypeDefaultConf() {
    	
        Factoid.getFactoidLog().write("Create default conf for lands");
    	TreeMap<Type, DummyLand> defaultConf = new TreeMap<Type, DummyLand>();

    	for(Type type : Factoid.getTypes().getTypes()) {
    		ConfigurationNode typeConf = landDefault.getNode(type.getName());
    		DummyLand dl = new DummyLand(type.getName());
            defaultConfNoType.copyPermsFlagsTo(dl);
    		defaultConf.put(type, landModify(dl, typeConf, 
    				"ContainerPermissions", "ContainerFlags"));
    	}
    	
    	return defaultConf;
    }

    private DummyLand landModify(DummyLand dl, ConfigurationNode fc, String perms, String flags) {

        ConfigurationNode csPerm = null;
        ConfigurationNode csFlags = null;
        
        if(fc != null) {
            csPerm = fc.getNode(perms);
            csFlags = fc.getNode(flags);
        }

        // Add permissions
        if (csPerm != null) {
            for (Entry<Object, ? extends ConfigurationNode> containers : csPerm.getChildrenMap().entrySet()) {
                
                PlayerContainerType pcType = PlayerContainerType.getFromString(containers.getKey().toString());
                
                if (pcType.hasParameter()) {
                    for (Entry<Object, ? extends ConfigurationNode> containerNames : containers.getValue().getChildrenMap().entrySet()) {
                        for (Entry<Object, ? extends ConfigurationNode> permss : containerNames.getValue().getChildrenMap().entrySet()) {
                            Factoid.getFactoidLog().write("Container: " + containers.getKey() + ":" 
                            		+ containerNames.getKey() + ", " + permss.getKey());
                            
                            // Remove _ if it is a Bukkit Permission
                            String containerNameLower;
                            if(pcType == PlayerContainerType.PERMISSION) {
                                containerNameLower = containerNames.getKey().toString().toLowerCase().replaceAll("_", ".");
                            } else {
                                containerNameLower = containerNames.getKey().toString().toLowerCase();
                            }
                            
                            dl.addPermission(
                                    PlayerContainer.create(null, pcType, containerNameLower),
                                    new Permission(Factoid.getParameters().getPermissionTypeNoValid(
                                    		permss.getKey().toString().toUpperCase()),
                                            permss.getValue().getNode("Value").getBoolean(true),
                                            permss.getValue().getNode("Heritable").getBoolean(true)));
                        }
                    }
                } else {
                    for (Entry<Object, ? extends ConfigurationNode> permss : containers.getValue().getChildrenMap().entrySet()) {
                        Factoid.getFactoidLog().write("Container: " + containers.getKey() + ", " + permss.getKey());
                        dl.addPermission(
                                PlayerContainer.create(null, pcType, null),
                                new Permission(Factoid.getParameters().getPermissionTypeNoValid(permss.getKey().toString().toUpperCase()),
                                        permss.getValue().getNode("Value").getBoolean(true),
                                        permss.getValue().getNode("Heritable").getBoolean(true)));
                    }
                }
            }
        }

        // add flags
        if (csFlags != null) {
            for (Entry<Object, ? extends ConfigurationNode> flagss : csFlags.getChildrenMap().entrySet()) {
                Factoid.getFactoidLog().write("Flag: " + flagss.getKey());
                FlagType ft = Factoid.getParameters().getFlagTypeNoValid(flagss.getKey().toString().toUpperCase());
                dl.addFlag(new LandFlag(ft,
                        FlagValue.getFromString(flagss.getValue().getNode("Value").getString(), ft), 
                        fc.getNode("Heritable").getBoolean(true)));
            }
        }
        
        return dl;
    }
}
