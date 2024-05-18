package JAVA_MUSIC;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Main_Controller implements Initializable {
    private final int[] speedArray = {25, 50, 75, 100, 125, 150, 200};

    @FXML
    private Button refresh;
    @FXML
    private Label songLabel;
    @FXML
    private ComboBox<String> speedComboBox;
    @FXML
    private Slider seekSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private AnchorPane pane;

    public static MediaPlayer mediaPlayer;
    private int songNumber = 0;
    public ArrayList<File> songList = new ArrayList<>();

    private static final String MONGO_CONNECTION = "mongodb+srv://shihabnewaz563:ACunity65@data.4wtqusn.mongodb.net/?retryWrites=true&w=majority&appName=Data";
    private static final String DATABASE_NAME = "MusicApp";
    private static final String COLLECTION_NAME = "musicInfo";

    private static final MongoClient mongoClient = MongoClients.create(MONGO_CONNECTION);
    private static final MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
    public static final MongoCollection<Document> songCollection = database.getCollection(COLLECTION_NAME);

    private Timeline timeline;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int j : speedArray) {
            speedComboBox.getItems().add(Integer.toString(j));
        }
        speedComboBox.setOnAction(this::changeSpeed);
        speedComboBox.setLayoutX(pane.getWidth() - 528);
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.setVolume(volumeSlider.getValue() * 0.001));
        volumeSlider.setLayoutX(pane.getWidth() - 402);
        seekSlider.prefWidthProperty().bind(pane.widthProperty().subtract(20));

        seekSlider.setOnMouseClicked(mouseEvent -> {
            double seekPosition = seekSlider.getValue();
            mediaPlayer.pause(); // Pause playback temporarily
            mediaPlayer.seek(Duration.seconds(seekPosition));
            mediaPlayer.play();  // Resume playback after seeking

        });

        // Add listener for AnchorPane width changes
        pane.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Adjust layout when width changes
            speedComboBox.setLayoutX(newVal.doubleValue() - 528); // Adjust as needed
            volumeSlider.setLayoutX(newVal.doubleValue() - 402); // Adjust as needed
        });

        timeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> {
                    double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                    double totalDuration = mediaPlayer.getTotalDuration().toSeconds();
                    seekSlider.setValue(currentTime);

                    if (currentTime >= totalDuration) {
                        timeline.stop();
                        nextMedia();
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        refresh.setOnAction(event -> populateFromDB());
        populateFromDB();
    }

    public void populateFromDB() {
        songList.clear(); // Clear the existing list to avoid duplicates
        List<Document> songDocuments = songCollection.find().projection(Projections.include("filePath")).into(new ArrayList<>());
        // Populate the songList with File objects using the file paths
        for (Document songDoc : songDocuments) {
            String filePath = songDoc.getString("filePath");
            File songFile = new File(filePath);
            songList.add(songFile);
        }
        if (songList.isEmpty()) {
            songLabel.setText("No songs available.");
            // Disable playback controls or provide appropriate feedback to the user
        } else {
            songLabel.setText(songCollection.find().into(new ArrayList<>()).get(0).getString("title") + " by " +
                    songCollection.find().into(new ArrayList<>()).get(0).getString("artist"));
        }
    }

    @FXML
    private void deleteFromDB() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/deleteFromDB.fxml"));
        Stage stage4 = new Stage();
        Parent root = loader.load();
        stage4.setTitle("Delete");
        stage4.setScene(new Scene(root));
        stage4.show();
    }

    @FXML
    private void playMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(new Media(songList.get(songNumber).toURI().toString()));
        }
        timeline.play();
        changeSpeed(null); // Assuming this sets the initial playback speed
        mediaPlayer.play();
    }

    @FXML
    private void pauseMedia() {
        timeline.pause();
        mediaPlayer.pause();
    }

    @FXML
    private void resetMedia() {
        seekSlider.setValue(0);
        timeline.stop();
        mediaPlayer.seek(Duration.ZERO);
    }

    @FXML
    private void nextMedia() {
        if (!songList.isEmpty()) {
            songNumber = (songNumber + 1) % songList.size();
            stopThenPlay();
        }
    }


    @FXML
    private void previousMedia() {
        if (!songList.isEmpty()) {
            songNumber = (songNumber - 1+ songList.size()) % songList.size();
            stopThenPlay();
        }
    }

    private void stopThenPlay() {
        if (!songList.isEmpty() && songNumber < songList.size()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Media media = new Media(songList.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            try {
                List<Document> songDocuments = songCollection.find().projection(Projections.include("title", "artist")).into(new ArrayList<>());
                System.out.println(songNumber);
                if (songNumber < songDocuments.size()) {
                    songLabel.setText(songDocuments.get(songNumber).getString("title") + " by " +
                            songDocuments.get(songNumber).getString("artist"));
                } else {
                    songLabel.setText("Unknown song");
                }
            } catch (Exception e) {
                e.printStackTrace();
                songLabel.setText("Error fetching song details");
            }

            playMedia();
        }
    }

    @FXML
    private void changeSpeed(ActionEvent Event) {
        if (speedComboBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Integer.parseInt(speedComboBox.getValue()) * 0.01);
        }
    }

    @FXML
    private void SearchByDate(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SearchByDate.fxml"));
        Stage stage4 = new Stage();
        Parent root = loader.load();
        stage4.setTitle("TOP CHARTS");
        stage4.setScene(new Scene(root));
        stage4.show();

    }

    @FXML
    private void songDownloadWithSearch(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SearchOption.fxml"));
        Stage stage9 = new Stage();
        Parent root = loader.load();
        stage9.setScene(new Scene(root));
        stage9.show();
    }
}
