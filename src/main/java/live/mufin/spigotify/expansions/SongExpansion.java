package live.mufin.spigotify.expansions;

import com.google.gson.Gson;
import live.mufin.spigotify.Spigotify;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;


public class SongExpansion extends PlaceholderExpansion {
  
  public static final MediaType JSON
     = MediaType.get("application/json; charset=utf-8");
  private final OkHttpClient okHttpClient;
  private final Spigotify spigotify;
  
  public SongExpansion(Spigotify spigotify) {
    this.spigotify = spigotify;
    this.okHttpClient = new OkHttpClient().newBuilder().build();
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
  
  TracksResponse getSongInfo(String username) throws IOException {
    Request request = new Request.Builder()
       .url(
          "https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="
             + username +
             "&api_key="
             + spigotify.getConfig().getString("api-key") +
             "&format=json&limit=1"
       )
       .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      String body = response.body().string();
      return new Gson().fromJson(body, TracksResponse.class);
    }
  }
  
  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    try {
      AtomicReference<String> result = new AtomicReference<>(null);
      this.spigotify.storage
         .load()
         .stream()
         .filter(user -> user.getUuid().equals(player.getUniqueId()))
         .findAny()
         .ifPresent(user -> {
           if (user.isVisible())
             try {
               TracksResponse response = getSongInfo(user.getLastFmUsername());
               switch (params) {
                 case "song" -> {
                   Track t = response.getRecenttracks().getTrack().get(0);
                   result.set(t.getAttributes() != null | t.getAttributes().isNowplaying() ? t.getName() : null);
                 }
                 case "artist" -> {
                   Track t = response.getRecenttracks().getTrack().get(0);
                   result.set(t.getAttributes() != null | t.getAttributes().isNowplaying() ? t.getArtist().getName() : null);
                 }
                 case "album" -> {
                   Track t = response.getRecenttracks().getTrack().get(0);
                   result.set(t.getAttributes() != null | t.getAttributes().isNowplaying() ? t.getAlbum().getName() : null);
                 }
               }
             } catch (IOException | IndexOutOfBoundsException e) {
               e.printStackTrace();
             }
           else result.set(null);
         });
      return result.get() == null ? "Not playing" : result.get();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
