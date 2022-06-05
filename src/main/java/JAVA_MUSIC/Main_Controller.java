package JAVA_MUSIC;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    @FXML
    private Label songLabel;
    @FXML
    private ComboBox<String> speedComboBox;
    @FXML
    private Slider seekSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Media media;

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
    }

    public ArrayList<File> createPlaylist(ActionEvent actionEvent) throws IOException {
        songList = new ArrayList<>();
        File directory = new File("I:\\Project\\Download\\src\\main\\resources\\Music");
        File[] files = directory.listFiles();
        if (files != null) {
            Collections.addAll(songList, files);
        }
        media = new Media(songList.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songList.get(songNumber).getName());
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
        mediaPlayer.stop();
        if (running) {
            cancelTimer();
        }
        media = new Media(songList.get(songNumber).toURI().toString());
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
        mediaPlayer.stop();
        if (running) {
            cancelTimer();
        }
        media = new Media(songList.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songList.get(songNumber).getName());
        playMedia();
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

    public void loadDatabase(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DB_UI.fxml"));
        Stage stage3 = new Stage();
        Parent root = loader.load();
        DB_controller controller = loader.getController();
        controller.listArtists();

        stage3.setTitle("MUSIC DATABASE");
        stage3.setScene(new Scene(root));
        stage3.show();
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
