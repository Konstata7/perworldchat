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

package lol.hyper.perworldchat;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorldGroups {

    private final PerWorldChat plugin;
    private final Map<String, Set<String>> groupsByWorld = new HashMap<>();
    private final Map<String, Set<String>> groupsByName = new LinkedHashMap<>();

    public WorldGroups(PerWorldChat plugin) {
        this.plugin = plugin;
    }

    public void load() {
        groupsByWorld.clear();
        groupsByName.clear();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("world-groups");
        if (section == null) {
            return;
        }

        for (String groupName : section.getKeys(false)) {
            List<String> worlds = section.getStringList(groupName);
            if (worlds.isEmpty()) {
                continue;
            }
            LinkedHashSet<String> members = new LinkedHashSet<>(worlds);
            groupsByName.put(groupName, members);
            for (String world : members) {
                groupsByWorld.put(world, members);
            }
        }
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    public void save() {
        Map<String, List<String>> serialized = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entry : groupsByName.entrySet()) {
            serialized.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        plugin.getConfig().set("world-groups", serialized);
        plugin.saveConfig();
    }

    public void setGroup(String groupName, List<String> worlds) {
        removeGroup(groupName);
        for (String world : worlds) {
            removeWorld(world);
        }
        LinkedHashSet<String> members = new LinkedHashSet<>(worlds);
        groupsByName.put(groupName, members);
        for (String world : members) {
            groupsByWorld.put(world, members);
        }
        save();
    }

    public boolean removeGroup(String groupName) {
        Set<String> members = groupsByName.remove(groupName);
        if (members == null) {
            return false;
        }
        for (String world : members) {
            groupsByWorld.remove(world);
        }
        return true;
    }

    public boolean removeWorld(String worldName) {
        Set<String> members = groupsByWorld.remove(worldName);
        if (members == null) {
            return false;
        }
        members.remove(worldName);
        if (members.isEmpty()) {
            groupsByName.values().remove(members);
        }
        return true;
    }

    public Set<String> getGroupNames() {
        return new LinkedHashSet<>(groupsByName.keySet());
    }

    public Set<World> getChatWorlds(World world) {
        Set<String> members = groupsByWorld.get(world.getName());
        Set<World> worlds = new LinkedHashSet<>();
        if (members == null) {
            worlds.add(world);
            return worlds;
        }
        for (String name : members) {
            World grouped = Bukkit.getWorld(name);
            if (grouped != null) {
                worlds.add(grouped);
            }
        }
        return worlds;
    }

    public String getGroupName(String worldName) {
        for (Map.Entry<String, Set<String>> entry : groupsByName.entrySet()) {
            if (entry.getValue().contains(worldName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
