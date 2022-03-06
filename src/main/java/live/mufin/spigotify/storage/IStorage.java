package live.mufin.spigotify.storage;

import java.io.IOException;
import java.util.List;

public interface IStorage {
  
  List<User> load() throws IOException;
  
  void save(List<User> users) throws IOException;
  
}
