package me.tabinol.factoid.config.sponge;

import static me.tabinol.factoid.config.Config.GLOBAL;

import java.io.File;
import java.io.IOException;
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

import org.spongepowered.api.service.config.ConfigDir;

import com.google.inject.Inject;

public class WorldConfigSponge extends WorldConfig {

	public static final String FILE_LANDDEFAULT="Factoid_landdefault.conf"; 
	public static final String FILE_WORLDCONFIG="Factoid_landdefault.conf"; 
	
	@Inject
	@ConfigDir(sharedRoot = true)
	private File configDir;

	/** The land default. */
	ConfigurationNode  landDefault;
    
    /** The world config. */
	ConfigurationNode worldConfig;

	public WorldConfigSponge() {
		
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
        for (ConfigurationNode worldNode : worldConfig.getChildrenList()) {
        	if(worldNode.getKey().toString().equalsIgnoreCase(GLOBAL)) {
            	createConfForWorld(worldNode, landList, false);
        	}
        }
        
        // The none-global
        for (ConfigurationNode worldNode : worldConfig.getChildrenList()) {
        	if(!worldNode.getKey().toString().equalsIgnoreCase(GLOBAL)) {
            	createConfForWorld(worldNode, landList, true);
        	}
        }

        return landList;
    }

    private void createConfForWorld(ConfigurationNode worldNode, TreeMap<String, DummyLand> landList, boolean copyFromGlobal) {
    	
        String worldName = worldNode.toString();
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
            for (ConfigurationNode container : csPerm.getChildrenList()) {
                
                PlayerContainerType pcType = PlayerContainerType.getFromString(container.getKey().toString());
                
                if (pcType.hasParameter()) {
                    for (ConfigurationNode containerName : container.getChildrenList()) {
                        for (ConfigurationNode perm : containerName.getChildrenList()) {
                            Factoid.getFactoidLog().write("Container: " + container.getKey() + ":" 
                            		+ containerName.getKey() + ", " + perm.getKey());
                            
                            // Remove _ if it is a Bukkit Permission
                            String containerNameLower;
                            if(pcType == PlayerContainerType.PERMISSION) {
                                containerNameLower = containerName.getKey().toString().toLowerCase().replaceAll("_", ".");
                            } else {
                                containerNameLower = containerName.getKey().toString().toLowerCase();
                            }
                            
                            dl.addPermission(
                                    PlayerContainer.create(null, pcType, containerNameLower),
                                    new Permission(Factoid.getParameters().getPermissionTypeNoValid(
                                    		perm.getKey().toString().toUpperCase()),
                                            perm.getNode("Value").getBoolean(true),
                                            perm.getNode("Heritable").getBoolean(true)));
                        }
                    }
                } else {
                    for (ConfigurationNode perm : container.getChildrenList()) {
                        Factoid.getFactoidLog().write("Container: " + container + ", " + perm);
                        dl.addPermission(
                                PlayerContainer.create(null, pcType, null),
                                new Permission(Factoid.getParameters().getPermissionTypeNoValid(perm.getKey().toString().toUpperCase()),
                                        perm.getNode("Value").getBoolean(true),
                                        perm.getNode("Heritable").getBoolean(true)));
                    }
                }
            }
        }

        // add flags
        if (csFlags != null) {
            for (ConfigurationNode flag : csFlags.getChildrenList()) {
                Factoid.getFactoidLog().write("Flag: " + flag.getKey());
                FlagType ft = Factoid.getParameters().getFlagTypeNoValid(flag.getKey().toString().toUpperCase());
                dl.addFlag(new LandFlag(ft,
                        FlagValue.getFromString(flag.getNode(".Value").getString(), ft), 
                        fc.getNode("Heritable").getBoolean(true)));
            }
        }
        
        return dl;
    }
}
