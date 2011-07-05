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
import org.bukkit.command.ConsoleCommandSender;

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
        if (args.length == 0) {
            return false;
        }

        String command = args[0];

        if (command.equals("list") || command.equals("vlist")) {
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
        
        if (args.length < 2) {
            sender.sendMessage("§cYou must specify a Plugin Name!");
            return true;
        }

        int pNameStart = 1;
        
        if(args[0].equals("describe"))
        {
            pNameStart = 2;
        }

        StringBuilder pname = new StringBuilder();
        for (int i = pNameStart; i < args.length; i++) {
            if (i == pNameStart) {
                pname.append(args[i]);
            } else {
                pname.append(" ").append(args[i]);
            }
        }
        String pluginName = pname.toString();

        if (command.equals("load")) {
            loadPlugin(sender, pluginName);
            return true;
        }

        Plugin targetPlugin = serverPM.getPlugin(pluginName);
        if (targetPlugin == null) {
            sender.sendMessage("§cInvalid plugin, check name and try again.");
            return true;
        }

        if (command.equals("reload")) {
            reloadPlugin(sender, targetPlugin);
            return true;
        } else if (command.equals("disable")) {
            disablePlugin(sender, targetPlugin);
            return true;
        } else if (command.equals("enable")) {
            enablePlugin(sender, targetPlugin);
            return true;
        } else if (command.equals("info")) {
            getPluginInfo(sender, targetPlugin);
            return true;
        }
        else if(command.equals("usage"))
        {
            listCommands(sender,targetPlugin);
            return true;
        }
        else if(command.equals("describe"))
        {
            describeCommand(sender,targetPlugin,args[1]);
            return true;
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
                if (authority.has(player, permission)) {
                    return true;
                } else {
                    return false;
                }
            } else if (sender instanceof ConsoleCommandSender) {
                return true;
            }
            return false;
        } else {
            if (sender instanceof Player) {
                Player person = (Player)sender;
                if(person.isOp())
                    return true;
            }
            else if(sender instanceof ConsoleCommandSender)
            {
                return true;
            }
            return false;
        }
    }

    private void listPluginsByPage(CommandSender sender, int page, boolean appendVersion) {
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.list")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = serverPM.getPlugins();
        int pagecount = (int) Math.ceil(((double)plugins.length) / ((double)10));
        if(page>pagecount || page < 1)
        {
            sender.sendMessage("§cInvalid page...");
            return;
        }
        pluginList.append("§ePlugin List <Page §a").append(page).append("§e of §a").append(pagecount).append("§e>: ");

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
                pluginList.append(" §a\"");
            } else {
                pluginList.append(" §c\"");
            }
            pluginList.append(thisPlugin.getDescription().getName());
            if (appendVersion) {
                pluginList.append(" [").append(thisPlugin.getDescription().getVersion()).append("]");
            }
            pluginList.append("\"");
        }
        sender.sendMessage(pluginList.toString());
    }

    private void listPlugins(CommandSender sender, boolean appendVersion) {
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.list")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = serverPM.getPlugins();

        pluginList.append("§ePlugin List:");

        for (int i = 0; i < plugins.length; i++) {
            Plugin thisPlugin = plugins[i];
            if (thisPlugin.isEnabled()) {
                pluginList.append(" §a\"");
            } else {
                pluginList.append(" §c\"");
            }
            pluginList.append(thisPlugin.getDescription().getName());
            if(appendVersion)
                pluginList.append(" [").append(thisPlugin.getDescription().getVersion()).append("]");
            pluginList.append("\"");
        }
        sender.sendMessage(pluginList.toString());
    }

    private void getPluginInfo(CommandSender sender, Plugin targetPlugin) {
        String pluginName = targetPlugin.getDescription().getName();
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.list")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        if (targetPlugin.isEnabled()) {
            sender.sendMessage("§e[" + targetPlugin.getDescription().getName() + "] Status: §aEnabled");
        } else {
            sender.sendMessage("§e[" + targetPlugin.getDescription().getName() + "] Status: §cDisabled");
        }

        String version = targetPlugin.getDescription().getVersion();
        if (version == null || version.equals("")) {
            sender.sendMessage("§c" + pluginName + " has a invalid version field.");
        } else {
            sender.sendMessage("Version: §a" + targetPlugin.getDescription().getVersion());
        }

        ArrayList<String> authors = targetPlugin.getDescription().getAuthors();
        if (authors.isEmpty()) {
            sender.sendMessage("§c" + pluginName + " has no authors listed.");
        } else {
            StringBuilder authorString = new StringBuilder();
            authorString.append("§eAuthor(s): §a");
            for (int i = 0; i < authors.size(); i++) {
                if (i == 0) {
                    authorString.append(authors.get(i));
                } else {
                    authorString.append(", ").append(authors.get(i));
                }
            }
            sender.sendMessage(authorString.toString());
        }

        String descript = targetPlugin.getDescription().getDescription();
        if (descript == null || descript.equals("")) {
            sender.sendMessage("§c" + pluginName + " has a invalid description field.");
        } else {
            sender.sendMessage("§eDescription: §f" + targetPlugin.getDescription().getDescription());
        }

    }

    private void disablePlugin(CommandSender sender, Plugin targetPlugin) {
        String pluginName = targetPlugin.getDescription().getName();
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        if (targetPlugin.isEnabled() == false) {
            sender.sendMessage("§ePlugin §c[" + pluginName + "]§e is already disabled!");
            return;
        }
        serverPM.disablePlugin(targetPlugin);
        if (!targetPlugin.isEnabled()) {
            sender.sendMessage("§eDisabled: §c[" + pluginName + "]");
        } else {
            sender.sendMessage("§ePlugin §cFAILED§e to Disable: §a[" + pluginName + "]");
        }
    }

    private void enablePlugin(CommandSender sender, Plugin targetPlugin) {
        String pluginName = targetPlugin.getDescription().getName();
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        if (targetPlugin.isEnabled() == true) {
            sender.sendMessage("§ePlugin §a[" + pluginName + "]§e is already enabled!");
            return;
        }
        serverPM.enablePlugin(targetPlugin);
        if (targetPlugin.isEnabled()) {
            sender.sendMessage("§eEnabled: §a[" + pluginName + "]");
        } else {
            sender.sendMessage("§ePlugin §cFAILED§e to Enable: §c[" + pluginName + "]");
        }
    }

    private void reloadPlugin(CommandSender sender, Plugin targetPlugin) {
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        disablePlugin(sender,targetPlugin);
        enablePlugin(sender,targetPlugin);
    }

    private void loadPlugin(CommandSender sender, String pluginName) {
        if (!checkAuthority(sender, "plugman.admin")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        File pluginFile = new File(new File("plugins"), pluginName + ".jar");
        if (pluginFile.isFile()) {
            try {
                Plugin newPlugin = serverPM.loadPlugin(pluginFile);
                if (newPlugin != null) {
                    pluginName = newPlugin.getDescription().getName();
                    sender.sendMessage("§ePlugin Loaded: §c[" + pluginName + "]");
                    serverPM.enablePlugin(newPlugin);
                    if (newPlugin.isEnabled()) {
                        sender.sendMessage("§ePlugin Enabled: §a[" + pluginName + "]");
                    } else {
                        sender.sendMessage("§ePlugin §cFAILED§e to Enable:§c[" + pluginName + "]");
                    }
                } else {
                    sender.sendMessage("§ePlugin §cFAILED§e to Load!");
                }
            } catch (UnknownDependencyException ex) {
                sender.sendMessage("§cFile exists but is not a plugin file.");
            } catch (InvalidPluginException ex) {
                sender.sendMessage("§cFile exists but is not a plugin file.");
            } catch (InvalidDescriptionException ex) {
                sender.sendMessage("§cPlugin exists but is invalid.");
            }
        } else {
            sender.sendMessage("§cFile does NOT exist, check name and try again.");
        }
    }

    private void listCommands(CommandSender sender, Plugin targetPlugin) {
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.describe")) {
            sender.sendMessage("§cYou don't have permission to do this...");
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
                commandsOut.append("§a");
            else
                commandsOut.append("§c");
            commandsOut.append("[").append(targetPlugin.getDescription().getName()).append("]§e Command List: ");
            for(int i = 0; i < parsedCommands.size(); i ++)
            {
                String thisCommand = parsedCommands.get(i);
                if(commandsOut.length() + thisCommand.length() > 55)
                {
                    sender.sendMessage(commandsOut.toString());
                    commandsOut = new StringBuilder();
                }
                commandsOut.append(" §e\"").append(thisCommand).append("\"");
            }
            sender.sendMessage(commandsOut.toString());
        }
        else
            sender.sendMessage("§cPlugin has no registered commands...");
    }

    private void describeCommand(CommandSender sender, Plugin targetPlugin, String commandName)
    {
        if (!checkAuthority(sender, "plugman.admin") && !checkAuthority(sender, "plugman.describe")) {
            sender.sendMessage("§cYou don't have permission to do this...");
            return;
        }
        LinkedHashMap commands = (LinkedHashMap) targetPlugin.getDescription().getCommands();
        if(commands.containsKey(commandName))
        {
            LinkedHashMap command = (LinkedHashMap) commands.get(commandName);
            if(command.containsKey("description"))
            {
               String desc = (String) command.get("description");
               sender.sendMessage("§a"+ commandName + " - §e" + desc);
            }
            else
            {
                sender.sendMessage("§cCommand has no built in description...");
            }
        }
        else
        {
            sender.sendMessage("§cCommand not found in plugin...");
        }
    }
}
