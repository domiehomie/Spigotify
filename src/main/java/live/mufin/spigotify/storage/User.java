package live.mufin.spigotify.storage;

import java.util.Objects;

public class User {

  String lastFmUsername;
  boolean visible;

  public User(String lastFmUsername, boolean visible) {
    this.lastFmUsername = lastFmUsername;
    this.visible = visible;
  }


  @Override
  public String toString() {
    return "User{" +
       "lastFmUsername='" + lastFmUsername + '\'' +
       ", visible=" + visible +
       '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastFmUsername, visible);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return lastFmUsername.equals(user.lastFmUsername);
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
