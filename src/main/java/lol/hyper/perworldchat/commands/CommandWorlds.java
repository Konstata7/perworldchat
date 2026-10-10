/*
 * This file is part of PerWorldChat.
 *
 * PerWorldChat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PerWorldChat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PerWorldChat.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.perworldchat.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lol.hyper.perworldchat.WorldGroups;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommandWorlds implements BasicCommand {

    private static final String ADMIN_PERMISSION = "perworldchat.admin";

    private final WorldGroups worldGroups;

    public CommandWorlds(WorldGroups worldGroups) {
        this.worldGroups = worldGroups;
    }

    @Override
    public void execute(@NonNull CommandSourceStack source, String @NonNull [] args) {
        CommandSender sender = source.getSender();

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            listWorlds(sender);
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "group" -> group(sender, args);
            case "ungroup" -> ungroup(sender, args);
            case "delete" -> delete(sender, args);
            case "reload" -> reload(sender);
            default -> sender.sendMessage(Component.text("Usage: /worlds [list|group|ungroup|delete|reload]").color(NamedTextColor.RED));
        }
    }

    private void listWorlds(CommandSender sender) {
        sender.sendMessage(Component.text("------------------Worlds-------------------").color(NamedTextColor.GOLD));
        for (World world : Bukkit.getWorlds()) {
            Set<String> playersInWorld = new HashSet<>();
            world.getPlayers().forEach(player -> playersInWorld.add(player.getName()));

            String groupName = worldGroups.getGroupName(world.getName());
            String label = groupName == null ? world.getName() : world.getName() + " [" + groupName + "]";

            Component worldMessage = Component.text(label + " (" + playersInWorld.size() + "): ").color(NamedTextColor.GOLD).append(Component.text(String.join(", ", playersInWorld)).color(NamedTextColor.YELLOW));
            sender.sendMessage(worldMessage);
        }
        sender.sendMessage(Component.text("-------------------------------------------").color(NamedTextColor.GOLD));
    }

    private void group(CommandSender sender, String[] args) {
        if (!checkAdmin(sender)) {
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /worlds group <name> <world...>").color(NamedTextColor.RED));
            return;
        }

        String groupName = args[1];
        List<String> worlds = new ArrayList<>(Arrays.asList(args).subList(2, args.length));
        worldGroups.setGroup(groupName, worlds);
        sender.sendMessage(Component.text("Grouped " + String.join(", ", worlds) + " into '" + groupName + "'.").color(NamedTextColor.GREEN));
    }

    private void ungroup(CommandSender sender, String[] args) {
        if (!checkAdmin(sender)) {
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /worlds ungroup <world>").color(NamedTextColor.RED));
            return;
        }

        if (worldGroups.removeWorld(args[1])) {
            worldGroups.save();
            sender.sendMessage(Component.text("Removed '" + args[1] + "' from its group.").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("'" + args[1] + "' is not in any group.").color(NamedTextColor.RED));
        }
    }

    private void delete(CommandSender sender, String[] args) {
        if (!checkAdmin(sender)) {
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /worlds delete <group>").color(NamedTextColor.RED));
            return;
        }

        if (worldGroups.removeGroup(args[1])) {
            worldGroups.save();
            sender.sendMessage(Component.text("Deleted group '" + args[1] + "'.").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Group '" + args[1] + "' does not exist.").color(NamedTextColor.RED));
        }
    }

    private void reload(CommandSender sender) {
        if (!checkAdmin(sender)) {
            return;
        }
        worldGroups.reload();
        sender.sendMessage(Component.text("Reloaded world groups.").color(NamedTextColor.GREEN));
    }

    private boolean checkAdmin(CommandSender sender) {
        if (!sender.hasPermission(ADMIN_PERMISSION)) {
            sender.sendMessage(Component.text("You do not have permission to do that.").color(NamedTextColor.RED));
            return false;
        }
        return true;
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack source, String @NonNull [] args) {
        if (args.length <= 1) {
            return Arrays.asList("list", "group", "ungroup", "delete", "reload");
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "ungroup":
                if (args.length == 2) {
                    return worldNames();
                }
                break;
            case "delete":
                if (args.length == 2) {
                    return new ArrayList<>(worldGroups.getGroupNames());
                }
                break;
            case "group":
                if (args.length == 2) {
                    return new ArrayList<>(worldGroups.getGroupNames());
                }
                return worldNames();
            default:
                break;
        }
        return Collections.emptyList();
    }

    private List<String> worldNames() {
        List<String> names = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            names.add(world.getName());
        }
        return names;
    }
}
