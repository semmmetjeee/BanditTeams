package me.semmmetje.banditteams.storage;
import org.bukkit.Location;
import java.util.Set;
import java.util.UUID;
public record Team(String name, UUID leader, Set<UUID> members, boolean joinRequestsEnabled, boolean teamChatEnabled, Location home) {}
