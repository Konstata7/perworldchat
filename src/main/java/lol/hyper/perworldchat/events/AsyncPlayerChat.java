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

package lol.hyper.perworldchat.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import lol.hyper.perworldchat.WorldGroups;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class AsyncPlayerChat implements Listener {

    private final WorldGroups worldGroups;

    public AsyncPlayerChat(WorldGroups worldGroups) {
        this.worldGroups = worldGroups;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        World currentWorld = event.getPlayer().getWorld();
        Set<Player> recipients = new HashSet<>();
        for (World world : worldGroups.getChatWorlds(currentWorld)) {
            recipients.addAll(world.getPlayers());
        }
        event.viewers().retainAll(recipients);
    }
}
