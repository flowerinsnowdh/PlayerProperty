package online.flowerinsnow.playerproperty.listener;

import online.flowerinsnow.playerproperty.PlayerPropertyPlugin;
import online.flowerinsnow.playerproperty.object.PlayerProperty;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {
    private final PlayerPropertyPlugin plugin;

    public JoinQuitListener(PlayerPropertyPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlayerPropertyManager().init(event.getPlayer()); // 玩家加入时，初始化
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerPropertyManager().dispose(event.getPlayer()); // 玩家退出时，销毁数据，以防内存泄露
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerProperty playerProperty = plugin.getPlayerPropertyManager().getPlayerProperty(event.getEntity());
        // 死亡后将玩家的数值
        playerProperty.setWater(100);
        playerProperty.setMental(100);
        plugin.getPlayerPropertyManager().updatePlayerScoreboard(event.getEntity(), playerProperty.getWater(), playerProperty.getMental());
    }
}
