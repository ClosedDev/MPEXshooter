package io.github.kuroka3.mpexshooter.weapon;

import io.github.kuroka3.mpexshooter.MPEXshooter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

public class Shoot {
    public static void launcharrow(Player shooter, int loop, String metadataKey) {

        for(int i=loop; i>0; i--) {
            final int count = i;
            Bukkit.getScheduler().runTaskLater(MPEXshooter.getPlugin(MPEXshooter.class), () -> {
                Projectile pr = shooter.launchProjectile(org.bukkit.entity.Arrow.class);
                pr.setShooter(shooter);
                pr.setVelocity(pr.getVelocity().multiply(2.47));
                shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_SHOOT, 100, 1);
                pr.setMetadata(metadataKey, new FixedMetadataValue(MPEXshooter.getPlugin(MPEXshooter.class), 0));
                if(metadataKey == "R99") {
                    int amos = R99.amor99.get(shooter);
                    amos--;
                    R99.amor99.put(shooter, amos);
                } else if(metadataKey == "Kraber") {
                    int amos = Kraber.amokraber.get(shooter);
                    amos--;
                    Kraber.amokraber.put(shooter, amos);
                }
            }, count);
        }
    }
}
