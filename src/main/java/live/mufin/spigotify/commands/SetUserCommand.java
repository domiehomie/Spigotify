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

import java.io.IOException;
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
         try {
           var users = spigotify.storage.load();
           users.stream()
              .filter(user -> user.getUuid().equals(player.getUniqueId()))
              .findFirst()
              .ifPresentOrElse(user -> {
                user.setLastFmUsername(s);
              }, () -> users.add(new User(s, true, player.getUniqueId())));
           spigotify.storage.save(users);
           
           player.sendMessage(Component.text("Successfully set your Last.fm username.")
              .color(TextColor.fromHexString("#44bd32")));
         } catch (IOException e) {
           e.printStackTrace();
         }
       }, () -> player.sendMessage(
          Component.text("You must provide a Last.fm username.")
             .color(TextColor.fromHexString("#eb2f06"))
       ));
    
    return true;
  }
}
