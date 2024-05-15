package JAVA_MUSIC;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Main_Controller implements Initializable {
    private final int[] speedArray = {25, 50, 75, 100, 125, 150, 200};
    public ImageView playerButton;
    public Button nextButton;
    public ImageView playButtons;
    public Button previousButton;
    public Button resetButton;
    public ImageView player_Button;
    public Button pauseButton;
    public ImageView playerButtons;
    public Button playButton;
    public Button createPlaylist;
    @FXML
    private Label songLabel;
    @FXML
    private ComboBox<String> speedComboBox;
    @FXML
    private Slider seekSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ImageView albumImageView;
    @FXML
    private AnchorPane pane;

    private MediaPlayer mediaPlayer;
    private ArrayList<File> songList;
    private int songNumber;
    private Timer timer;
    private boolean running;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int j : speedArray) {
            speedComboBox.getItems().add(Integer.toString(j));
        }
        speedComboBox.setOnAction(this::changeSpeed);
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.setVolume(volumeSlider.getValue() * 0.001));

        speedComboBox.setLayoutX(pane.getWidth() - 528); // Adjust as needed
        volumeSlider.setLayoutX(pane.getWidth() - 402); // Adjust as needed
        seekSlider.prefWidthProperty().bind(pane.widthProperty().subtract(20)); // Adjust as needed
        // Add listener for AnchorPane width changes
        pane.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Adjust layout when width changes
            speedComboBox.setLayoutX(newVal.doubleValue() - 528); // Adjust as needed
            volumeSlider.setLayoutX(newVal.doubleValue() - 402); // Adjust as needed
        });

    }

    public ArrayList<File> createPlaylist(ActionEvent actionEvent) throws IOException {
        songList = new ArrayList<>();
        File directory = new File("Music_Downloads");
        File[] files = directory.listFiles();
        if (files != null) {
            Collections.addAll(songList, files);
        }

        return songList;
    }

    public void playMedia() {
        beginTimer();
        changeSpeed(null);
        mediaPlayer.play();
    }

    public void pauseMedia() {
        cancelTimer();
        mediaPlayer.pause();
    }

    public void resetMedia() {
        seekSlider.setValue(0);
        mediaPlayer.seek(Duration.ZERO);
    }

    public void nextMedia() {
        if (songNumber < songList.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        stopThenPlay();
    }

    private void stopThenPlay() {
        mediaPlayer.stop();
        if (running) {
            cancelTimer();
        }
        Media media = new Media(songList.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songList.get(songNumber).getName());
        playMedia();
    }

    public void previousMedia() {
        if (songNumber > 0) {
            songNumber--;
        } else {
            songNumber = songList.size() - 1;
        }
        stopThenPlay();
    }

    public void changeSpeed(ActionEvent Event) {
        if (speedComboBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Integer.parseInt(speedComboBox.getValue()) * 0.01);
        }
    }

    public void beginTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                running = true;
                double currentTime = mediaPlayer.getCurrentTime().toMillis();
                double endTime = mediaPlayer.getTotalDuration().toMillis();
                seekSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                mediaPlayer.currentTimeProperty().addListener((Value, duration, time) -> seekSlider.setValue(time.toSeconds()));
                seekSlider.setOnMouseClicked(mouseEvent -> mediaPlayer.seek(Duration.seconds(seekSlider.getValue())));

                if (currentTime / endTime == 1) {
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 100);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }


    @FXML
    void TopChart(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SearchByDate.fxml"));
        Stage stage4 = new Stage();
        Parent root = loader.load();
        stage4.setTitle("TOP CHARTS");
        stage4.setScene(new Scene(root));
        stage4.show();

    }

    public void songDownload(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SongDownloader.fxml"));
        Stage stage4 = new Stage();
        Parent root = loader.load();
        stage4.setTitle("Song Download With Url");
        stage4.setScene(new Scene(root));
        stage4.show();
    }

    public void songDownloadWithSearch(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SearchOption.fxml"));
        Stage stage9 = new Stage();
        Parent root = loader.load();
        stage9.setScene(new Scene(root));
        stage9.show();
    }
}
