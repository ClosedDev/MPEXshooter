package io.github.kuroka3.mpexshooter.etc;

import io.github.kuroka3.mpexshooter.weapon.Kraber;
import io.github.kuroka3.mpexshooter.weapon.R99;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ShowAmo extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack i = player.getInventory().getItemInMainHand();
            if (i.getType() == Material.NETHERITE_SWORD) {
                if(R99.amor99.get(player) == null) R99.amor99.put(player, 30);
                player.sendTitle("", ChatColor.GOLD + String.valueOf(R99.amor99.get(player)), 0, 2, 0);
            } else if (i.getType() == Material.SPYGLASS) {
                if(Kraber.amokraber.get(player) == null) Kraber.amokraber.put(player, 4);
                player.sendTitle("", ChatColor.AQUA + String.valueOf(Kraber.amokraber.get(player)), 0, 2, 0);
            }
        }
    }

}
