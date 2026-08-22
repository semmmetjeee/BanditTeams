package me.semmmetje.banditteams.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Text {
  private static final Pattern HEX=Pattern.compile("&#([A-Fa-f0-9]{6})"); private Text(){}
  public static String color(String text){Matcher m=HEX.matcher(text==null?"":text);StringBuffer out=new StringBuffer();while(m.find())m.appendReplacement(out,ChatColor.of("#"+m.group(1)).toString());m.appendTail(out);return ChatColor.translateAlternateColorCodes('&',out.toString());}
  public static Component component(String text){return MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(color(text))));}
}
