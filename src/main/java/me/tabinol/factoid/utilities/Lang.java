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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;


/**
 * The Class Lang.
 */
public class Lang {

    /** The Constant ACTUAL_VERSION. */
    public static final int ACTUAL_VERSION = Factoid.getMavenAppProperties().getPropertyInt("langVersion");
    
    /** The lang. */
    private String lang = null;
    
    /** The lang file. */
    private File langFile;

    Properties properties;

    /**
     * Instantiates a new lang.
     */
    public Lang() {
        this.properties = new Properties();
        reloadConfig();
        checkVersion();
    }

    /**
     * Reload config.
     */
    public final void reloadConfig() {
        this.lang = Factoid.getConf().getLang();
        this.langFile = new File(Factoid.getServer().getDataFolder() 
        		+ "/lang/", lang + ".properties");
        if (Factoid.getConf().getLang() != null) {
            copyLang();
            loadProperties();
        }
    }

    // Check if it is the next version, if not, the file will be renamed
    /**
     * Check version.
     */
    public final void checkVersion() {

        int fileVersion = Integer.parseInt(properties.getProperty("VERSION"));

        // We must rename the file and activate the new file
        if (ACTUAL_VERSION != fileVersion) {
            langFile.renameTo(new File(Factoid.getServer().getDataFolder() 
            		+ "/lang/", lang + ".properties.v" + fileVersion));
            reloadConfig();
            Factoid.getServer().info("There is a new language file. Your old language file was renamed \""
                    + lang + ".properties.v" + fileVersion + "\".");
        }
    }

    /**
     * Gets the message.
     *
     * @param path the path
     * @param param the param
     * @return the message
     */
    public String getMessage(String path, String... param) {

        String message = properties.getProperty(path);

        if (message == null) {
            return "MESSAGE NOT FOUND FOR PATH: " + path;
        }
        if (param.length >= 1) {
            int occurence = getOccurence(message, '%');
            if (occurence == param.length) {
                for (int i = 0; i < occurence; i++) {
                    message = replace(message, "%", param[i]);
                    // System.out.print(message);
                }
            } else {
                return "Error! variable missing for Entries.";
            }
        }

        return message;
    }

    /**
     * Checks if is message exist.
     *
     * @param path the path
     * @return true, if is message exist
     */
    public boolean isMessageExist(String path) {

        return properties.containsKey(path);
    }

    /**
     * Replace.
     *
     * @param s_original the s_original
     * @param s_cherche the s_cherche
     * @param s_nouveau the s_nouveau
     * @return the string
     */
    public String replace(String s_original, String s_cherche, String s_nouveau) {
        if ((s_original == null) || (s_original.equals(""))) {
            return "";
        }
        if ((s_nouveau == null) || (s_nouveau.equals("")) || (s_cherche == null) || (s_cherche.equals(""))) {
            return new String(s_original);
        }

        StringBuffer s_final;
        int index = s_original.indexOf(s_cherche);

        s_final = new StringBuffer(s_original.substring(0, index));
        s_final.append(s_nouveau);
        s_final.append(s_original.substring(index + s_cherche.length()));

        return s_final.toString();
    }

    /**
     * Load properties.
     */
    private void loadProperties() {
        try {
            
            InputStream resource = new FileInputStream(langFile);
            properties.load(resource);
            resource.close();
        
        } catch (IOException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Copyt the language file.
     */
    private void copyLang() {
        try {
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                FileCopy.copyTextFromJav(Factoid.getServer().getResource("/lang/" + lang + ".properties"), langFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the occurence.
     *
     * @param s the s
     * @param r the r
     * @return the occurence
     */
    private int getOccurence(String s, char r) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == r) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Gets the help.
     *
     * @param mainCommand the main command
     * @param commandName the command name
     * @return the help
     */
    public String getHelp(String mainCommand, String commandName) {
        
        // No help for this command?
        if(!properties.containsKey("HELP." + mainCommand + "." + commandName + ".1")) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        int t = 1;
        String str;
        
        
        while ((str = properties.getProperty("HELP." + mainCommand + "." + commandName + "." + t)) != null){
            sb.append(str).append(Config.NEWLINE);
        }
        
        return sb.toString();
    }
}
