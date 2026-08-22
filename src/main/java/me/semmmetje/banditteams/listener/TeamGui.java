package me.semmmetje.banditteams.listener;

import me.semmmetje.banditteams.BanditTeams;
import me.semmmetje.banditteams.storage.Team;
import me.semmmetje.banditteams.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.io.File;
import java.util.*;

public final class TeamGui implements Listener {
  private static final int[] CONTENT={10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
  private final BanditTeams plugin; private YamlConfiguration gui;
  public TeamGui(BanditTeams plugin){this.plugin=plugin;load();}
  private void load(){gui=YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"gui.yml"));}
  public void members(Player viewer,Team team){openMembers(viewer,team,0);}
  public void teams(Player viewer){openTeams(viewer,0);}
  public void settings(Player viewer,Team team){load();Inventory inv=base("settings",Map.of("team",team.name()),new Holder(View.SETTINGS,team.name(),0));inv.setItem(gui.getInt("settings.requests.slot"),settingItem("settings.requests",team.joinRequestsEnabled()));inv.setItem(gui.getInt("settings.team-chat.slot"),settingItem("settings.team-chat",team.teamChatEnabled()));viewer.openInventory(inv);}
  private void openMembers(Player viewer,Team team,int page){load();List<UUID> members=team.members().stream().sorted(Comparator.comparing(this::name,String.CASE_INSENSITIVE_ORDER)).toList();int pages=pages(members.size());page=Math.max(0,Math.min(page,pages-1));Inventory inv=base("members",Map.of("team",team.name()),new Holder(View.MEMBERS,team.name(),page));for(int i=0,start=page*CONTENT.length;i<CONTENT.length&&start+i<members.size();i++)inv.setItem(CONTENT[i],memberItem(members.get(start+i),team));navigation(inv,View.MEMBERS,team.name(),page,pages);viewer.openInventory(inv);}
  private void openTeams(Player viewer,int page){load();List<Team> teams=plugin.teams().allTeams().stream().sorted(Comparator.comparing(Team::name,String.CASE_INSENSITIVE_ORDER)).toList();int pages=pages(teams.size());page=Math.max(0,Math.min(page,pages-1));Inventory inv=base("teams",Map.of(),new Holder(View.TEAMS,"",page));for(int i=0,start=page*CONTENT.length;i<CONTENT.length&&start+i<teams.size();i++)inv.setItem(CONTENT[i],teamItem(teams.get(start+i)));String create="teams.create";inv.setItem(gui.getInt(create+".slot"),item(material(create+".material",Material.EMERALD),gui.getString(create+".name","Create Team"),gui.getStringList(create+".lore")));navigation(inv,View.TEAMS,"",page,pages);viewer.openInventory(inv);}
  private Inventory base(String section,Map<String,String> vars,Holder holder){Inventory inv=Bukkit.createInventory(holder,54,Text.component(replace(gui.getString(section+".title","Teams"),vars)));Material material=material("shared.border.material",Material.BLACK_STAINED_GLASS_PANE);for(int i=0;i<54;i++)if(i<9||i>=45||i%9==0||i%9==8)inv.setItem(i,item(material,gui.getString("shared.border.name"," "),List.of()));return inv;}
  private void navigation(Inventory inv,View view,String team,int page,int pages){if(page>0)setNav(inv,"previous",view,team,page);setNav(inv,"page",view,team,page,Map.of("page",String.valueOf(page+1),"pages",String.valueOf(pages)));if(page<pages-1)setNav(inv,"next",view,team,page);}
  private void setNav(Inventory inv,String type,View view,String team,int page){setNav(inv,type,view,team,page,Map.of());}
  private void setNav(Inventory inv,String type,View view,String team,int page,Map<String,String> vars){String base="shared.navigation."+type;inv.setItem(gui.getInt(base+".slot"),item(material(base+".material",Material.PAPER),replace(gui.getString(base+".name"," "),vars),gui.getStringList(base+".lore").stream().map(line->replace(line,vars)).toList()));}
  private ItemStack settingItem(String key,boolean enabled){Map<String,String> vars=Map.of("status",enabled?"&aEnabled":"&cDisabled");return item(material(key+(enabled?".enabled-material":".disabled-material"),Material.LIME_DYE),replace(gui.getString(key+".name","Setting"),vars),gui.getStringList(key+".lore").stream().map(line->replace(line,vars)).toList());}
  private ItemStack memberItem(UUID id,Team team){OfflinePlayer player=Bukkit.getOfflinePlayer(id);String playerName=name(id);String role=id.equals(team.leader())?"&#d9a441Leader":"&7Member";String status=gui.getString(player.isOnline()?"status-output.online":"status-output.offline",player.isOnline()?"&a&lONLINE":"&c&lOFFLINE");return head(player,gui.getString("members.member.name","%player%"),gui.getStringList("members.member.lore"),Map.of("player",playerName,"role",role,"online",status));}
  private ItemStack teamItem(Team team){OfflinePlayer leader=Bukkit.getOfflinePlayer(team.leader());return head(leader,gui.getString("teams.team.name","%team%"),gui.getStringList("teams.team.lore"),Map.of("team",team.name(),"leader",name(team.leader()),"members",String.valueOf(team.members().size()),"max_members",String.valueOf(plugin.getConfig().getInt("max-members",10)),"join_status",joinStatus(team)));}
  private String joinStatus(Team team){if(team.members().size()>=plugin.getConfig().getInt("max-members",10))return "&cThis team is full.";if(!team.joinRequestsEnabled())return "&cJoin requests are disabled.";return "&e→ Send a join request";}
  private ItemStack head(OfflinePlayer owner,String title,List<String> lore,Map<String,String> values){ItemStack stack=new ItemStack(Material.PLAYER_HEAD);SkullMeta meta=(SkullMeta)stack.getItemMeta();meta.setOwningPlayer(owner);meta.setDisplayName(Text.color(replace(title,values)));meta.setLore(lore.stream().map(line->Text.color(replace(line,values))).toList());stack.setItemMeta(meta);return stack;}
  private ItemStack item(Material material,String title,List<String> lore){ItemStack stack=new ItemStack(material);ItemMeta meta=stack.getItemMeta();meta.setDisplayName(Text.color(title));meta.setLore(lore.stream().map(Text::color).toList());stack.setItemMeta(meta);return stack;}
  private Material material(String path,Material fallback){Material result=Material.matchMaterial(gui.getString(path,fallback.name()));return result==null?fallback:result;}
  private int pages(int count){return Math.max(1,(int)Math.ceil(count/(double)CONTENT.length));}
  private String name(UUID id){OfflinePlayer p=Bukkit.getOfflinePlayer(id);return p.getName()==null?id.toString().substring(0,8):p.getName();}
  private String replace(String text,Map<String,String> vars){for(var entry:vars.entrySet())text=text.replace("%"+entry.getKey()+"%",entry.getValue());return text;}
  @EventHandler public void click(InventoryClickEvent event){if(!(event.getInventory().getHolder() instanceof Holder holder))return;event.setCancelled(true);if(!(event.getWhoClicked() instanceof Player player))return;int slot=event.getRawSlot();if(slot<0||slot>=event.getInventory().getSize())return;if(holder.view==View.TEAMS&&slot==gui.getInt("teams.create.slot")){player.closeInventory();plugin.command().beginCreate(player);return;}if(holder.view==View.TEAMS){Team team=teamAt(holder.page,slot);if(team!=null){plugin.command().requestJoin(player,team);return;}}if(holder.view==View.SETTINGS){toggleSetting(player,holder,slot);return;}if(slot==gui.getInt("shared.navigation.previous.slot")&&holder.page>0)open(player,holder,holder.page-1);else if(slot==gui.getInt("shared.navigation.next.slot"))open(player,holder,holder.page+1);}
  private void toggleSetting(Player player,Holder holder,int slot){Team team=find(holder.team);if(team==null||!team.leader().equals(player.getUniqueId())){player.closeInventory();return;}if(slot==gui.getInt("settings.requests.slot"))plugin.teams().setJoinRequests(team,!team.joinRequestsEnabled());else if(slot==gui.getInt("settings.team-chat.slot"))plugin.teams().setTeamChat(team,!team.teamChatEnabled());else return;Team updated=find(team.name());if(updated!=null){settings(player,updated);plugin.command().send(player,"settings-updated",Map.of());}}
  private Team teamAt(int page,int slot){for(int i=0;i<CONTENT.length;i++)if(CONTENT[i]==slot){List<Team> teams=plugin.teams().allTeams().stream().sorted(Comparator.comparing(Team::name,String.CASE_INSENSITIVE_ORDER)).toList();int index=page*CONTENT.length+i;return index<teams.size()?teams.get(index):null;}return null;}
  private Team find(String name){return plugin.teams().allTeams().stream().filter(team->team.name().equalsIgnoreCase(name)).findFirst().orElse(null);}
  private void open(Player player,Holder holder,int page){if(holder.view==View.TEAMS)openTeams(player,page);else{Team team=find(holder.team);if(team!=null)openMembers(player,team,page);}}
  private enum View{MEMBERS,TEAMS,SETTINGS}
  private record Holder(View view,String team,int page) implements InventoryHolder{@Override public Inventory getInventory(){return null;}}
}
