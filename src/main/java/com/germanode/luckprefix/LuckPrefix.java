//LuckPrefix by Germanode
package com.germanode.luckprefix;

import org.bukkit.plugin.java.JavaPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.node.types.WeightNode;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LuckPrefix extends JavaPlugin {
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().severe("LuckPerms not found! LuckPrefix requires LuckPerms to function. Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        luckPerms = LuckPermsProvider.get();
        getLogger().info("LuckPrefix enabled!");
                          }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY + "Usage: /prefix <prefix> [player]");
            return true;
        }

        String prefix = ChatColor.translateAlternateColorCodes('&', args[0]);
        if (!player.hasPermission("prefix.color")) {
            prefix = ChatColor.stripColor(prefix);
        }

        if (prefix.length() > 16 && !player.hasPermission("prefix.length")) {
            player.sendMessage(ChatColor.GRAY + "Your prefix cannot be longer than 16 characters.");
            return true;
        }

        if (args.length > 1) {
            if (!player.hasPermission("prefix.assign")) {
                player.sendMessage(ChatColor.GRAY + "You do not have permission to assign prefixes to others.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.GRAY + "Player not found.");
                return true;
            }

            User user = luckPerms.getUserManager().getUser(target.getUniqueId());
            if (user == null) {
                player.sendMessage(ChatColor.GRAY + "User not found in LuckPerms.");
                return true;
            }

            PrefixNode prefixNode = LuckPermsProvider.get().getNodeBuilderRegistry().forPrefix().prefix(prefix).build();
            user.data().add(prefixNode);
            luckPerms.getUserManager().saveUser(user);
            target.sendMessage(ChatColor.GRAY + "Your prefix has been updated to: " + prefix);
            player.sendMessage(ChatColor.GRAY + "Prefix updated for " + target.getName() + ": " + prefix);
            } else {
                if (!player.hasPermission("prefix.self")) {
                    player.sendMessage(ChatColor.GRAY + "You do not have permission to assign a prefix to yourself.");
                    return true;
                }
            }

            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                player.sendMessage(ChatColor.GRAY + "User not found in LuckPerms.");
                return true;
            }
    }
}