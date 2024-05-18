package JAVA_MUSIC;

import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.bson.Document;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteSongController implements Initializable {

    @FXML
    private ComboBox<String> songComboBox;

    private final MongoCollection<Document> songCollection = Main_Controller.songCollection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateSongComboBox();
    }

    private void populateSongComboBox() {
        List<Document> songDocuments = songCollection.find().into(new ArrayList<>());
        for (Document songDoc : songDocuments) {
            String songTitle = songDoc.getString("title");
            String songArtist = songDoc.getString("artist");
            songComboBox.getItems().add(songTitle + " - " + songArtist);
        }
    }

    @FXML
    private void deleteSong() {
        String selectedSong = songComboBox.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            String[] parts = selectedSong.split(" - ");
            String title = parts[0];
            String artist = parts[1];

            // Retrieve the document to get the file path
            Document filter = new Document("title", title).append("artist", artist);
            Document songDoc = songCollection.find(filter).first();

            if (songDoc != null) {
                String filePath = songDoc.getString("filePath");

                // Check if the song being deleted is the one currently playing
                if (Main_Controller.mediaPlayer != null && Main_Controller.mediaPlayer.getMedia() != null) {
                    String currentlyPlayingFilePath = Main_Controller.mediaPlayer.getMedia().getSource();
                    System.out.println(currentlyPlayingFilePath);
                    if (currentlyPlayingFilePath.equals(filePath)) {
                        // Stop playing the current media
                        Main_Controller.mediaPlayer.stop();
                    }
                }

                // Delete the song from the database
                songCollection.deleteOne(filter);
                // Remove the deleted song from the ComboBox
                songComboBox.getItems().remove(selectedSong);

                // Delete the local file
                try {
                    URI uri = new URI(filePath);
                    File songFile = new File(uri);
                    if (songFile.exists() && !songFile.delete()) {
                        System.err.println("Failed to delete file: " + songFile.getAbsolutePath());
                    }
                } catch (URISyntaxException e) {
                    System.err.println("Failed to parse file URI: " + filePath);
                }
            }
        }

    }
}