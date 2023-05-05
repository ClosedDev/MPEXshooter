package io.github.kuroka3.mpexshooter;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.kuroka3.mpexshooter.etc.ShowAmo;
import io.github.kuroka3.mpexshooter.etc.SwapHand;
import io.github.kuroka3.mpexshooter.weapon.Kraber;
import io.github.kuroka3.mpexshooter.weapon.R99;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public final class MPEXshooter extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println("MPEXshooter plugin is enabled");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new SwapHand(), this);
        getServer().getPluginManager().registerEvents(new Kraber(), this);
        getServer().getPluginManager().registerEvents(new R99(), this);
        ShowAmo task = new ShowAmo();
        task.runTaskTimer(this, 0, 1);

    }

    public static final Set<Player> noDamagePlayers = new HashSet<>();

    @EventHandler
    public void onDamageByArrow(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.ARROW) {
            Projectile pr = (Projectile) e.getDamager();
            e.setCancelled(true);
            Player p = (Player) pr.getShooter();
            if (pr.hasMetadata("R99")) e.setDamage(0.6667);
            else if (pr.hasMetadata("Kraber")) e.setDamage(20);
            LivingEntity en = (LivingEntity) e.getEntity();
            if (e.getEntity().getType() == EntityType.PLAYER) {
                Player vic = (Player) e.getEntity();
                if (!noDamagePlayers.contains(vic)) vic.damage(e.getDamage());
            } else {
                en.damage(e.getDamage());
            }
            en.setNoDamageTicks(0);
            p.sendActionBar(ChatColor.RED + String.valueOf(e.getDamage()));
            p.setExp((float) (en.getHealth() / en.getMaxHealth()));
            p.setLevel((int) en.getHealth());
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
        } else if(e.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getDamager();
            ItemStack i = p.getInventory().getItemInMainHand();
            if(i.getType() == Material.NETHERITE_SWORD) e.setDamage(3);
            p.sendActionBar(ChatColor.RED + String.valueOf(e.getDamage()));
            LivingEntity en = (LivingEntity) e.getEntity();
            double ehalth = en.getHealth() - e.getDamage();
            if(ehalth<0) ehalth = 0;
            p.setExp((float) (ehalth / en.getMaxHealth()));
            p.setLevel((int) Math.round(ehalth));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Projectile pr = e.getEntity();
        if (pr.getType().equals(EntityType.ARROW)) {
            if (!pr.hasMetadata("R99") && !pr.hasMetadata("Kraber")) return;
            if (e.getHitEntity() == null && e.getHitBlock().getType() != Material.BARRIER) {
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    pr.remove();
                }, 40L);
            } else {
                pr.remove();
            }
        }
    }

    @EventHandler
    public void onPickupArrow(PlayerPickupArrowEvent e) {
        Projectile pr = e.getArrow();
        if(pr.hasMetadata("R99") || pr.hasMetadata("Kraber")) {
            e.setCancelled(true);
        }
    }

    private final Set<Player> noUpDown = new HashSet<>();

    @EventHandler
    public void onDownGold(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        Block b = p.getLocation().subtract(0.0, 1.0, 0.0).getBlock();
        if (!p.isSneaking() && b.getType() == Material.GOLD_BLOCK  && !noUpDown.contains(p)) {
            p.teleport(p.getLocation().subtract(0.0, 3.0, 0.0));
            MPEXshooter.noDamagePlayers.add(p);
            noUpDown.add(p);
            new BukkitRunnable() {
                int tl = 10;

                @Override
                public void run() {
                    if (!p.isOnline() || !MPEXshooter.noDamagePlayers.contains(p)) {
                        cancel();
                        return;
                    }
                    if (tl == 0) {
                        MPEXshooter.noDamagePlayers.remove(p);
                        p.sendActionBar(ChatColor.GREEN + "\ubb34\uc801" + ChatColor.GRAY + " :: " + ChatColor.RED + "0");
                        cancel();
                        return;
                    }
                    p.sendActionBar(ChatColor.GREEN + "\ubb34\uc801" + ChatColor.GRAY + " :: " + ChatColor.YELLOW + tl);
                    tl--;
                }
            }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 1);


            Bukkit.getScheduler().runTaskLater(MPEXshooter.getPlugin(MPEXshooter.class), () -> noUpDown.remove(p), 20);
        }
    }

    @EventHandler
    public void onUpGold(PlayerJumpEvent e) {
        Player p = e.getPlayer();
        Block b = p.getLocation().add(0.0, 2.0, 0.0).getBlock();
        if (!p.isSneaking() && b.getType() == Material.GOLD_BLOCK && !noUpDown.contains(p)) {
            p.teleport(p.getLocation().add(0.0, 3.0, 0.0));
            MPEXshooter.noDamagePlayers.add(p);
            noUpDown.add(p);
            new BukkitRunnable() {
                int tl = 10;

                @Override
                public void run() {
                    if (!p.isOnline() || !MPEXshooter.noDamagePlayers.contains(p)) {
                        cancel();
                        return;
                    }
                    if (tl == 0) {
                        MPEXshooter.noDamagePlayers.remove(p);
                        p.sendActionBar(ChatColor.GREEN + "\ubb34\uc801" + ChatColor.GRAY + " :: " + ChatColor.RED + "0");
                        cancel();
                        return;
                    }
                    p.sendActionBar(ChatColor.GREEN + "\ubb34\uc801" + ChatColor.GRAY + " :: " + ChatColor.YELLOW + tl);
                    tl--;
                }
            }.runTaskTimer(MPEXshooter.getPlugin(MPEXshooter.class), 0, 1);

            Bukkit.getScheduler().runTaskLater(MPEXshooter.getPlugin(MPEXshooter.class), () -> noUpDown.remove(p), 20);
        }
    }


    @Override
    public void onDisable() {
        System.out.println("MPEXshooter plugin is disabled");
    }
}
