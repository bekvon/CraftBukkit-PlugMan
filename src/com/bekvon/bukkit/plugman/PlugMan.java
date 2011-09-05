/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.plugman;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public class PlugMan extends JavaPlugin {
    Server mcserver;
    Plugin permPlug;
    PermissionHandler authority;
    PluginManager serverPM;

    public PlugMan()
    {

    }

    public void onDisable() {
        mcserver = null;
        serverPM = null;
        permPlug = null;
        authority = null;
        Logger.getLogger("Minecraft").log(Level.INFO, "[PlugMan] Shutting Down!");
    }

    public void onEnable() {
        Logger.getLogger("Minecraft").log(Level.INFO, "[PlugMan] Starting Up! - Version: " + this.getDescription().getVersion() +" by bekvon");
        mcserver = getServer();
        serverPM = mcserver.getPluginManager();
        checkPermissions();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(this.isEnabled())
        {
            if(cmd.getName().equals("plugman"))
            {
                return doCommand(sender,args);
            }
        }
        return super.onCommand(sender, cmd, commandLabel, args);
    }

    private boolean doCommand(CommandSender sender, String args[]) {
        
        boolean error = false;
        ArrayList<String> out = new ArrayList<String>();
        
        if (args.length == 0) {
            return false;
        }

        String command = args[0];

        if (command.equals("list") || command.equals("vlist")  && !error) {
            if (args.length >= 2) {
                try {
                    int page = Integer.parseInt(args[1]);
                    if (command.equals("vlist")) {
                        listPluginsByPage(sender,page, true);
                    } else {
                        listPluginsByPage(sender, page, false);
                    }
                } catch (Exception ex) {
                }
                return true;
            }
            if (command.equals("vlist")) {
                listPlugins(sender, true);
            } else {
                listPlugins(sender, false);
            }
            return true;
        }
        
        if (args.length < 2  && !error) {
            out.add("<red>You must specify a Plugin Name!");
            error = true;
        }

        int pNameStart = 1;
        
        if(args[0].equals("describe")  && !error) {
            pNameStart = 2;
        }

        String pluginName = "";
        
        if (!error) {
            StringBuilder pname = new StringBuilder();
            for (int i = pNameStart; i < args.length; i++) {
                if (i == pNameStart) {
                    pname.append(args[i]);
                } else {
                    pname.append(" ").append(args[i]);
                }
            }
            pluginName = pname.toString();
        }

        if (command.equals("load")  && !error) {
            loadPlugin(sender, pluginName);
            return true;
        }

        Plugin targetPlugin = null;
        if (!error)
            targetPlugin = serverPM.getPlugin(pluginName);
        if (targetPlugin == null  && !error) {
            out.add("<red>Invalid plugin, check name and try again.");
            error = true;
        }

        if (command.equals("reload")  && !error) {
            reloadPlugin(sender, targetPlugin);
            return true;
        } else if (command.equals("disable")  && !error) {
            disablePlugin(sender, targetPlugin);
            return true;
        } else if (command.equals("enable")  && !error) {
            enablePlugin(sender, targetPlugin);
            return true;
        } else if (command.equals("info")  && !error) {
            getPluginInfo(sender, targetPlugin);
            return true;
        } else if(command.equals("usage")  && !error) {
            listCommands(sender,targetPlugin);
            return true;
        } else if(command.equals("describe")  && !error) {
            describeCommand(sender,targetPlugin,args[1]);
            return true;
        }
        
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }
        return false;
    }

    private void checkPermissions() {
        Plugin p = mcserver.getPluginManager().getPlugin("Permissions");
        if (p != null) {
            permPlug = p;
            authority = ((Permissions) permPlug).getHandler();
            Logger.getLogger("Minecraft").log(Level.INFO, "[PlugMan] Found Permissions Plugin!");
        } else {
            permPlug = null;
            authority = null;
            Logger.getLogger("Minecraft").log(Level.INFO, "[PlugMan] Permissions Plugin NOT Found, using OP!");
        }
    }

    private boolean checkAuthority(CommandSender sender, String permission) {
        if (permPlug != null && authority != null && permPlug.isEnabled()) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if(player.isOp())
                    return true;
                if(player.hasPermission(permission))
                    return true;
                if (authority.has(player, permission)) {
                    return true;
                } else {
                    return false;
                }
            } else 
                return true;
        } else {
            if (sender instanceof Player) {
                Player person = (Player)sender;
                if(person.isOp())
                    return true;
                if(person.hasPermission(permission))
                    return true;
            }
            else
                return true;
            return false;
        }
    }

    private void listPluginsByPage(CommandSender sender, int page, boolean appendVersion) {
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.list")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = serverPM.getPlugins();
        int pagecount = (int) Math.ceil(((double)plugins.length) / ((double)10));
        if(page>pagecount || page < 1)
        {
            sender.sendMessage("<red>Invalid page...");
            return;
        }
        pluginList.append("<yellow>Plugin List <Page <green>").append(page).append("<yellow> of <green>").append(pagecount).append("<yellow>>: ");

        page = page - 1;
        int firstplugin = 10 * page;
        int lastplugin = (10 * page) + 10;
        if(firstplugin>=plugins.length)
        {
            sender.sendMessage(pluginList.toString());
            return;
        }
        if(lastplugin >=plugins.length)
        {
            lastplugin = plugins.length;
        }

        for (int i = firstplugin; i < lastplugin; i++) {
            Plugin thisPlugin = plugins[i];
            if (thisPlugin.isEnabled()) {
                pluginList.append(" <green>\"");
            } else {
                pluginList.append(" <red>\"");
            }
            pluginList.append(thisPlugin.getDescription().getName());
            if (appendVersion) {
                pluginList.append(" [").append(thisPlugin.getDescription().getVersion()).append("]");
            }
            pluginList.append("\"");
        }
        if (sender instanceof Player)
            sender.sendMessage(ChatTools.colorMessage(pluginList.toString(), "PlugMan"));
        else
            sender.sendMessage(ChatTools.stripColor(pluginList.toString()));
    }

    private void listPlugins(CommandSender sender, boolean appendVersion) {
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.list")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = serverPM.getPlugins();

        pluginList.append("<yellow>Plugin List:");

        for (int i = 0; i < plugins.length; i++) {
            Plugin thisPlugin = plugins[i];
            if (thisPlugin.isEnabled()) {
                pluginList.append(" <green>\"");
            } else {
                pluginList.append(" <red>\"");
            }
            pluginList.append(thisPlugin.getDescription().getName());
            if(appendVersion)
                pluginList.append(" [").append(thisPlugin.getDescription().getVersion()).append("]");
            pluginList.append("\"");
        }
        if (sender instanceof Player)
            sender.sendMessage(ChatTools.colorMessage(pluginList.toString(), "PlugMan"));
        else
            sender.sendMessage(ChatTools.stripColor(pluginList.toString()));
    }

    private void getPluginInfo(CommandSender sender, Plugin targetPlugin) {
        String pluginName = targetPlugin.getDescription().getName();
        String version = targetPlugin.getDescription().getVersion();
        ArrayList<String> authors = targetPlugin.getDescription().getAuthors();
        String descript = targetPlugin.getDescription().getDescription();
        ArrayList<String> out = new ArrayList<String>();
        
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.list")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        if (targetPlugin.isEnabled()) {
            out.add("<yellow>[" + targetPlugin.getDescription().getName() + "] Status: <green>Enabled");
        } else {
            out.add("<yellow>[" + targetPlugin.getDescription().getName() + "] Status: <red>Disabled");
        }

        if (version == null || version.equals("")) {
            out.add("<red>" + pluginName + " has a invalid version field.");
        } else {
            out.add("Version: <green>" + targetPlugin.getDescription().getVersion());
        }

        
        if (authors.isEmpty()) {
            out.add("<red>" + pluginName + " has no authors listed.");
        } else {
            StringBuilder authorString = new StringBuilder();
            authorString.append("<yellow>Author(s): <green>");
            for (int i = 0; i < authors.size(); i++) {
                if (i == 0) {
                    authorString.append(authors.get(i));
                } else {
                    authorString.append(", ").append(authors.get(i));
                }
            }
            out.add(authorString.toString());
        }

        
        if (descript == null || descript.equals("")) {
            out.add("<red>" + pluginName + " has a invalid description field.");
        } else {
            out.add("<yellow>Description: <white>" + targetPlugin.getDescription().getDescription());
        }
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }

    }

    private void disablePlugin(CommandSender sender, Plugin targetPlugin) {
        String pluginName = targetPlugin.getDescription().getName();
        ArrayList<String> out = new ArrayList<String>();
        boolean error = false;
        
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        if (targetPlugin.isEnabled() == false) {
            out.add("<yellow>Plugin <red>[" + pluginName + "]<yellow> is already disabled!");
            error = true;
        }
        if (!error) {
            serverPM.disablePlugin(targetPlugin);
            if (!targetPlugin.isEnabled()) {
                out.add("<red>Disabled: <red>[" + pluginName + "]");
            } else {
                out.add("<yellow>Plugin <red>FAILED<yellow> to Disable: <green>[" + pluginName + "]");
            }
        }
        
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }
        
    }

    private void enablePlugin(CommandSender sender, Plugin targetPlugin) {
        String pluginName = targetPlugin.getDescription().getName();
        ArrayList<String> out = new ArrayList<String>();
        boolean error = false;
        
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        if (targetPlugin.isEnabled() == true) {
            out.add("<yellow>Plugin <green>[" + pluginName + "]<yellow> is already enabled!");
            error = true;
        }
        if (!error) {
            serverPM.enablePlugin(targetPlugin);
            if (targetPlugin.isEnabled()) {
                out.add("<yellow>Enabled: <green>[" + pluginName + "]");
            } else {
                out.add("<yellow>Plugin <red>FAILED<yellow> to Enable: <red>[" + pluginName + "]");
            }
        }
        
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }
    }   

    private void reloadPlugin(CommandSender sender, Plugin targetPlugin) {
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        disablePlugin(sender,targetPlugin);
        enablePlugin(sender,targetPlugin);
    }

    private void loadPlugin(CommandSender sender, String pluginName) {
        ArrayList<String> out = new ArrayList<String>();
        
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        File pluginFile = new File(new File("plugins"), pluginName + ".jar");
        if (pluginFile.isFile()) {
            try {
                Plugin newPlugin = serverPM.loadPlugin(pluginFile);
                if (newPlugin != null) {
                    pluginName = newPlugin.getDescription().getName();
                    out.add("<yellow>Plugin Loaded: <red>[" + pluginName + "]");
                    serverPM.enablePlugin(newPlugin);
                    if (newPlugin.isEnabled()) {
                        out.add("<yellow>Plugin Enabled: <green>[" + pluginName + "]");
                    } else {
                        out.add("<yellow>Plugin <red>FAILED<yellow> to Enable:<red>[" + pluginName + "]");
                    }
                } else {
                    out.add("<yellow>Plugin <red>FAILED<yellow> to Load!");
                }
            } catch (UnknownDependencyException ex) {
                out.add("<red>File exists but is not a plugin file.");
            } catch (InvalidPluginException ex) {
                out.add("<red>File exists but is not a plugin file.");
            } catch (InvalidDescriptionException ex) {
                out.add("<red>Plugin exists but is invalid.");
            }
        } else {
            out.add("<red>File does NOT exist, check name and try again.");
        }
        
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }
    }

    @SuppressWarnings("rawtypes")
    private void listCommands(CommandSender sender, Plugin targetPlugin) {
        ArrayList<String> out = new ArrayList<String>();
        
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.describe")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        ArrayList<String> parsedCommands = new ArrayList<String>();
        LinkedHashMap commands = (LinkedHashMap) targetPlugin.getDescription().getCommands();
        if (commands != null) {
            Iterator commandsIt = commands.entrySet().iterator();
            while (commandsIt.hasNext()) {
                Entry thisEntry = (Entry) commandsIt.next();
                if (thisEntry != null) {
                    parsedCommands.add((String) thisEntry.getKey());
                }
            }
        }
        if(!parsedCommands.isEmpty())
        {
            StringBuilder commandsOut = new StringBuilder();
            if(targetPlugin.isEnabled())
                commandsOut.append("<green>");
            else
                commandsOut.append("<red>");
            commandsOut.append("[").append(targetPlugin.getDescription().getName()).append("]<yellow> Command List: ");
            for(int i = 0; i < parsedCommands.size(); i ++)
            {
                String thisCommand = parsedCommands.get(i);
                if(commandsOut.length() + thisCommand.length() > 55)
                {
                    sender.sendMessage(commandsOut.toString());
                    commandsOut = new StringBuilder();
                }
                commandsOut.append(" <yellow>\"").append(thisCommand).append("\"");
            }
            out.add(commandsOut.toString());
        }
        else
            out.add("<red>Plugin has no registered commands...");
        
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }
    }

    @SuppressWarnings("rawtypes")
    private void describeCommand(CommandSender sender, Plugin targetPlugin, String commandName) {
        ArrayList<String> out = new ArrayList<String>();
    
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.describe")) {
            sender.sendMessage("<red>You don't have permission to do this...");
            return;
        }
        LinkedHashMap commands = (LinkedHashMap) targetPlugin.getDescription().getCommands();
        if(commands.containsKey(commandName))
        {

            LinkedHashMap command = (LinkedHashMap) commands.get(commandName);
            if(command.containsKey("description"))
            {
               String desc = (String) command.get("description");
               out.add("<green>"+ commandName + " - <yellow>" + desc);
            }
            else
            {
                out.add("<red>Command has no built in description...");
            }
        }
        else
        {
            out.add("<red>Command not found in plugin...");
        }
        
        for (String s : out) {
            if (sender instanceof Player)
                sender.sendMessage(ChatTools.colorMessage(s, "PlugMan"));
            else
                sender.sendMessage(ChatTools.stripColor(s));
        }
    }
}
