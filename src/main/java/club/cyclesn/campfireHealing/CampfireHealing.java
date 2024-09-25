package club.cyclesn.campfireHealing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public final class CampfireHealing extends JavaPlugin implements Listener {
    private final Set<Player> playersInRange = new HashSet<>();

    @Override
    public void onEnable() {
        getLogger().info("CampfireHealing has been enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
        startHealingTask();
    }

    @Override
    public void onDisable() {
        getLogger().info("CampfireHealing has been disabled!");
        playersInRange.clear();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        // 检查半径 8 格内的所有方块
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                if (Math.abs(x) + Math.abs(z) > 8) continue; // 确保是圆形范围
                Location checkLocation = playerLocation.clone().add(x, 0, z);
                Block block = checkLocation.getBlock();
                // 检查玩家是否靠近点燃的篝火
                if ((block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE)) {
                    if (block.getBlockData() instanceof Campfire campfire) {
                        if (campfire.isLit()) {
                            playersInRange.add(player);
                            return; // 找到点燃的篝火后，直接返回
                        }
                    }
                }
            }
        }
        playersInRange.remove(player);
    }

    private void startHealingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : playersInRange) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0, true, false));
                }
            }
        }.runTaskTimer(this, 0, 40);
    }
}
