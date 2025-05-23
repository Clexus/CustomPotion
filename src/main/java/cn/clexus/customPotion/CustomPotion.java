package cn.clexus.customPotion;

import cn.clexus.customPotion.commands.CommandCompleter;
import cn.clexus.customPotion.commands.CommandHandler;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.events.EventsListener;
import cn.clexus.customPotion.events.PacketsListener;
import cn.clexus.customPotion.events.PlayerCollideEvent;
import cn.clexus.customPotion.hooks.mythicmobs.MythicMobsEvents;
import cn.clexus.customPotion.hooks.mythicmobs.MythicMobsSupport;
import cn.clexus.customPotion.hooks.placeholderapi.PlaceholderAPISupport;
import cn.clexus.customPotion.managers.DatabaseManager;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EffectLoader;
import cn.clexus.customPotion.utils.I18n;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class CustomPotion extends JavaPlugin {
    private final Map<Player, Set<Entity>> playerCollisions = new HashMap<>();
    public static DatabaseManager databaseManager;
    public static CustomPotion plugin;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        plugin = this;
    }

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 25175);
        PacketEvents.getAPI().init();
        saveDefaultConfig();
        saveMessageExampleFile();

        File messageFile = new File(getDataFolder(), "message.yml");
        FileConfiguration message = YamlConfiguration.loadConfiguration(messageFile);
        I18n.initialize(message);
        MythicMobsSupport.init(this);
        PlaceholderAPISupport.init(this);
        EffectLoader.registerAllEffects();
        PotionManager.startEffectProcessor(this);
        CustomEffectType.initializeStaticFields();
        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketsListener(), PacketListenerPriority.NORMAL);
        if (MythicMobsSupport.hasSupport()) {
            Bukkit.getPluginManager().registerEvents(new MythicMobsEvents(), this);
        }
        this.getCommand("customeffect").setExecutor(new CommandHandler());
        this.getCommand("customeffect").setTabCompleter(new CommandCompleter());
        new CollisionDetectionTask().runTaskTimer(this, 0L, 10L);
        try {
            databaseManager = new DatabaseManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveMessageExampleFile() {
        File messageFile = new File(getDataFolder(), "message.yml");
        if (!messageFile.exists()) {
            try (InputStream resourceStream = getResource("message.yml")) {
                if (resourceStream != null) {
                    Files.copy(resourceStream, messageFile.toPath());
                }
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "An error occurred when copying files:", e);
            }
        }
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        PotionManager.shutdown();
        databaseManager.shutdown();
    }

    private class CollisionDetectionTask extends BukkitRunnable {
        private static final double RADIUS = 1.8;

        @Override
        public void run() {
            playerCollisions.keySet().removeIf(player -> !player.isOnline());

            for (Player player : Bukkit.getOnlinePlayers()) {
                Set<Entity> currentCollisions = new HashSet<>();

                for (Entity e : player.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                    if (!(e instanceof LivingEntity entity)) {
                        continue;
                    }

                    BoundingBox playerBox = player.getBoundingBox();
                    BoundingBox entityBox = entity.getBoundingBox();
                    if (playerBox.overlaps(entityBox)) {
                        currentCollisions.add(entity);

                        Set<Entity> playerCollisionSet = playerCollisions.computeIfAbsent(player, k -> new HashSet<>());

                        if (!playerCollisionSet.contains(entity)) {
                            Bukkit.getPluginManager().callEvent(new PlayerCollideEvent(player, entity));
                            playerCollisionSet.add(entity);
                        }
                    }
                }

                Set<Entity> existingCollisions = playerCollisions.get(player);
                if (existingCollisions != null) {
                    existingCollisions.retainAll(currentCollisions);
                }
            }
        }
    }
}
