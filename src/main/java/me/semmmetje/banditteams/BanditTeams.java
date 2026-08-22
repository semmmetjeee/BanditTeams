package me.semmmetje.banditteams;

import me.semmmetje.banditteams.command.TeamCommand;
import me.semmmetje.banditteams.listener.TeamPvpListener;
import me.semmmetje.banditteams.listener.TeamGui;
import me.semmmetje.banditteams.storage.TeamStore;
import me.semmmetje.banditteams.util.TeamExpansion;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class BanditTeams extends JavaPlugin {
  private TeamStore teams; private TeamExpansion expansion; private TeamCommand command;
  @Override public void onEnable(){saveDefaultConfig();saveResource("gui.yml",false);teams=new TeamStore(this);command=new TeamCommand(this);PluginCommand team=getCommand("team");if(team==null){getLogger().severe("Missing /team command.");getServer().getPluginManager().disablePlugin(this);return;}team.setExecutor(command);team.setTabCompleter(command);getServer().getPluginManager().registerEvents(command,this);getServer().getPluginManager().registerEvents(new TeamPvpListener(this),this);getServer().getPluginManager().registerEvents(new TeamGui(this),this);if(getServer().getPluginManager().getPlugin("PlaceholderAPI")!=null){expansion=new TeamExpansion(this);expansion.register();}}
  @Override public void onDisable(){if(expansion!=null)expansion.unregister();}
  public TeamStore teams(){return teams;}
  public TeamCommand command(){return command;}
}
