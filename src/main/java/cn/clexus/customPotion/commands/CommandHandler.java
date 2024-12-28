package cn.clexus.customPotion.commands;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.EffectRegistry;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.managers.PotionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandHandler extends @NotNull Command implements CommandExecutor {

    public CommandHandler(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            List<String> completions = new ArrayList<>();

            // 根据命令参数数量判断
            if (args.length == 1) {
                // 提供子命令补全：modify 和 clear
                completions.add("modify");
                completions.add("clear");
            } else if (args.length == 2) {
                // 提供目标补全：玩家名或实体 UUID
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList());
            } else if (args.length == 3 && args[0].equalsIgnoreCase("modify")) {
                // 提供药水效果类型补全
                completions.addAll(EffectRegistry.getAllEffects().keySet());
            } else if (args.length == 4 && args[0].equalsIgnoreCase("modify")) {
                // 提供等级补全
                completions.add("1");
            } else if (args.length == 5 && args[0].equalsIgnoreCase("modify")) {
                // 提供持续时间补全（单位：tick）
                completions.add("200");
            } else if (args.length == 6 && args[0].equalsIgnoreCase("modify")) {
                // 提供叠加状态补全：true 和 false
                completions.add("replace");
                completions.add("add_all");
                completions.add("add_time");
                completions.add("add_amplifier");
            }else if (args.length == 7 && args[0].equalsIgnoreCase("modify")) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            // 根据输入过滤结果（部分匹配）
            return completions.stream()
                    .filter(entry -> entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .collect(Collectors.toList());
    }

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
        if (args.length < 5) {
            sender.sendMessage("Usage: /customeffect modify <UUID/玩家名> <类型> <等级> <时长> [叠加状态] [来源]");
        }

        // 解析参数
        String targetName = args[1];
        String effectTypeName = args[2];
        int level = Integer.parseInt(args[3]);
        int duration = Integer.parseInt(args[4]);
        StackingModes stackingMode = args.length > 5 ? StackingModes.valueOf(args[5].toUpperCase()) : StackingModes.REPLACE;
        String sourceName = args.length > 6 ? args[6] : null;

        // 查找目标实体
        LivingEntity targetEntity = getLivingEntityByName(targetName);
        if (targetEntity == null) {
            sender.sendMessage("Player or entity not found: " + targetName);
        }

        // 查找效果类型
        CustomEffectType effectType = EffectRegistry.getById(effectTypeName);
        if (effectType == null) {
            sender.sendMessage("Unknown effect type: " + effectTypeName);
        }

        // 创建并添加效果
        CustomEffect effect = new CustomEffect(effectType,duration,level,null);
        if (sourceName != null) {
            LivingEntity source = getLivingEntityByName(sourceName);
            effect.setSource(source);
        }

        PotionManager.addEffect(targetEntity, effect, stackingMode);
        sender.sendMessage("Effect applied to " + targetName + ": " + effectTypeName);
        return true;
    }

    // 处理 clear 命令
    private boolean handleClearCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /customeffect clear <UUID/玩家名> [类型]");
            return true;
        }

        // 解析参数
        String targetName = args[1];
        LivingEntity targetEntity = getLivingEntityByName(targetName);
        if (targetEntity == null) {
            sender.sendMessage("Player or entity not found: " + targetName);
            return true;
        }

        if (args.length == 3) {
            // 清除特定类型的效果
            String effectTypeName = args[2];
            CustomEffectType effectType = EffectRegistry.getById(effectTypeName);
            if (effectType == null) {
                sender.sendMessage("Unknown effect type: " + effectTypeName);
                return true;
            }
            PotionManager.clearEffects(targetEntity, effectType);
            sender.sendMessage("Cleared " + effectTypeName + " effect from " + targetName);
        } else {
            // 清除所有效果
            PotionManager.clearEffects(targetEntity);
            sender.sendMessage("Cleared all effects from " + targetName);
        }

        return true;
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

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        onCommand(sender,this,commandLabel,args);
        return true;
    }
}
