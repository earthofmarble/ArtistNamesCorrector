import com.mpatric.mp3agic.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

         // static ArrayList<Song> songsList = new ArrayList<>();
            static final String DIRECTORY_PATH = "E:\\iTunes Media\\Music";

    public static void main(String[] args){
        try {
            //после тестов забыл вернуть всё в один проход, ладно, уже поздно менять
            walk(DIRECTORY_PATH, true);
            walk(DIRECTORY_PATH, false);
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        }
    }

                                            //isFirst - true - если проходим по названиям, false - если по исполнителям
    public static void walk(String path, boolean isFirst) throws InvalidDataException, IOException, UnsupportedTagException {

        File root = new File(path);
        File[] list = root.listFiles();

        String tempArtist = null;
        String tempSong = null;
        String tempPath = null;

        if (list == null) return;
        //проходим по файлам
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk(f.getAbsolutePath(), isFirst);
                System.out.println( "Dir:" + f.getAbsoluteFile() + " PARENT: " + f.getAbsoluteFile().getParentFile().getName());
            }
            else {
                System.out.println( "File:" + f.getAbsoluteFile() );


                    //получаем ид3 теги нашей песни
                    try {
                        tempPath = String.valueOf(f.getAbsoluteFile());
                        Mp3File mp3file = new Mp3File(f.getAbsoluteFile());
                        obsoleteStage(mp3file);
                        ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                        //получаем имена
                        String song = id3v2Tag.getTitle();
                        String artist = id3v2Tag.getArtist();

                        System.out.println("песня " + song);
                        System.out.println("Артист " + artist);


                        if (!isFirst) {
                            ArrayList<Integer> positionList = new ArrayList<>();

                            for (int index = artist.indexOf(";"); index >= 0; index = artist.indexOf(";", index + 1)) {
                                positionList.add(index);
                                System.out.println("!!!");
                            }
                            System.out.println("pos length " + positionList.size() + " \u001B[32m");
                            //  artist = artist.replaceAll(";", "");
                            StringBuilder builder = new StringBuilder(artist);
                            if (positionList.size() > 1) {
                                builder.deleteCharAt(positionList.get(positionList.size() - 1));
                                builder.insert(positionList.get(positionList.size() - 1), " &"); //1

//                                id3v2Tag.setArtist(String.valueOf(builder));

                                if (positionList.size() > 2) {
                                    for (int i = 1; i < positionList.size() - 1; i++) {
                                        builder.deleteCharAt(positionList.get(i));
                                        builder.insert(positionList.get(i), ","); //2
                                    }
                                }
                                builder.deleteCharAt(positionList.get(0));
                                builder.insert(positionList.get(0), " feat.");//3

                                id3v2Tag.setArtist(String.valueOf(builder));
                                System.out.println(" gtctyz: " + builder);
                                saveMp3(mp3file, tempPath);
                            }


                            if (positionList.size() == 1) {
                                builder.deleteCharAt(positionList.get(0));
                                builder.insert(positionList.get(0), " feat.");


                                System.out.println("gtcty1z: " + builder);
                                id3v2Tag.setArtist(String.valueOf(builder));
                                saveMp3(mp3file, tempPath);
                            }



                            System.out.println("\u001B[0m");
                        } else {
                                if (song.contains("feat.")) {
                                    String tempArtistAdd=song;
                                    tempSong = StringUtils.substringBefore(song, "feat.");
                                    System.out.println("tempSong1: " + tempSong);
                                    tempArtistAdd=song.replace(tempSong, "");
                                    System.out.println("song: " + song);
                                    System.out.println("tempArt: " + tempArtistAdd);


                                    tempArtistAdd=tempArtistAdd.replaceAll("\\)","");
                                    System.out.println("111: " + tempArtistAdd);

                                    tempSong=tempSong.substring(0, tempSong.length()-1);

                                    if (!String.valueOf(artist.charAt(artist.length()-1)).equals(" ")){
                                        tempArtist = artist + " " + tempArtistAdd;
                                    } else if (String.valueOf(artist.charAt(artist.length()-1)).equals(" ")){
                                        tempArtist = artist + tempArtistAdd;
                                    }

                                    System.out.println("NEW ARTIST: " + tempArtist + " new song: " + tempSong);

                                    id3v2Tag.setArtist(tempArtist);
                                    id3v2Tag.setTitle(tempSong);
                                    saveMp3(mp3file, tempPath);

                                   }

                        }
                    } catch (InvalidDataException | NullPointerException | IllegalArgumentException  e){
                        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        writeFile(tempPath, String.valueOf(e), "", "nompegsfound");
                    }


            }
        }
    }


    private static void writeFile(String songPath, String lyricsPath, String count, String fileName){

        try(FileWriter writer = new FileWriter(fileName+ ".txt", true))
        {
            String text = "! " + count + "!" + "\nПуть к песне: " + songPath + "\nПуть к тексту: " + lyricsPath +"\n\n";
            writer.write(text);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static void obsoleteStage(Mp3File item){

        if (item.getId3v2Tag().getObseleteFormat()){

            String tempTitle = item.getId3v2Tag().getTitle();
            String tempYear = item.getId3v2Tag().getYear();
            String tempAlbum = item.getId3v2Tag().getAlbum();
            String tempArtist = item.getId3v2Tag().getArtist();
            String tempAlbumArtist = item.getId3v2Tag().getAlbumArtist();
            int tempGenre = item.getId3v2Tag().getGenre();
            String tempGenreDescription = item.getId3v2Tag().getGenreDescription();
            String tempTrack = item.getId3v2Tag().getTrack();
            byte[] tempAlbumCover = item.getId3v2Tag().getAlbumImage();
            String tempMimeType = item.getId3v2Tag().getAlbumImageMimeType();
            String tempLyrics = item.getId3v2Tag().getLyrics();
            String tempLyricsReady = item.getId3v2Tag().getPaymentUrl();

            ID3v24Tag id3v24Tag = new ID3v24Tag();
            item.setId3v2Tag(id3v24Tag);

            item.getId3v2Tag().setTitle(tempTitle);
            item.getId3v2Tag().setYear(tempYear);
            item.getId3v2Tag().setAlbum(tempAlbum);
            item.getId3v2Tag().setArtist(tempArtist);
            item.getId3v2Tag().setGenre(tempGenre);
            item.getId3v2Tag().setGenreDescription(tempGenreDescription);
            item.getId3v2Tag().setTrack(tempTrack);
            item.getId3v2Tag().setAlbumImage(tempAlbumCover, tempMimeType);
            item.getId3v2Tag().setAlbumArtist(tempAlbumArtist);
            item.getId3v2Tag().setLyrics(tempLyrics);
            item.getId3v2Tag().setPaymentUrl(tempLyricsReady);

            //  item.getId3v2Tag().setArtist("ARTIST NEW OBSOLETE");

            saveMp3(item, item.getFilename());
        }
//        else {
//            //  System.out.println(item.getId3v2Tag().getGenre() + "  " + item.getId3v2Tag().getGenreDescription());
//            item.getId3v2Tag().setArtist("ARTIST NEW not obsolete");
//            saveMp3(item, item.getFilename());
//        }
    }

    private static void renameFile(String oldPath){
        //чтоб не запутаться
        //берем НОВЫЙ файл
        File newFile = new File(oldPath + ".new");
        if(!newFile.exists() || newFile.isDirectory()) {
            System.out.println("НЕ НАШЛО .new файл, нужно проверить");
            // writeFile(oldPath, "НЕ НАШЛО .new файл, нужно проверить", "", "ERRORS");
        }
        //берем старый файл
        File oldFile = new File(oldPath);
        //копируем путь старого файла
        String buf = oldFile.getPath();
        //берем файл бэкапа
        File bakFile = new File(oldPath + ".bak");
        if(bakFile.exists() && !bakFile.isDirectory()) {
            System.out.println("НЕ ЗАПИСАЛО .bak файл, нужно проверить");
            //  writeFile(oldPath, "НЕ ЗАПИСАЛО .bak файл, нужно проверить", "", "ERRORS");
            return;
        }
        //переименовываем старый файл в файл бэкапа
        System.out.println(oldFile.renameTo(bakFile));
        //переименовываем новый файл в старый
        System.out.println(newFile.renameTo(new File(buf)));
        //удаляем старый файл(уже бекапа)
        System.out.println(bakFile.delete());

    }

    private static void saveMp3(Mp3File mp3file, String oldPath){
        try {
            if(!mp3file.hasId3v2Tag()){
                return;
            }
            // ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            // id3v2Tag.setPaymentUrl("lyricsReady");
            mp3file.save(oldPath+".new");
        } catch (IOException | NotSupportedException e) {
            System.out.println("ERROR не может записать новый файл");
            //   writeFile(String.valueOf(e), oldPath, "ERROR не может записать новый файл", "ERRORS");
        }
        renameFile(oldPath);
    }



}
