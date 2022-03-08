package live.mufin.spigotify.expansions;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import live.mufin.spigotify.Spigotify;
import live.mufin.spigotify.storage.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class SongExpansion extends PlaceholderExpansion {

  public static final MediaType JSON
     = MediaType.get("application/json; charset=utf-8");
  private final OkHttpClient okHttpClient;
  private Spigotify spigotify;

  private Cache<User, Track> userCache = Caffeine.newBuilder()
     .build();

  public SongExpansion(Spigotify spigotify) {
    this.spigotify = spigotify;
    this.okHttpClient = new OkHttpClient().newBuilder().build();

    Bukkit.getScheduler().runTaskTimer(spigotify, this.fetcher, 0, 20 * spigotify.getConfig().getInt("fetch-delay"));
  }

  Runnable fetcher = () -> spigotify.storage
     .load()
     .forEach(user -> {
       if (Bukkit.getPlayer(user.getUuid()) != null)
         Bukkit.getScheduler().runTaskAsynchronously(spigotify, () -> userCache.put(user, getSongInfo(user.getLastFmUsername()).getRecenttracks().getTrack().get(0)));
     });

  @Override
  public @NotNull String getIdentifier() {
    return "spigotify";
  }

  @Override
  public @NotNull String getAuthor() {
    return "mufinlive";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public boolean persist() {
    return true;
  }

  TracksResponse getSongInfo(String username) {
    Request request = new Request.Builder()
       .url(
          "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="
             + username +
             "&api_key="
             + spigotify.getConfig().getString("api-key") +
             "&format=json&limit=1"
       )
       .build();
    try {
      try (Response response = okHttpClient.newCall(request).execute()) {
        String body = response.body().string();
        return new Gson().fromJson(body, TracksResponse.class);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    Optional<User> userOpt = spigotify.storage
       .load()
       .stream()
       .filter(user -> user.getUuid().equals(player.getUniqueId()))
       .findAny();
    if (userOpt.isEmpty()) return "Not Found";
    User user = userOpt.get();
    Track track = userCache.getIfPresent(user);
    if (track == null) return "Not Found";

    switch (params) {
      case "song" -> {
        return track.getAttributes() != null | track.getAttributes().isNowplaying() ? track.getName() : null;
      }
      case "artist" -> {
        return track.getAttributes() != null | track.getAttributes().isNowplaying() ? track.getArtist().getName() : null;
      }
      case "album" -> {
        return track.getAttributes() != null | track.getAttributes().isNowplaying() ? track.getAlbum().getName() : null;
      }
      default -> {
        return "Not playing";
      }
    }

  }
}
