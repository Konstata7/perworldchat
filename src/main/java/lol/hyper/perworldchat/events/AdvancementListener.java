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

import lol.hyper.perworldchat.PerWorldChat;
import lol.hyper.perworldchat.WorldGroups;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.HashSet;
import java.util.Set;

public class AdvancementListener implements Listener {

    private final PerWorldChat plugin;
    private final WorldGroups worldGroups;

    public AdvancementListener(PerWorldChat plugin, WorldGroups worldGroups) {
        this.plugin = plugin;
        this.worldGroups = worldGroups;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        if (!plugin.getConfig().getBoolean("separate-advancements", true)) {
            return;
        }

        Component message = event.message();
        if (message == null) {
            return;
        }
        event.message(null);

        Set<Player> recipients = new HashSet<>();
        for (World world : worldGroups.getChatWorlds(event.getPlayer().getWorld())) {
            recipients.addAll(world.getPlayers());
        }
        for (Player recipient : recipients) {
            recipient.sendMessage(message);
        }
    }
}
