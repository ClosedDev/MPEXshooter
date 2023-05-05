package io.github.kuroka3.mpexshooter.weapon;

import io.github.kuroka3.mpexshooter.MPEXshooter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.github.kuroka3.mpexshooter.weapon.Shoot.launcharrow;

public class Kraber implements Listener {

    public static Map<Player, Integer> amokraber = new HashMap<>();
    private Set<Player> reloading = new HashSet<>();
    @EventHandler
    public void onPlayerShootKraber(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.SPYGLASS) {
                event.setCancelled(true);
                if (!MPEXshooter.noDamagePlayers.contains(p) && !reloading.contains(p)) {
                    shootkraber(p);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerShootKraberByFKey(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();

        if(i != null && i.getType() == Material.SPYGLASS  && !MPEXshooter.noDamagePlayers.contains(p)) {
            shootkraber(p);
        }
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER) {
            Player at = (Player) e.getDamager();
            ItemStack i = at.getInventory().getItemInMainHand();
            if (i != null && i.getType() == Material.SPYGLASS) {
                shootkraber(at);
            }
        }
    }

    @EventHandler
    public void onReloadKraber(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack i = e.getItemDrop().getItemStack();
        if(i.getType() == Material.SPYGLASS && !p.isSneaking()) {
            e.setCancelled(true);
            if (amokraber.get(p) < 4) {
                if (!reloading.contains(p)) {
                    reloading.add(p);
                    new BukkitRunnable() {
                        double timeLeft = 4.3;

                        @Override
                        public void run() {
                            ItemStack item = p.getInventory().getItemInMainHand();
                            if (item.getType() == Material.SPYGLASS) {
                                if (amokraber.get(p) > 0) {
                                    amokraber.put(p, 0);
                                    timeLeft = 3.2;
                                }
                                timeLeft -= 0.1;
                                if (timeLeft > 0) {
                                    p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: " + ChatColor.YELLOW + String.format("%.1f", timeLeft));
                                } else {
                                    amokraber.put(p, 4);
                                    reloading.remove(p);
                                    p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: " + ChatColor.GREEN + "0");
                                    cancel();
                                }
                            }
                        }
                    }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 2);
                }
            }
        }
    }

    private void shootkraber(Player p) {
        if (amokraber.get(p) == null) amokraber.put(p, 4);
        if (amokraber.get(p) > 0) {
            if (p.hasMetadata("spyglass_cooldown")) {
                double cooldown = p.getMetadata("spyglass_cooldown").get(0).asDouble();
                if (cooldown > 0) {
                    return;
                }
            }
            p.setMetadata("spyglass_cooldown", new FixedMetadataValue(MPEXshooter.getPlugin(MPEXshooter.class), 10.0));
            new BukkitRunnable() {
                double timeLeft = 2.2;

                @Override
                public void run() {
                    if (amokraber.get(p) < 1) timeLeft = 0.0;
                    ItemStack item = p.getInventory().getItemInMainHand();
                    timeLeft -= 0.1;
                    if (timeLeft > 0) {
                        if (item.getType() == Material.SPYGLASS) p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.RED + "\uc900\ube44\uc911" + ChatColor.GRAY + " :: " + ChatColor.YELLOW + String.format("%.1f", timeLeft));
                    } else {
                        p.removeMetadata("spyglass_cooldown", MPEXshooter.getPlugin(MPEXshooter.class));
                        p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.GREEN + "" + ChatColor.BOLD + "\uc0ac\uc6a9\u0020\uac00\ub2a5");
                        if (amokraber.get(p) < 1) p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.RED + "" + ChatColor.BOLD + "\uc7ac\uc7a5\uc804\u0020\ud544\uc694");
                        cancel();
                    }
                }
            }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 2);
            launcharrow(p, 1, "Kraber");
        } else {
            if (!reloading.contains(p)) {
                reloading.add(p);
                new BukkitRunnable() {
                    double timeLeft = 4.3;

                    @Override
                    public void run() {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item.getType() == Material.SPYGLASS) {
                            timeLeft -= 0.1;
                            if (timeLeft > 0) {
                                p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: "  + ChatColor.YELLOW + String.format("%.1f", timeLeft));
                            } else {
                                amokraber.put(p, 4);
                                reloading.remove(p);
                                p.sendActionBar(ChatColor.AQUA + "" + ChatColor.BOLD + "\ud06c\ub808\uc774\ubc84\u0020" + ChatColor.RED + "\uc7ac\uc7a5\uc804" + ChatColor.GRAY + " :: " + ChatColor.GREEN + "0");
                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 2);
            }
        }
    }
}
