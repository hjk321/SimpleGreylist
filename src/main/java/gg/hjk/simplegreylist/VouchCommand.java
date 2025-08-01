package gg.hjk.simplegreylist;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.track.Track;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class VouchCommand implements CommandExecutor {

    private final SimpleGreylist plugin;
    private final String unknownPlayerGroupName = "default";
    private final String trackToUseName = "default";

    public VouchCommand(SimpleGreylist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        boolean hasPermission;
        String name;
        UUID id;
        if (sender instanceof ConsoleCommandSender) {
            hasPermission = true;
            name = "[Console]";
            id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        } else if (sender instanceof Player) {
            hasPermission = sender.hasPermission("simplegreylist.vouch");
            name = sender.getName();
            id = ((Player) sender).getUniqueId();
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command can only be run by a player or the console."));
            return true;
        }
        if (!hasPermission) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission."));
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        OfflinePlayer player = this.plugin.getServer().getPlayerExact(args[0]);
        if (player == null) {
            player = this.plugin.getServer().getOfflinePlayerIfCached(args[0]);
        }
        if (player == null || (!player.isOnline() && !player.hasPlayedBefore())) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Couldn't find a player named " + args[0] + "."));
            return true;
        }
        if (player.isBanned()) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>" + args[0] + " appears to be a banned player."));
            return true;
        }

        final UUID finalId = id;
        final String finalName = name;
        final OfflinePlayer finalPlayer = player;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<Group> group = plugin.lp.getGroupManager().loadGroup(unknownPlayerGroupName).join();
            Optional<Track> track = plugin.lp.getTrackManager().loadTrack(trackToUseName).join();
            if (group.isEmpty() || track.isEmpty() || !track.get().containsGroup(group.get())) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>There was an issue getting permission groups. Please report this error."));
                });
                return;
            }

            User user = plugin.lp.getUserManager().loadUser(finalPlayer.getUniqueId(), args[0]).join();
            if (user == null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Couldn't find a player named " + args[0] + "."));
                });
                return;
            }
            if (!user.getInheritedGroups(QueryOptions.builder(QueryMode.CONTEXTUAL).build()).contains(group.get())) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>" + args[0] + " appears to be vouched for already."));
                });
                return;
            }
            boolean success = track.get().promote(user, plugin.lp.getContextManager().getStaticContext()).wasSuccessful();
            if (!success) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>We were unable to set permissions for " + args[0] + ". Please report this error."));
                });
                return;
            }
            plugin.lp.getUserManager().saveUser(user).join();

            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize("<dark_aqua>" + finalName + " has vouched for " + args[0] + "."));
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Successfully vouched for " + args[0] + "."));
                Player onlinePlayer = plugin.getServer().getPlayer(finalPlayer.getUniqueId());
                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize("<green>" + finalName + " vouched for you. You may now interact with the world."));
                }
            });

            plugin.lp.getActionLogger().submit(Action.builder()
                    .source(finalId).sourceName(finalName)
                    .target(finalPlayer.getUniqueId()).targetName(args[0]).targetType(Action.Target.Type.USER)
                    .timestamp(Instant.now()).description("[SimpleGreylist] vouched for")
                    .build()).join();
        });

        return true;
    }
}
