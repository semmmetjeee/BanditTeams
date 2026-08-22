package me.semmmetje.banditteams.listener;

import me.semmmetje.banditteams.BanditTeams;
import me.semmmetje.banditteams.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class TeamPvpListener implements Listener {
  private final BanditTeams plugin; public TeamPvpListener(BanditTeams plugin){this.plugin=plugin;}
  @EventHandler public void damage(EntityDamageByEntityEvent event){if(!(event.getEntity() instanceof Player victim))return;Player attacker=attacker(event);if(attacker==null||!plugin.teams().sameTeam(attacker.getUniqueId(),victim.getUniqueId()))return;event.setCancelled(true);attacker.sendActionBar(Text.component(plugin.getConfig().getString("messages.friendly-fire","&cFriendly fire is disabled.")));}
  private Player attacker(EntityDamageByEntityEvent event){if(event.getDamager() instanceof Player player)return player;if(event.getDamager() instanceof Projectile projectile&&projectile.getShooter() instanceof Player player)return player;return null;}
}
