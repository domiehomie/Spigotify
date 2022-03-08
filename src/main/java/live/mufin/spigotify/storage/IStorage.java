package live.mufin.spigotify.storage;

import java.io.IOException;
import java.util.List;

public interface IStorage {
  
  List<User> load();
  
  void save(List<User> users);
  
}
