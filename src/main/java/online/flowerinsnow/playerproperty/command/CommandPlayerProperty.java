package online.flowerinsnow.playerproperty.command;

import online.flowerinsnow.playerproperty.PlayerPropertyPlugin;
import online.flowerinsnow.playerproperty.manager.PlayerPropertyManager;
import online.flowerinsnow.playerproperty.object.PlayerProperty;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class CommandPlayerProperty implements TabExecutor {
    private final PlayerPropertyPlugin plugin;

    public CommandPlayerProperty(PlayerPropertyPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        /*
        /playerproperty set <player> water <value>
         */
        if (args.length == 4) {
            if ("set".equalsIgnoreCase(args[0]) && "water".equalsIgnoreCase(args[2])) {
                String paramPlayername = args[1];
                String paramValue = args[3];
                Player player = Bukkit.getPlayerExact(paramPlayername);
                if (player == null) {
                    sender.sendMessage("目标玩家不在线");
                    return false;
                }
                int value;
                try {
                    value = Integer.parseInt(paramValue);
                } catch (NumberFormatException e) {
                    sender.sendMessage("您输入的数字不正确");
                    return false;
                }
                PlayerPropertyManager playerPropertyManager = plugin.getPlayerPropertyManager();
                PlayerProperty playerProperty = playerPropertyManager.getPlayerProperty(player);
                playerProperty.setWater(value); // 将玩家的水分设置为命令中的值
                playerPropertyManager.updatePlayerScoreboard(player, playerProperty.getWater(), playerProperty.getMental()); // 更新玩家的记分板
                return true;
            }
        }
        sender.sendMessage("用法：/playerproperty set <player> water <value>");
        return false;
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("set"));
            subCommands.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return subCommands;
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().collect(
                    ArrayList::new,
                    (list, p) -> {
                        if (p.getName().toLowerCase().startsWith(args[1])) {
                            list.add(p.getName());
                        }
                    },
                    ArrayList::addAll
            );
        } else if (args.length == 3) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("water"));
            subCommands.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return subCommands;
        } else {
            return new ArrayList<>();
        }
    }
}
