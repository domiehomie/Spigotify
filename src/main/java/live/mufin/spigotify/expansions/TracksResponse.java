package live.mufin.spigotify.expansions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TracksResponse {
  RecentTracks recenttracks;
  
  public RecentTracks getRecenttracks() {
    return recenttracks;
  }
}

class RecentTracks {
  List<Track> track;
  
  public List<Track> getTrack() {
    return track;
  }
}

class Track {
  private Artist artist;
  private Album album;
  private String name;
  @SerializedName("@attr")
  private Attributes attributes;
  
  public Artist getArtist() {
    return artist;
  }
  
  public Album getAlbum() {
    return album;
  }
  
  public String getName() {
    return name;
  }
  
  public Attributes getAttributes() {
    return attributes;
  }
}

class Artist {
  @SerializedName("#text")
  private String name;
  
  public String getName() {
    return name;
  }
}

class Album {
  @SerializedName("#text")
  private String name;
  
  public String getName() {
    return name;
  }
}

class Attributes {
  private boolean nowplaying;
  
  public boolean isNowplaying() {
    return nowplaying;
  }
}