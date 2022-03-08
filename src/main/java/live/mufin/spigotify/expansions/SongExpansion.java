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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;


public class SongExpansion extends PlaceholderExpansion {

  public static final MediaType JSON
     = MediaType.get("application/json; charset=utf-8");
  private final OkHttpClient okHttpClient;
  private final Cache<User, Track> userCache = Caffeine.newBuilder()
     .build();
  private Spigotify spigotify;

  private final BiConsumer<Map<String, User>, Player> fetchPlayer = (Map<String, User> users, Player player) -> {
    User user = users.get(player.getUniqueId().toString());
    if (user == null) return;
    TracksResponse tracks = getSongInfo(user.getLastFmUsername());
    if (tracks == null ||
       tracks.getRecenttracks() == null ||
       tracks.getRecenttracks().getTrack() == null ||
       tracks.getRecenttracks().getTrack().size() == 0
    ) {
      userCache.invalidate(user);
      return;
    }
    this.userCache.put(user, tracks.getRecenttracks().getTrack().get(0));
  };

  private final Runnable fetcher = () -> {
    Map<String, User> users = spigotify.storage.load();
    Bukkit.getOnlinePlayers()
       .parallelStream()
       .forEach(player -> Bukkit.getScheduler().runTaskAsynchronously(spigotify, () -> fetchPlayer.accept(users, player)));
  };

  public SongExpansion(Spigotify spigotify) {
    this.spigotify = spigotify;
    this.okHttpClient = new OkHttpClient().newBuilder().build();

    Bukkit.getScheduler().runTaskTimer(spigotify, this.fetcher, 0, 20L * spigotify.getConfig().getInt("fetch-delay"));
  }

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
      return null;
    }
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    User user = spigotify.storage
       .load()
       .get(player.getUniqueId().toString());
    if (user == null) return "Loading...";

    Track track = userCache.getIfPresent(user);

    if (track == null) return "Loading...";
    if (track.getAttributes() == null) return "Not Playing";
    switch (params) {
      case "song" -> {
        return track.getAttributes().isNowplaying() ? track.getName() : null;
      }
      case "artist" -> {
        return track.getAttributes().isNowplaying() ? track.getArtist().getName() : null;
      }
      case "album" -> {
        return track.getAttributes().isNowplaying() ? track.getAlbum().getName() : null;
      }
      default -> {
        return null;
      }
    }

  }
}
