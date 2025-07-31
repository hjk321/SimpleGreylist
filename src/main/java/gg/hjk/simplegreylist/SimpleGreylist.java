package gg.hjk.simplegreylist;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SimpleGreylist extends JavaPlugin implements Listener {
    LuckPerms lp;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            this.lp = provider.getProvider();
        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(this.getCommand("vouch")).setExecutor(new VouchCommand(this));
    }

    // HACKY EVENTS THAT SHOULD BE IN VANE
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_targeted(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        if (target instanceof Player && !target.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_inside_block(EntityInsideBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && !entity.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_pickup_item(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && !entity.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_attempt_pickup_item(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_pickup_arrow(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_pickup_xp(PlayerPickupExperienceEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on_player_drop_item(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("vane.admin.modify_world"))
            event.setCancelled(true);
    }

}
