package live.mufin.spigotify.expansions;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import live.mufin.spigotify.Spigotify;
import live.mufin.spigotify.storage.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.insprill.fetch4j.exception.FetchException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

import static net.insprill.fetch4j.Fetch.fetch;

public class SongExpansion extends PlaceholderExpansion {

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
    return "1.2";
  }

  @Override
  public boolean persist() {
    return true;
  }

  TracksResponse getSongInfo(String username) {
    try {
      String body = fetch(
              "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=%s&api_key=%s&format=json&limit=1"
                      .formatted(username, spigotify.getConfig().getString("api-key"))
      ).getBody();
      return new Gson().fromJson(body, TracksResponse.class);
    } catch (FetchException e) {
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
