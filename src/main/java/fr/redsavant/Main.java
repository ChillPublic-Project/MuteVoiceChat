package fr.redsavant;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import litebans.api.Database;
import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static fr.redsavant.utils.Colorize.colorize;

public class Main extends JavaPlugin implements Listener {

    private final Set<UUID> mutedPlayers = ConcurrentHashMap.newKeySet();

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        getServer().getPluginManager().registerEvents(this, this);

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            service.registerPlugin(new VoicePlugin());
        } else {
            getLogger().warning("BukkitVoicechatService not found, is SimpleVoiceChat installed ?");
        }

        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                if ("mute".equals(entry.getType()) && entry.isActive()) {
                    UUID uuid = UUID.fromString(entry.getUuid());
                    mutedPlayers.add(uuid);
                }
            }

            @Override
            public void entryRemoved(Entry entry) {
                if ("mute".equals(entry.getType())) {
                    UUID uuid = UUID.fromString(entry.getUuid());
                    mutedPlayers.remove(uuid);
                }
            }
        });

        getLogger().info("Voice mute enabled");
    }

    public static Main getInstance() {
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                boolean muted = Database.get().isPlayerMuted(player.getUniqueId(), null);

                if (muted) {
                    mutedPlayers.add(player.getUniqueId());

                    if (!getConfig().getBoolean("messages.enabled")) return;

                    String message = getConfig().getString("messages.join-message", "<red>Vous etes mute sur ce serveur</red>");

                    Bukkit.getScheduler().runTask(this, () ->
                            player.sendMessage(colorize(message))
                    );
                }
            } catch (Exception e) {
                getLogger().warning("Failed to check mute status for " + player.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public boolean isMuted(UUID uuid) {
        return mutedPlayers.contains(uuid);
    }

    public void removeMute(UUID uuid) {
        mutedPlayers.remove(uuid);
    }
}