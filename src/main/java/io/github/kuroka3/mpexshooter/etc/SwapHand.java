package io.github.kuroka3.mpexshooter.etc;

import io.github.kuroka3.mpexshooter.MPEXshooter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static io.github.kuroka3.mpexshooter.weapon.Shoot.launcharrow;

public class SwapHand implements Listener {
    @EventHandler
    public void onWallJump(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        if(p.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() == Material.AIR && (i.getType() == Material.AIR || i.getType() == Material.NETHERITE_SWORD)) {
            if(!p.isSneaking()) {
                if (p.isSprinting()) {
                    if (p.getLocation().subtract(1.0, 0.0, 0.0).getBlock().getType() != Material.AIR) {
                        Vector unitVector = new Vector(0.5, 0.5, p.getLocation().getDirection().getZ() * 0.5);
                        p.setVelocity(unitVector);
                    } else if (p.getLocation().subtract(-1.0, 0.0, 0.0).getBlock().getType() != Material.AIR) {
                        Vector unitVector = new Vector(-0.5, 0.5, p.getLocation().getDirection().getZ() * 0.5);
                        p.setVelocity(unitVector);
                    } else if (p.getLocation().subtract(0.0, 0.0, 1.0).getBlock().getType() != Material.AIR) {
                        Vector unitVector = new Vector(p.getLocation().getDirection().getX() * 0.5, 0.5, 0.5);
                        p.setVelocity(unitVector);
                    } else if (p.getLocation().subtract(0.0, 0.0, -1.0).getBlock().getType() != Material.AIR) {
                        Vector unitVector = new Vector(p.getLocation().getDirection().getX() * 0.5, 0.5, -0.5);
                        p.setVelocity(unitVector);
                    }
                }
            } else {
                if (p.getLocation().subtract(1.0, 0.0, 0.0).getBlock().getType() != Material.AIR ||
                        p.getLocation().subtract(-1.0, 0.0, 0.0).getBlock().getType() != Material.AIR ||
                        p.getLocation().subtract(0.0, 0.0, 1.0).getBlock().getType() != Material.AIR ||
                        p.getLocation().subtract(0.0, 0.0, -1.0).getBlock().getType() != Material.AIR) {
                    Vector unitVector = new Vector(0.0, 0.6, 0.0);
                    p.setVelocity(unitVector);
                }
            }
        }
        if(i.getType() == Material.NETHER_STAR) {
            if (p.hasMetadata("heal_cooldown")) {
                double cooldown = p.getMetadata("heal_cooldown").get(0).asDouble();
                if (cooldown > 0) {
                    return;
                }
            }
            p.setMetadata("heal_cooldown", new FixedMetadataValue(MPEXshooter.getPlugin(MPEXshooter.class), 10.0));
            new BukkitRunnable() {
                double timeLeft = 30.0;

                @Override
                public void run() {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    timeLeft -= 0.1;
                    if (timeLeft > 0) {
                        if (item.getType() == Material.NETHER_STAR) p.sendActionBar(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "\ud790 " + ChatColor.RED + "\uc900\ube44\uc911" + ChatColor.GRAY + " :: " + ChatColor.YELLOW + String.format("%.1f", timeLeft));
                    } else {
                        p.removeMetadata("heal_cooldown", MPEXshooter.getPlugin(MPEXshooter.class));
                        p.sendActionBar(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "\ud790 " + ChatColor.GREEN + "" + ChatColor.BOLD + "\uc0ac\uc6a9\u0020\uac00\ub2a5");
                        cancel();
                    }
                }
            }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 2);
            PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 100, 2);
            p.addPotionEffect(effect, true);
        }
    }
}
