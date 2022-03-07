package live.mufin.spigotify.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import live.mufin.spigotify.Spigotify;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage implements IStorage {
  
  private static final String FILE_NAME = "users.json";
  private final Gson gson;
  private final Spigotify spigotify;
  
  public JsonStorage(Spigotify spigotify) {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    this.spigotify = spigotify;
  }
  
  @Override
  public List<User> load() throws IOException {
    File f = new File(spigotify.getDataFolder(), FILE_NAME);
    if(!f.exists()) {
      f.createNewFile();
    }
    Reader reader = Files.newBufferedReader(f.toPath());
    List<User> users = gson.fromJson(reader, new TypeToken<List<User>>() {
    }.getType());
    reader.close();
    if (users == null) return new ArrayList<>();
    return users;
  }
  
  @Override
  public void save(List<User> users) throws IOException {
    Writer writer = Files.newBufferedWriter(new File(spigotify.getDataFolder(), FILE_NAME).toPath());
    this.gson.toJson(users, writer);
    writer.close();
  }
  
}
