package online.flowerinsnow.playerproperty;

import online.flowerinsnow.playerproperty.listener.JoinQuitListener;
import online.flowerinsnow.playerproperty.manager.PlayerPropertyManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPropertyPlugin extends JavaPlugin {
    private static PlayerPropertyPlugin instance;
    private PlayerPropertyManager playerPropertyManager;
    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        playerPropertyManager = new PlayerPropertyManager(this);

        // 监听玩家加入和退出，以完成初始化或销毁
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this); // 取消本插件的所有计划任务
    }

    public PlayerPropertyManager getPlayerPropertyManager() {
        return playerPropertyManager;
    }

    public static PlayerPropertyPlugin getInstance() {
        return instance;
    }
}