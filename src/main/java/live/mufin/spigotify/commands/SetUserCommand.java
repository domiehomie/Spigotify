package live.mufin.spigotify.commands;

import live.mufin.spigotify.Spigotify;
import live.mufin.spigotify.storage.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SetUserCommand implements CommandExecutor {
  private final Spigotify spigotify;


  public SetUserCommand(Spigotify spigotify) {
    this.spigotify = spigotify;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) return true;

    Arrays.stream(args)
       .findFirst()
       .ifPresentOrElse((s) -> {
         var users = spigotify.storage.load();
         var user = users.get(player.getUniqueId().toString());

         if (user == null) {
           users.put(player.getUniqueId().toString(), new User(s, true));
         } else {
           user.setLastFmUsername(s);
           users.put(player.getUniqueId().toString(), user);
         }
         spigotify.storage.save(users);
         player.sendMessage(Component.text("Successfully set your Last.fm username.")
            .color(TextColor.fromHexString("#44bd32")));
       }, () -> player.sendMessage(
          Component.text("You must provide a Last.fm username.")
             .color(TextColor.fromHexString("#eb2f06"))
       ));

    return true;
  }
}
