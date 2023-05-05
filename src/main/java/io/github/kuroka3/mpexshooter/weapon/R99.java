package io.github.kuroka3.mpexshooter.weapon;

import io.github.kuroka3.mpexshooter.MPEXshooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.kuroka3.mpexshooter.weapon.Shoot.launcharrow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class R99 implements Listener {
    private Set<Player> noshoot = new HashSet<>();
    public static Map<Player, Integer> amor99 = new HashMap<>();
    private Set<Player> reloading = new HashSet<>();
    @EventHandler
    public void onPlayerShootR99(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        EquipmentSlot eq = e.getHand();

        if(i != null && i.getType() == Material.NETHERITE_SWORD && e.getAction().name().contains("RIGHT") && eq.equals(EquipmentSlot.HAND) && !noshoot.contains(p)) {
            if(amor99.get(p) == null) amor99.put(p, 30);
            if (amor99.get(p) > 4) {
                noshoot.add(p);
                launcharrow(p, 5, "R99");
                Bukkit.getScheduler().runTaskLater(MPEXshooter.getPlugin(MPEXshooter.class), () -> noshoot.remove(p), 4);
            } else {
                if (!reloading.contains(p)) {
                    reloading.add(p);
                    new BukkitRunnable() {
                        double timeLeft = 2.5;

                        @Override
                        public void run() {
                            ItemStack item = p.getInventory().getItemInMainHand();
                            if (item.getType() == Material.NETHERITE_SWORD) {
                                timeLeft -= 0.1;
                                if (timeLeft > 0) {
                                    p.sendActionBar(ChatColor.GOLD + "" + ChatColor.BOLD + "R99 " + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: "  + ChatColor.YELLOW + String.format("%.1f", timeLeft));
                                } else {
                                    amor99.put(p, 30);
                                    reloading.remove(p);
                                    p.sendActionBar(ChatColor.GOLD + "" + ChatColor.BOLD + "R99 " + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: " + ChatColor.GREEN + "0");
                                    cancel();
                                }
                            }
                        }
                    }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 2);
                }
            }
        }
    }

    @EventHandler
    public void onReloadR99(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack i = e.getItemDrop().getItemStack();
        if(i.getType() == Material.NETHERITE_SWORD && !p.isSneaking()) {
            e.setCancelled(true);
            if (amor99.get(p) < 30) {
                if (!reloading.contains(p)) {
                    reloading.add(p);
                    new BukkitRunnable() {
                        double timeLeft = 2.5;

                        @Override
                        public void run() {
                            ItemStack item = p.getInventory().getItemInMainHand();
                            if (item.getType() == Material.NETHERITE_SWORD) {
                                if (amor99.get(p) > 0) {
                                    timeLeft = 1.8;
                                }
                                amor99.put(p, 0);
                                timeLeft -= 0.1;
                                if (timeLeft > 0) {
                                    p.sendActionBar(ChatColor.GOLD + "" + ChatColor.BOLD + "R99 " + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: " + ChatColor.YELLOW + String.format("%.1f", timeLeft));
                                } else {
                                    amor99.put(p, 30);
                                    reloading.remove(p);
                                    p.sendActionBar(ChatColor.GOLD + "" + ChatColor.BOLD + "R99 " + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: " + ChatColor.GREEN + "0");
                                    cancel();
                                }
                            }
                        }
                    }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 2);
                }
            }
        }
    }
}
