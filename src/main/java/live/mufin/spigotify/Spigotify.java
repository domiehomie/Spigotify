package live.mufin.spigotify;

import live.mufin.spigotify.commands.SetUserCommand;
import live.mufin.spigotify.commands.SpigotifyCommand;
import live.mufin.spigotify.commands.ToggleVisibleCommand;
import live.mufin.spigotify.expansions.SongExpansion;
import live.mufin.spigotify.storage.IStorage;
import live.mufin.spigotify.storage.JsonStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spigotify extends JavaPlugin {
  
  public IStorage storage;
  
  @Override
  public void onEnable() {
    this.storage = new JsonStorage(this);
    Objects.requireNonNull(getCommand("setuser")).setExecutor(new SetUserCommand(this));
    Objects.requireNonNull(getCommand("togglevisible")).setExecutor(new ToggleVisibleCommand(this));
    Objects.requireNonNull(getCommand("spigotify")).setExecutor(new SpigotifyCommand(this));

    saveDefaultConfig();
    
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
      getSLF4JLogger().error("You must install PAPI.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    if(Objects.requireNonNull(getConfig().getString("api-key")).equals("API_KEY_HERE")) {
      getSLF4JLogger().error("You must set a valid API key. Disabling...");
      Bukkit.getPluginManager().disablePlugin(this);
    }

    new SongExpansion(this).register();
  }
  
  @Override
  public void onDisable() {
  
  }
  
}