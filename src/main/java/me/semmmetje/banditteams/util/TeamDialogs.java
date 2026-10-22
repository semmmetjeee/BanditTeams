package me.semmmetje.banditteams.util;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.semmmetje.banditteams.BanditTeams;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public final class TeamDialogs {
    private final BanditTeams plugin;
    private YamlConfiguration config;

    public TeamDialogs(BanditTeams plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "dialogs.yml");
        if (!file.exists()) {
            plugin.saveResource("dialogs.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void openCreate(Player player) {
        open(player, "create", "team_name", 10, plugin.command()::createFromDialog);
    }

    public void openInvite(Player player) {
        open(player, "invite", "player_name", 16, plugin.command()::inviteFromDialog);
    }

    private void open(Player player, String section, String defaultKey, int defaultMaxLength,
                      java.util.function.BiConsumer<Player, String> callback) {
        String key = config.getString(section + ".input.key", defaultKey);
        String title = config.getString(section + ".title", section.equals("create") ? "Create a team" : "Invite a player");
        String label = config.getString(section + ".input.label", section.equals("create") ? "Team name" : "Player username");
        String initial = config.getString(section + ".input.initial", "");
        int maxLength = Math.max(1, config.getInt(section + ".input.max-length", defaultMaxLength));
        int width = Math.max(1, config.getInt(section + ".input.width", 300));
        String confirm = config.getString(section + ".buttons.confirm", "Confirm");
        String cancel = config.getString(section + ".buttons.cancel", "Cancel");

        Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(Text.component(title))
                .inputs(List.of(DialogInput.text(key, Text.component(label))
                    .initial(initial)
                    .maxLength(maxLength)
                    .width(width)
                    .labelVisible(true)
                    .build()))
                .build())
            .type(DialogType.confirmation(
                ActionButton.builder(Text.component(confirm))
                    .action(DialogAction.customClick((view, audience) -> {
                        if (!(audience instanceof Player target)) {
                            return;
                        }
                        String value = view.getText(key);
                        if (value != null) {
                            callback.accept(target, value);
                        }
                    }, ClickCallback.Options.builder().uses(1).build()))
                    .build(),
                ActionButton.builder(Text.component(cancel)).build()
            ))
        );
        player.showDialog(dialog);
    }
}
