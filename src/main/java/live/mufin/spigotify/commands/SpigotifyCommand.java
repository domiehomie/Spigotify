package live.mufin.spigotify.commands;

import live.mufin.spigotify.Spigotify;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SpigotifyCommand implements CommandExecutor {

  private final Spigotify spigotify;

  public SpigotifyCommand(Spigotify spigotify) {
    this.spigotify = spigotify;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if(args.length == 0) {
      commandSender.sendMessage(
         Component.text()
            .append(dividerComponent())
            .append(Component.newline())
            .append(cmdComponent("spigotify"))
            .append(Component.newline())
            .append(cmdComponent("spigotify reload"))
            .append(Component.newline())
            .append(cmdComponent("setuser <username>"))
            .append(Component.newline())
            .append(cmdComponent("togglevisible"))
            .append(Component.newline())
            .append(dividerComponent())
      );
      return true;
    }
    if(args[0].equalsIgnoreCase("reload")) {
      this.spigotify.reloadConfig();
      commandSender.sendMessage(Component.text("Reloaded config.").color(TextColor.fromHexString("#44bd32")));
    }
    return true;
  }


  private Component cmdComponent(String cmd) {
    return Component.text("/" + cmd).color(TextColor.fromHexString("#44bd32"));
  }

  private Component dividerComponent() {
    return Component.text("============").color(TextColor.fromHexString("#2f3640"));
  }
}
