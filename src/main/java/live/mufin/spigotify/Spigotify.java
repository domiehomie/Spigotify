package live.mufin.spigotify;

import live.mufin.spigotify.commands.SetUserCommand;
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
    getCommand("setuser").setExecutor(new SetUserCommand(this));
    getCommand("togglevisible").setExecutor(new ToggleVisibleCommand(this));
    
    saveDefaultConfig();
    
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
      getSLF4JLogger().error("You must install PAPI.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    
    new SongExpansion(this).register();
  }
  
  @Override
  public void onDisable() {
  
  }
  
}