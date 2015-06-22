package me.tabinol.factoid.config.bukkit;

import static me.tabinol.factoid.config.Config.GLOBAL;

import java.io.File;
import java.util.Set;
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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldConfigBukkit extends WorldConfig {
	
    /** The land default. */
    private final FileConfiguration landDefault;
    
    /** The world config. */
    private final FileConfiguration worldConfig;

	public WorldConfigBukkit(JavaPlugin plugin) {
		
        // Create files (if not exist) and load
        if (!new File(plugin.getDataFolder(), "landdefault.yml").exists()) {
            plugin.saveResource("landdefault.yml", false);
        }
        if (!new File(plugin.getDataFolder(), "worldconfig.yml").exists()) {
            plugin.saveResource("worldconfig.yml", false);
        }
        landDefault = YamlConfiguration.loadConfiguration(new File(Factoid.getServer().getDataFolder(), "landdefault.yml"));
        worldConfig = YamlConfiguration.loadConfiguration(new File(Factoid.getServer().getDataFolder(), "worldconfig.yml"));
        
        // Create default (whitout type)
        defaultConfNoType = getLandDefaultConf();
		
	}

    @Override
	public TreeMap<String, DummyLand> getLandOutsideArea() {

        TreeMap<String, DummyLand> landList = new TreeMap<String, DummyLand>();
        Set<String> keys = worldConfig.getConfigurationSection("").getKeys(false);
        
        // We have to take _global_ first then others
        for (String worldName : keys) {
        	if(worldName.equalsIgnoreCase(GLOBAL)) {
            	createConfForWorld(worldName, landList, false);
        	}
        }
        
        // The none-global
        for (String worldName : keys) {
        	if(!worldName.equalsIgnoreCase(GLOBAL)) {
            	createConfForWorld(worldName, landList, true);
        	}
        }

        return landList;
    }

    private void createConfForWorld(String worldName, TreeMap<String, DummyLand> landList, boolean copyFromGlobal) {
    	
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
    		ConfigurationSection typeConf = landDefault.getConfigurationSection(type.getName());
    		DummyLand dl = new DummyLand(type.getName());
            defaultConfNoType.copyPermsFlagsTo(dl);
    		defaultConf.put(type, landModify(dl, typeConf, 
    				"ContainerPermissions", "ContainerFlags"));
    	}
    	
    	return defaultConf;
    }

    private DummyLand landModify(DummyLand dl, ConfigurationSection fc, String perms, String flags) {

        ConfigurationSection csPerm = null;
        ConfigurationSection csFlags = null;
        
        if(fc != null) {
            csPerm = fc.getConfigurationSection(perms);
            csFlags = fc.getConfigurationSection(flags);
        }

        // Add permissions
        if (csPerm != null) {
            for (String container : csPerm.getKeys(false)) {
                
                PlayerContainerType pcType = PlayerContainerType.getFromString(container);
                
                if (pcType.hasParameter()) {
                    for (String containerName : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        for (String perm : fc.getConfigurationSection(perms + "." + container + "." + containerName).getKeys(false)) {
                            Factoid.getFactoidLog().write("Container: " + container + ":" + containerName + ", " + perm);
                            
                            // Remove _ if it is a Bukkit Permission
                            String containerNameLower;
                            if(pcType == PlayerContainerType.PERMISSION) {
                                containerNameLower = containerName.toLowerCase().replaceAll("_", ".");
                            } else {
                                containerNameLower = containerName.toLowerCase();
                            }
                            
                            dl.addPermission(
                                    PlayerContainer.create(null, pcType, containerNameLower),
                                    new Permission(Factoid.getParameters().getPermissionTypeNoValid(perm.toUpperCase()),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Value"),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Heritable")));
                        }
                    }
                } else {
                    for (String perm : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        Factoid.getFactoidLog().write("Container: " + container + ", " + perm);
                        dl.addPermission(
                                PlayerContainer.create(null, pcType, null),
                                new Permission(Factoid.getParameters().getPermissionTypeNoValid(perm.toUpperCase()),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Value"),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Heritable")));
                    }
                }
            }
        }

        // add flags
        if (csFlags != null) {
            for (String flag : csFlags.getKeys(false)) {
                Factoid.getFactoidLog().write("Flag: " + flag);
                FlagType ft = Factoid.getParameters().getFlagTypeNoValid(flag.toUpperCase());
                dl.addFlag(new LandFlag(ft,
                        FlagValue.getFromString(fc.getString(flags + "." + flag + ".Value"), ft), 
                        fc.getBoolean(flags + "." + flag + ".Heritable")));
            }
        }
        
        return dl;
    }
}
