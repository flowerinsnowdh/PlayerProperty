package online.flowerinsnow.playerproperty.manager;

import online.flowerinsnow.playerproperty.PlayerPropertyPlugin;
import online.flowerinsnow.playerproperty.object.PlayerProperty;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 玩家属性管理器
 */
public class PlayerPropertyManager {
    /**
     * 储存玩家属性
     */
    private final HashMap<Player, PlayerProperty> playerProperties = new HashMap<>();
    /**
     * 玩家掉属性的任务
     */
    private final HashMap<Player, BukkitTask> playerTasks = new HashMap<>();
    /**
     * 储存玩家的计分板
     */
    private final HashMap<Player, Scoreboard> playerScoreboards = new HashMap<>();

    private final PlayerPropertyPlugin plugin;

    public PlayerPropertyManager(PlayerPropertyPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 从配置文件读取玩家的属性，用于永久存储玩家的状态
     *
     * @param config 配置文件
     * @return 从配置文件读取玩家的属性
     */
    public PlayerProperty getPlayerPropertyFromConfiguration(ConfigurationSection config) {
        int water = config.getInt("water");
        int mental = config.getInt("mental");
        return new PlayerProperty(water, mental);
    }

    /**
     * 将玩家属性写入配置文件，用于永久存储玩家的状态
     *
     * @param playerProperty 玩家属性
     * @param configFile 配置文件
     */
    public void savePlayerPropertyToConfiguration(PlayerProperty playerProperty, File configFile) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("water", playerProperty.getWater());
        config.set("mental", playerProperty.getMental());
        if (!configFile.exists()) { // 文件不存在，创建文件
            //noinspection ResultOfMethodCallIgnored
            configFile.getParentFile().mkdirs();
            try {
                //noinspection ResultOfMethodCallIgnored
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe(e.toString());
        }
    }

    /**
     * 为玩家初始化，用于玩家加入时
     *
     * @param player 玩家
     */
    public void init(Player player) {
        // 加载玩家属性
        getPlayerProperty(player);
        // 为玩家开启属性消耗任务
        startCostTask(player);
        // 为玩家加载记分板
        getPlayerScoreboard(player);
    }

    /**
     * 为玩家销毁属性，以防内存泄露，用于玩家退出时
     *
     * @param player 玩家
     */
    public void dispose(Player player) {
        // 保存并清除玩家属性
        savePlayerPropertyToConfiguration(playerProperties.remove(player), new File(plugin.getDataFolder(), "playerproperties/" + player.getUniqueId() + ".yml"));
        // 结束玩家属性消耗任务
        playerTasks.remove(player).cancel();
        playerScoreboards.remove(player);
    }

    /**
     * 获取玩家的属性
     *
     * @param player 玩家
     * @return 玩家的属性
     */
    public PlayerProperty getPlayerProperty(Player player) {
        // 先从内存中读取玩家的属性
        PlayerProperty playerProperty =  playerProperties.get(player);
        if (playerProperty == null) {
            // 内存中没有，从配置文件读取
            File configFile = new File(plugin.getDataFolder(), "playerproperties/" + player.getUniqueId() + ".yml");
            if (configFile.exists()) {
                playerProperty = getPlayerPropertyFromConfiguration(YamlConfiguration.loadConfiguration(configFile));
            } else { // 配置文件中也没有，新建并保存
                playerProperty = new PlayerProperty(100, 100);
                savePlayerPropertyToConfiguration(playerProperty, configFile);
            }
            playerProperties.put(player, playerProperty);
        }
        return playerProperty;
    }

    /**
     * 为玩家开启属性消耗任务
     *
     * @param player 玩家
     */
    @SuppressWarnings("UnusedReturnValue")
    public BukkitTask startCostTask(Player player) {
        BukkitTask task = playerTasks.get(player);
        if (task != null) { // 已经有一个正在运行的任务了
            task.cancel(); // 将任务取消
        }
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            PlayerProperty playerProperty = getPlayerProperty(player); // 获取玩家属性
            Location location = player.getLocation();
            Biome biome = player.getWorld().getBiome(location); // 获取玩家所在生物群系
            if (biome == Biome.DESERT) { // 如果是沙漠，每次属性-2
                playerProperty.setWater(playerProperty.getWater() - 2);
                playerProperty.setMental(playerProperty.getMental() - 2);
            } else { // 其他生物群系，每次属性-1
                playerProperty.setWater(playerProperty.getWater() - 1);
                playerProperty.setMental(playerProperty.getMental() - 1);
            }
            updatePlayerScoreboard(player, playerProperty.getWater(), playerProperty.getMental());
        }, 0L, 200L); // 新建任务，每10秒扣除玩家的属性
        playerTasks.put(player, task);
        return task;
    }

    /**
     * 获取玩家的记分板
     *
     * @param player 玩家
     * @return 玩家的记分板
     */
    public Scoreboard getPlayerScoreboard(Player player) {
        Scoreboard scoreboard = playerScoreboards.get(player);
        if (scoreboard == null) {
            // 记分板不存在，创建记分板
            //noinspection DataFlowIssue
            scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("property", Criteria.DUMMY, "属性");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            player.setScoreboard(scoreboard);
            playerScoreboards.put(player, scoreboard);
        }
        return scoreboard;
    }


    public void updatePlayerScoreboard(Player player, int water, int mental) {
        Scoreboard scoreboard = getPlayerScoreboard(player);
        Objective objective = scoreboard.getObjective("property");
        //noinspection DataFlowIssue
        objective.getScore("水分：").setScore(water);
        objective.getScore("神智：").setScore(mental);
    }
}
