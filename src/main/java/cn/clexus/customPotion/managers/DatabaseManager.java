package cn.clexus.customPotion.managers;

import cn.clexus.customPotion.CustomPotion;
import cn.clexus.customPotion.effects.CustomEffect;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class DatabaseManager {
    private final Connection connection;
    private final ExecutorService dbExecutor;

    public DatabaseManager() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + CustomPotion.plugin.getDataFolder().getAbsolutePath() + "/database.db");
        this.dbExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Database-Thread");
            t.setDaemon(true);
            return t;
        });

        initialize();
    }

    /**
     * 初始化数据库表结构
     */
    private void initialize() {
        executeUpdate("CREATE TABLE IF NOT EXISTS player_effects (" +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "effect_data TEXT NOT NULL" +
                ")");
    }

    /**
     * 执行SQL更新操作
     */
    private void executeUpdate(String sql, Object... params) {
        dbExecutor.execute(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.executeUpdate();
            } catch (SQLException e) {
                CustomPotion.plugin.getLogger().log(Level.SEVERE, "数据库执行错误: " + sql, e);
            }
        });
    }

    /**
     * 保存玩家的所有药水效果
     */
    public void savePlayerEffects(UUID playerUuid, List<CustomEffect> effects) {
        // 先清除现有数据
        clearPlayerEffects(playerUuid);

        if (effects == null || effects.isEmpty()) {
            return;
        }

        // 使用CustomEffect的toString()方法序列化效果
        for (CustomEffect effect : effects) {
            String serializedEffect = effect.toString();
            executeUpdate("INSERT INTO player_effects (player_uuid, effect_data) VALUES (?, ?)",
                    playerUuid.toString(), serializedEffect);
        }
    }

    /**
     * 清除玩家的所有效果数据
     */
    public void clearPlayerEffects(UUID playerUuid) {
        executeUpdate("DELETE FROM player_effects WHERE player_uuid = ?", playerUuid.toString());
    }

    /**
     * 加载玩家的所有药水效果
     *
     * @return 包含效果列表的CompletableFuture
     */
    public CompletableFuture<List<CustomEffect>> loadPlayerEffects(UUID playerUuid) {
        CompletableFuture<List<CustomEffect>> future = new CompletableFuture<>();

        dbExecutor.execute(() -> {
            List<CustomEffect> effects = new ArrayList<>();
            String sql = "SELECT effect_data FROM player_effects WHERE player_uuid = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerUuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String effectData = rs.getString("effect_data");
                        try {
                            CustomEffect effect = CustomEffect.fromString(effectData);
                            effects.add(effect);
                        } catch (Exception e) {
                            CustomPotion.plugin.getLogger().log(Level.WARNING,
                                    "解析效果数据失败: " + effectData, e);
                        }
                    }
                }

                future.complete(effects);
            } catch (SQLException e) {
                CustomPotion.plugin.getLogger().log(Level.SEVERE, "加载玩家效果错误", e);
                future.complete(Collections.emptyList());
            }
        });

        return future;
    }

    /**
     * 关闭数据库连接和执行器
     */
    public void shutdown() {
        dbExecutor.shutdown();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            CustomPotion.plugin.getLogger().log(Level.WARNING, "关闭数据库连接失败", e);
        }
    }
}