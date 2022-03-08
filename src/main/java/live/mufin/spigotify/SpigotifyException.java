package live.mufin.spigotify;

public class SpigotifyException extends RuntimeException{
  public SpigotifyException() {
    super();
  }

  public SpigotifyException(String message) {
    super(message);
  }

  public SpigotifyException(String message, Throwable cause) {
    super(message, cause);
  }

  public SpigotifyException(Throwable cause) {
    super(cause);
  }

  protected SpigotifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
