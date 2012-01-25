package com.bekvon.bukkit.plugman;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatTools {
    public static final String Black = "\u00A70";
    public static final String Navy = "\u00A71";
    public static final String Green = "\u00A72";
    public static final String Blue = "\u00A73";
    public static final String Red = "\u00A74";
    public static final String Purple = "\u00A75";
    public static final String Gold = "\u00A76";
    public static final String LightGray = "\u00A77";
    public static final String Gray = "\u00A78";
    public static final String DarkPurple = "\u00A79";
    public static final String LightGreen = "\u00A7a";
    public static final String LightBlue = "\u00A7b";
    public static final String Rose = "\u00A7c";
    public static final String LightPurple = "\u00A7d";
    public static final String Yellow = "\u00A7e";
    public static final String White = "\u00A7f";
    public static final int lineLength = 54;

    public static List<String> wordWrap(String[] tokens) {
        List<String> out = new ArrayList<String>();
        out.add("");

        for (String s : tokens) {
            if (stripColor(out.get(out.size() - 1)).length()
                    + stripColor(s).length() + 1 > lineLength)
                out.add("");
            out.set(out.size() - 1, out.get(out.size() - 1) + s + " ");
        }

        return out;
    }

    public static String formatTitle(String title) {
        String line = ".oOo.__________________________________________________.oOo.";
        int pivot = line.length() / 2;
        String center = ".[ " + "<yellow>" + title + "<gold>" + " ].";
        String out = "<gold>" + line.substring(0, pivot - center.length() / 2);
        out += center + line.substring(pivot + center.length() / 2);
        out = colorMessage(out, "");
        return out;
    }

    public static String formatCommand(String requirement, String command,
            String subCommand, String help) {
        String out = "  ";
        if (requirement.length() > 0)
            out += "<red>" + requirement + ": ";
        out += "<teal>" + command;
        if (subCommand.length() > 0)
            out += " " + "<aqua>" + subCommand;
        if (help.length() > 0)
            out += " " + "<gray>" + " : " + help;
        out = colorMessage(out, "");
        return out;
    }

    public static String stripColor(String msg) {
        String message = msg;

        if (message.contains("<header>")) {
            message = message
                    .replaceAll("<header>",
                            (""));
        }
        if (message.contains("<option>")) {
            message = message
                    .replaceAll("<option>",
                            (""));
        }
        if (message.contains("<subheader>")) {
            message = message
                    .replaceAll("<subheader>",
                            (""));
        }
        if (message.contains("<red>")) {
            message = message
                    .replaceAll("<red>", "");
        }
        if (message.contains("<black>")) {
            message = message
                    .replaceAll("<black>", "");
        }
        if (message.contains("<darkblue>")) {
            message = message
                    .replaceAll("<darkblue>", "");
        }
        if (message.contains("<darkgreen>")) {
            message = message
                    .replaceAll("<darkgreen>", "");
        }
        if (message.contains("<blue>")) {
            message = message
                    .replaceAll("<blue>", "");
        }
        if (message.contains("<aqua>")) {
            message = message
                    .replaceAll("<aqua>", "");
        }
        if (message.contains("<teal>")) {
            message = message
                    .replaceAll("<teal>", "");
        }
        if (message.contains("<blue>")) {
            message = message
                    .replaceAll("<blue>", "");
        }
        if (message.contains("<darkred>")) {
            message = message
                    .replaceAll("<darkred>", "");
        }
        if (message.contains("<purple>")) {
            message = message
                    .replaceAll("<purple>", "");
        }
        if (message.contains("<gold>")) {
            message = message
                    .replaceAll("<gold>", "");
        }
        if (message.contains("<gray>")) {
            message = message
                    .replaceAll("<gray>", "");
        }
        if (message.contains("<darkgray>")) {
            message = message
                    .replaceAll("<darkgray>", "");
        }
        if (message.contains("<darkpurple>")) {
            message = message
                    .replaceAll("<darkpurple>", "");
        }
        if (message.contains("<green>")) {
            message = message
                    .replaceAll("<green>", "");
        }
        if (message.contains("<red>")) {
            message = message
                    .replaceAll("<red>", "");
        }
        if (message.contains("<yellow>")) {
            message = message
                    .replaceAll("<yellow>", "");
        }
        if (message.contains("<white>")) {
            message = message
                    .replaceAll("<white>", "");
        }
        return message;
    }

    public static String colorMessage(String msg, String option) {
        String message = msg;

        if (message.contains("<header>")) {
            message = message
                    .replaceAll("<header>",
                            ("<gray>[<red>*ImDeity*<gray>] "));
        }
        if (message.contains("<option>")) {
            message = message
                    .replaceAll("<option>",
                            ("<gray>[<red>*"+option+"*<gray>] "));
        }
        if (message.contains("<subheader>")) {
            message = message
                    .replaceAll("<subheader>",
                            ("<gray>[<red>*<gray>] "));
        }
        if (message.contains("<red>")) {
            message = message
                    .replaceAll("<red>", "" 
                            + ChatColor.RED);
        }
        if (message.contains("<black>")) {
            message = message
                    .replaceAll("<black>", "" 
                            + ChatColor.BLACK);
        }
        if (message.contains("<darkblue>")) {
            message = message
                    .replaceAll("<darkblue>", "" 
                            + ChatColor.DARK_BLUE);
        }
        if (message.contains("<darkgreen>")) {
            message = message
                    .replaceAll("<darkgreen>", ""
                            + ChatColor.DARK_GREEN);
        }
        if (message.contains("<blue>")) {
            message = message
                    .replaceAll("<blue>", "" 
                        + ChatColor.BLUE);
        }
        if (message.contains("<aqua>")) {
            message = message
                    .replaceAll("<aqua>", "" 
                        + ChatColor.AQUA);
        }
        if (message.contains("<teal>")) {
            message = message
                    .replaceAll("<teal>", "" 
                        + ChatColor.DARK_AQUA);
        }
        if (message.contains("<blue>")) {
            message = message
                    .replaceAll("<blue>", "" 
                        + ChatColor.BLUE);
        }
        if (message.contains("<darkred>")) {
            message = message
                    .replaceAll("<darkred>", "" 
                            + ChatColor.DARK_RED);
        }
        if (message.contains("<purple>")) {
            message = message
                    .replaceAll("<purple>", "" 
                            + ChatColor.LIGHT_PURPLE);
        }
        if (message.contains("<gold>")) {
            message = message
                    .replaceAll("<gold>", "" 
                            + ChatColor.GOLD);
        }
        if (message.contains("<gray>")) {
            message = message
                    .replaceAll("<gray>", "" 
                            + ChatColor.GRAY);
        }
        if (message.contains("<darkgray>")) {
            message = message
                    .replaceAll("<darkgray>", "" 
                            + ChatColor.DARK_GRAY);
        }
        if (message.contains("<darkpurple>")) {
            message = message
                    .replaceAll("<darkpurple>", "" 
                            + ChatColor.DARK_PURPLE);
        }
        if (message.contains("<green>")) {
            message = message
                    .replaceAll("<green>", "" 
                            + ChatColor.GREEN);
        }
        if (message.contains("<red>")) {
            message = message
                    .replaceAll("<red>", "" 
                            + ChatColor.RED);
        }
        if (message.contains("<yellow>")) {
            message = message
                    .replaceAll("<yellow>", "" 
                            + ChatColor.YELLOW);
        }
        if (message.contains("<white>")) {
            message = message
                    .replaceAll("<white>", "" 
                            + ChatColor.WHITE);
        }
        return message;
    }
    
    public static void formatAndSend(String msg, String option, Player player) {
        String message = colorMessage(msg, option);
        
        player.sendMessage(message);
    }
}