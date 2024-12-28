package cn.clexus.customPotion.commands;

import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.EffectRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
                    .collect(Collectors.toList()));
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
}
