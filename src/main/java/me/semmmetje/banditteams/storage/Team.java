package me.semmmetje.banditteams.storage;
import java.util.Set;
import java.util.UUID;
public record Team(String name, UUID leader, Set<UUID> members) {}
