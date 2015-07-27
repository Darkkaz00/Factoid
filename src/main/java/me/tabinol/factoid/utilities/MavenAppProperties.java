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
package me.tabinol.factoid.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;


/**
 * Load app.properties from Maven properties
 *
 * @author Tabinol
 */
public class MavenAppProperties {

    /** The properties. */
    private final Properties properties;
    private final ServerType serverType;

    /**
     * Instantiates a new maven app properties.
     */
    public MavenAppProperties(ServerType serverType) {
        
    	this.properties = new Properties();
        this.serverType = serverType;
    }

    /**
     * Load properties.
     */
    public void loadProperties() {

        try {
            
        	InputStream resource;
        	JarFile jar;
        	
            if(serverType == ServerType.BUKKIT) {
            	File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            	jar = new JarFile(jarloc);
            	JarEntry entry = jar.getJarEntry("app.properties");
            	resource = jar.getInputStream(entry);
            } else {
            	jar = null;
            	resource = Factoid.getServer().getResource("/app.properties");
            }
            
            properties.load(resource);
            resource.close();
            if(jar != null) {
            	jar.close();
            }
        
        } catch (URISyntaxException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the property string.
     *
     * @param path the path
     * @return the property string
     */
    public String getPropertyString(String path) {

        return properties.getProperty(path);
    }

    /**
     * Gets the property int.
     *
     * @param path the path
     * @return the property int
     */
    public int getPropertyInt(String path) {

        return Integer.parseInt(properties.getProperty(path));
    }
}
