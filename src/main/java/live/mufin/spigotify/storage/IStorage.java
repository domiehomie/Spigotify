package live.mufin.spigotify.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IStorage {
  
  Map<String, User> load();
  
  void save(Map<String, User> users);
  
}
