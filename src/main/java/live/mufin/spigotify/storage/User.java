package live.mufin.spigotify.storage;

import java.util.Objects;
import java.util.UUID;

public class User {
  
  String lastFmUsername;
  boolean visible;
  UUID uuid;
  
  public User(String lastFmUsername, boolean visible, UUID uuid) {
    this.lastFmUsername = lastFmUsername;
    this.visible = visible;
    this.uuid = uuid;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return visible == user.visible && Objects.equals(lastFmUsername, user.lastFmUsername) && Objects.equals(uuid, user.uuid);
  }
  
  @Override
  public String toString() {
    return "User{" +
       "lastFmUsername='" + lastFmUsername + '\'' +
       ", visible=" + visible +
       ", uuid=" + uuid +
       '}';
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(lastFmUsername, visible, uuid);
  }
  
  public UUID getUuid() {
    return uuid;
  }
  
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public String getLastFmUsername() {
    return lastFmUsername;
  }
  
  public void setLastFmUsername(String lastFmUsername) {
    this.lastFmUsername = lastFmUsername;
  }
  
  public boolean isVisible() {
    return visible;
  }
  
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
