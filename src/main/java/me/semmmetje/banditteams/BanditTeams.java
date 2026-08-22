package me.semmmetje.banditteams;

import me.semmmetje.banditteams.command.TeamCommand;
import me.semmmetje.banditteams.listener.TeamPvpListener;
import me.semmmetje.banditteams.listener.TeamGui;
import me.semmmetje.banditteams.storage.TeamStore;
import me.semmmetje.banditteams.util.TeamExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.List;

public final class BanditTeams extends JavaPlugin {
  private TeamStore teams; private TeamExpansion expansion; private TeamCommand command; private Command crewCommand;

  @Override public void onEnable(){
    saveDefaultConfig();saveResource("gui.yml",false);teams=new TeamStore(this);command=new TeamCommand(this);
    registerCrewCommand();
    PluginCommand reload=getCommand("banditteams");
    if(reload==null){getLogger().severe("Missing /banditteams command.");getServer().getPluginManager().disablePlugin(this);return;}
    reload.setExecutor((sender,unused,label,args)->{
      if(!sender.hasPermission("banditteams.admin")){command.send(sender,"no-permission",java.util.Map.of());return true;}
      if(args.length==1&&args[0].equalsIgnoreCase("reload")){reloadConfig();registerCrewCommand();command.send(sender,"reloaded",java.util.Map.of());return true;}
      if(args.length>=1&&args[0].equalsIgnoreCase("admin")){return command.admin(sender,java.util.Arrays.copyOfRange(args,1,args.length));}
      command.send(sender,"reload-usage",java.util.Map.of());return true;
    });
    getServer().getPluginManager().registerEvents(command,this);getServer().getPluginManager().registerEvents(new TeamPvpListener(this),this);getServer().getPluginManager().registerEvents(new TeamGui(this),this);
    if(getServer().getPluginManager().getPlugin("PlaceholderAPI")!=null){expansion=new TeamExpansion(this);expansion.register();}
  }

  private void registerCrewCommand(){
    if(crewCommand!=null){crewCommand.unregister(Bukkit.getCommandMap());crewCommand=null;}
    if(!getConfig().getBoolean("custom-command.enabled",true))return;
    String name=getConfig().getString("custom-command.command","crew").trim().toLowerCase();
    String aliasesValue=getConfig().getString("custom-command.aliases","crews");
    List<String> aliases=aliasesValue.isBlank()?List.of():Arrays.stream(aliasesValue.split(",")).map(String::trim).filter(value->!value.isEmpty()).toList();
    crewCommand=new Command(name,"Create and manage your crew.","/"+name,aliases){
      @Override public boolean execute(CommandSender sender,String label,String[] args){return command.onCommand(sender,this,label,args);}
      @Override public List<String> tabComplete(CommandSender sender,String alias,String[] args,org.bukkit.Location location){return command.onTabComplete(sender,this,alias,args);}
    };
    Bukkit.getCommandMap().register(getDescription().getName().toLowerCase(),crewCommand);
  }

  @Override public void onDisable(){if(crewCommand!=null)crewCommand.unregister(Bukkit.getCommandMap());if(expansion!=null)expansion.unregister();}
  public TeamStore teams(){return teams;}
  public TeamCommand command(){return command;}
}
