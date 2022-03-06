package live.mufin.spigotify.commands;

import live.mufin.spigotify.Spigotify;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ToggleVisibleCommand implements CommandExecutor {
  
  private final Spigotify spigotify;
  
  public ToggleVisibleCommand(Spigotify spigotify) {
    this.spigotify = spigotify;
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(Component.text("Only players can execute this command.")
         .color(TextColor.fromHexString("#eb2f06")));
      return true;
    }
    
    try {
      var users = this.spigotify.storage.load();
      users
         .stream()
         .filter(usr -> usr.getUuid().equals(player.getUniqueId()))
         .findAny()
         .ifPresentOrElse(
            usr -> {
              users.remove(usr);
              usr.setVisible(!usr.isVisible());
              users.add(usr);
              try {
                this.spigotify.storage.save(users);
                player.sendMessage(
                   Component.text("Successfully toggled your visibility. [" + usr.isVisible() + "]")
                      .color(TextColor.fromHexString("#44bd32"))
                );
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            , () -> player.sendMessage(
               Component.text("You have not set your Last.fm username. Please do so using /setuser <username>.")
                  .color(TextColor.fromHexString("#eb2f06"))));
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }
}
