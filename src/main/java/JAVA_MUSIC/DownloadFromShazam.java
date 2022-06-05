package JAVA_MUSIC;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class DownloadFromShazam {
    @FXML
    public TextField songSearchText;
    public URL fileURL;

    public void songDownload() throws IOException, InterruptedException, URISyntaxException {
        String fileName = songSearchText.getText() + ".m4a";
        String dirName = "I:\\Project\\Download\\src\\main\\resources\\Music\\";
        String song = songSearchText.getText();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://shazam.p.rapidapi.com/search?term=" + song + "&locale=en-US&offset=0&limit=5"))
                .header("x-rapidapi-key", "b15c0784e0msh02700e180ecefc5p19cc28jsn8a35d86e826f")
                .header("x-rapidapi-host", "shazam.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<String> arrayList = new ArrayList<>();
        String[] a1 = response.body().split("[,:]");

        for (int i = 0; i < a1.length; i++) {
            if (a1[i].equals("\"uri\"")) {
                arrayList.add(a1[i + 2]);
            }
        }

        String file_Url_string = "https:" + arrayList.get(1);
        StringBuffer stringBuffer = new StringBuffer(file_Url_string);
        stringBuffer.delete(file_Url_string.length() - 3, file_Url_string.length());

        URI uri = new URI(stringBuffer.toString());
        fileURL = uri.toURL();
        downloadFile(fileURL, dirName + fileName);
    }

    public static void downloadFile(URL url, String fileName) throws IOException {
        FileUtils.copyURLToFile(url, new File(fileName));
    }

    public void songSearch(ActionEvent actionEvent) throws IOException, URISyntaxException, InterruptedException {
        songDownload();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.show();
    }
}
