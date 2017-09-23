package songlib.view;

/**
 * @author Swapna Chakraverthy
 * @author Neel Patel
 */
public class Song {
	
	private String name;
	private String artist;
	private String album;
	private int year;
	
	public Song() {
		
	}
	
	public Song(String name, String artist) {
		this.name = name;
		this.artist = artist;
	}

	public String toString() {
		return name + " by " + artist;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Song)) {
			return false;
		}
		Song other = (Song)o;
		return name == other.name && artist == other.artist;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public int getYear() {
		return year;
	}
}
