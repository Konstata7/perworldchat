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

import lol.hyper.hyperlib.HyperLib;
import lol.hyper.hyperlib.bstats.HyperStats;
import lol.hyper.hyperlib.releases.HyperUpdater;
import lol.hyper.perworldchat.commands.CommandWorlds;
import lol.hyper.perworldchat.events.AdvancementListener;
import lol.hyper.perworldchat.events.AsyncPlayerChat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PerWorldChat extends JavaPlugin {

    public AsyncPlayerChat asyncPlayerChat;
    public CommandWorlds commandWorlds;
    private WorldGroups worldGroups;

    @Override
    public void onEnable() {
        HyperLib hyperLib = new HyperLib(this);
        hyperLib.setup();

        HyperStats stats = new HyperStats(hyperLib, 11754);
        stats.setup();

        saveDefaultConfig();

        worldGroups = new WorldGroups(this);
        worldGroups.load();

        asyncPlayerChat = new AsyncPlayerChat(worldGroups);
        commandWorlds = new CommandWorlds(worldGroups);

        Bukkit.getServer().getPluginManager().registerEvents(asyncPlayerChat, this);
        Bukkit.getServer().getPluginManager().registerEvents(new AdvancementListener(this, worldGroups), this);

        registerCommand("worlds", commandWorlds);

        HyperUpdater updater = new HyperUpdater(hyperLib);
        updater.setRepository("hyperdefined", "PerWorldChat");
        updater.setModrinth("5OEklTLY");
        updater.setHangar("PerWorldChat", "paper");
        updater.check();
    }

    public WorldGroups getWorldGroups() {
        return worldGroups;
    }
}
