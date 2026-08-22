package me.semmmetje.banditteams.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.semmmetje.banditteams.BanditTeams;
import me.semmmetje.banditteams.storage.Team;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TeamExpansion extends PlaceholderExpansion {
  private final BanditTeams plugin; public TeamExpansion(BanditTeams plugin){this.plugin=plugin;}
  @Override public @NotNull String getIdentifier(){return "banditteams";} @Override public @NotNull String getAuthor(){return "Semmmetje";} @Override public @NotNull String getVersion(){return plugin.getDescription().getVersion();}@Override public boolean persist(){return true;}
  @Override public @Nullable String onRequest(OfflinePlayer player,@NotNull String params){if(player==null)return "";if(!params.equalsIgnoreCase("name"))return null;Team team=plugin.teams().teamOf(player.getUniqueId());return team==null?plugin.getConfig().getString("placeholders.default-name","No Crew"):team.name();}
}
