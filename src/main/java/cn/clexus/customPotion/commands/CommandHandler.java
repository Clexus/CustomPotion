package cn.clexus.customPotion.commands;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.EffectRegistry;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.managers.PotionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandHandler implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /customeffect <modify|clear> ...");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        return switch (subCommand) {
            case "modify" -> handleModifyCommand(sender, args);
            case "clear" -> handleClearCommand(sender, args);
            default -> {
                sender.sendMessage("Unknown subcommand: " + subCommand);
                yield false;
            }
        };
    }

    // 处理 modify 命令
    private boolean handleModifyCommand(CommandSender sender, String[] args) {
        // 检查参数长度，至少需要 3 个参数（目标、效果类型）
        if (args.length < 3) {
            if (shouldSendFeedback(sender)) {
                sender.sendMessage("Usage: /customeffect modify <UUID/玩家名> <类型> [时长] [等级] [叠加状态/来源]");
                sender.sendMessage("Usage: /customeffect modify <UUID/玩家名> <类型> [时长] [等级] [叠加状态] [来源]");
            }
            return true;
        }

        try {
            // 解析参数
            String targetName = args[1];
            String effectTypeName = args[2];

            // 默认值处理
            int duration = args.length > 3 ? Integer.parseInt(args[3]) : 200; // 默认等级为 1
            int level = args.length > 4 ? Integer.parseInt(args[4]) : 1; // 默认持续时间为 200

            // 验证等级和持续时间必须大于 0
            if (level <= 0) {
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Level must be greater than 0.");
                }
                return true;
            }

            if (duration <= 0) {
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Duration must be greater than 0.");
                }
                return true;
            }

            StackingModes stackingMode = StackingModes.NORMAL; // 默认叠加模式
            String sourceName = null; // 默认来源实体名称

            if (args.length > 5) {
                try {
                    // 尝试解析第六个参数为叠加模式
                    stackingMode = StackingModes.valueOf(args[5].toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 如果不是有效的叠加模式，尝试将其解析为实体名称
                    sourceName = args[5];
                }
            }

            if (args.length > 6) {
                // 如果存在第七个参数，则将其视为来源实体名称
                sourceName = args[6];
            }

            // 查找目标实体
            LivingEntity targetEntity = getLivingEntityByName(targetName);
            if (targetEntity == null) {
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Player or entity not found: " + targetName);
                }
                return true;
            }

            // 查找效果类型
            CustomEffectType effectType = EffectRegistry.getById(effectTypeName);
            if (effectType == null) {
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Unknown effect type: " + effectTypeName);
                }
                return true;
            }

            // 创建并添加效果
            CustomEffect effect = new CustomEffect(effectType, duration, level, null);
            if (sourceName != null) {
                LivingEntity source = getLivingEntityByName(sourceName);
                effect.setSource(source);
            }

            PotionManager.addEffect(targetEntity, effect, stackingMode);
            if (shouldSendFeedback(sender)) {
                sender.sendMessage("Effect applied to " + targetName + ": " + effectTypeName);
            }
        } catch (NumberFormatException e) {
            if (shouldSendFeedback(sender)) {
                sender.sendMessage("Level and duration must be valid integers.");
            }
        } catch (IllegalArgumentException e) {
            if (shouldSendFeedback(sender)) {
                sender.sendMessage("Invalid stacking mode. Available options: NORMAL, REPLACE, ADD_ALL, ADD_TIME, ADD_AMPLIFIER.");
            }
        }

        return true;
    }



    private boolean handleClearCommand(CommandSender sender, String[] args) {
        // 检查参数长度
        if (args.length < 2) {
            if (shouldSendFeedback(sender)) {
                sender.sendMessage("Usage: /customeffect clear <UUID/玩家名> [类型]");
            }
            return true;
        }

        try {
            // 解析参数
            String targetName = args[1];
            LivingEntity targetEntity = getLivingEntityByName(targetName);
            if (targetEntity == null) {
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Player or entity not found: " + targetName);
                }
                return true;
            }

            if (args.length == 3) {
                // 清除特定类型的效果
                String effectTypeName = args[2];
                CustomEffectType effectType = EffectRegistry.getById(effectTypeName);
                if (effectType == null) {
                    if (shouldSendFeedback(sender)) {
                        sender.sendMessage("Unknown effect type: " + effectTypeName);
                    }
                    return true;
                }
                PotionManager.clearEffects(targetEntity, effectType);
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Cleared " + effectTypeName + " effect from " + targetName);
                }
            } else {
                // 清除所有效果
                PotionManager.clearEffects(targetEntity);
                if (shouldSendFeedback(sender)) {
                    sender.sendMessage("Cleared all effects from " + targetName);
                }
            }
        } catch (IllegalArgumentException e) {
            if (shouldSendFeedback(sender)) {
                sender.sendMessage("Invalid arguments provided.");
            }
        }

        return true;
    }

    // 检查是否应该发送反馈消息
    private boolean shouldSendFeedback(CommandSender sender) {
        // 如果发送者不是控制台或玩家，默认返回 false
        if (!(sender instanceof Player || sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            return false;
        }

        // 检查 sendCommandFeedback 游戏规则
        return Boolean.TRUE.equals(sender.getServer().getWorlds().getFirst().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK));
    }



    // 根据名字获取 LivingEntity 实体
    private LivingEntity getLivingEntityByName(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return player;
        }

        // 如果是 UUID 格式，则查找实体
        try {
            UUID uuid = UUID.fromString(name);
            return (LivingEntity) Bukkit.getEntity(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
