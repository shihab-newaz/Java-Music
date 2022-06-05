package JAVA_MUSIC;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SongDownloadWithUrl {
    @FXML
    TextField songUrl;
    @FXML
    TextField songFileName;
    public String fileName, fileUrl;
    public String dirName = "I:\\Project\\Download\\src\\main\\resources\\Music";

    public void songDownloader(ActionEvent actionEvent) {
        try {
            fileName = "//" + songFileName.getText() + ".mp3";
            fileUrl = songUrl.getText();
            SAVE_FILE_WITH_URL(dirName + fileName, fileUrl);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SAVE_FILE_WITH_URL(String fileName, String fileUrl) throws IOException {
        BufferedInputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = new BufferedInputStream(new URL(fileUrl).openStream());
            fileOutputStream = new FileOutputStream(fileName);
            byte data[] = new byte[1024];
            int count;
            while ((count = inputStream.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, count);
            }
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (fileOutputStream != null)
                fileOutputStream.close();
        }
    }
//        public static void saveFileFromUrlWithCommonsIO (String fileName, String fileUrl) throws
//        MalformedURLException, IOException {
//        FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
}

