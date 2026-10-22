package me.semmmetje.banditteams.listener;

import me.semmmetje.banditteams.BanditTeams;
import me.semmmetje.banditteams.storage.Team;
import me.semmmetje.banditteams.util.Text;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class TeamPvpListener implements Listener {
  private final BanditTeams plugin;
  public TeamPvpListener(BanditTeams plugin){this.plugin=plugin;}

  @EventHandler
  public void damage(EntityDamageByEntityEvent event){
    if(!(event.getEntity() instanceof Player victim))return;
    // Ender pearls can cause teleport-related damage to their thrower; never treat them as team PvP.
    if(event.getDamager() instanceof EnderPearl)return;
    Player attacker=attacker(event);
    if(attacker==null||attacker.getUniqueId().equals(victim.getUniqueId()))return;
    Team team=plugin.teams().teamOf(attacker.getUniqueId());
    if(team==null||!team.members().contains(victim.getUniqueId())||team.friendlyFireEnabled())return;
    event.setCancelled(true);
    attacker.sendActionBar(Text.component(plugin.getConfig().getString("messages.friendly-fire","&cFriendly fire is disabled.")));
  }

  private Player attacker(EntityDamageByEntityEvent event){
    if(event.getDamager() instanceof Player player)return player;
    if(event.getDamager() instanceof Projectile projectile&&projectile.getShooter() instanceof Player player)return player;
    return null;
  }
}
