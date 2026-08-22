package me.semmmetje.banditteams.storage;

import me.semmmetje.banditteams.BanditTeams;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TeamStore {
  private final BanditTeams plugin; private final File file; private final YamlConfiguration yaml; private final Map<String,Team> teams=new LinkedHashMap<>();
  public TeamStore(BanditTeams plugin){this.plugin=plugin;file=new File(plugin.getDataFolder(),"teams.yml");yaml=YamlConfiguration.loadConfiguration(file);load();}
  private void load(){teams.clear();ConfigurationSection root=yaml.getConfigurationSection("teams");if(root==null)return;for(String key:root.getKeys(false)){ConfigurationSection s=root.getConfigurationSection(key);if(s==null)continue;try{String name=s.getString("name",key);UUID leader=UUID.fromString(Objects.requireNonNull(s.getString("leader")));Set<UUID> members=new LinkedHashSet<>();for(String raw:s.getStringList("members"))members.add(UUID.fromString(raw));members.add(leader);teams.put(key.toLowerCase(Locale.ROOT),new Team(name,leader,members));}catch(Exception ex){plugin.getLogger().warning("Skipping invalid team '"+key+"'.");}}}
  private void save(){yaml.set("teams",null);for(Team team:teams.values()){String base="teams."+team.name().toLowerCase(Locale.ROOT);yaml.set(base+".name",team.name());yaml.set(base+".leader",team.leader().toString());yaml.set(base+".members",team.members().stream().map(UUID::toString).toList());}try{yaml.save(file);}catch(IOException ex){plugin.getLogger().severe("Could not save teams.yml: "+ex.getMessage());}}
  public synchronized boolean nameTaken(String name){return teams.containsKey(name.toLowerCase(Locale.ROOT));}
  public synchronized Team teamOf(UUID player){return teams.values().stream().filter(team->team.members().contains(player)).findFirst().orElse(null);}
  public synchronized Team create(String name,UUID leader){if(nameTaken(name)||teamOf(leader)!=null)return null;Team team=new Team(name,leader,new LinkedHashSet<>(Set.of(leader)));teams.put(name.toLowerCase(Locale.ROOT),team);save();return team;}
  public synchronized boolean addMember(Team team,UUID player){if(teamOf(player)!=null)return false;team.members().add(player);save();return true;}
  public synchronized boolean removeMember(Team team,UUID player){if(!team.members().remove(player))return false;if(team.members().isEmpty())teams.remove(team.name().toLowerCase(Locale.ROOT));save();return true;}
  public synchronized boolean sameTeam(UUID first,UUID second){Team team=teamOf(first);return team!=null&&team.members().contains(second);}
}
