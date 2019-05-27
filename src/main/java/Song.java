public class Song {

    String songPath;
    String songName;
    String songContributingArtist;

    public Song (String songPath, String songName, String songContributingArtist){
        this.songPath=songPath;
        this.songName=songName;
        this.songContributingArtist=songContributingArtist;
    }

    public String getSongPath() {
        return songPath;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongContributingArtist() {
        return songContributingArtist;
    }
}
