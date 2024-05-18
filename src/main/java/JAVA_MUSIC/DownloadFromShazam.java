package JAVA_MUSIC;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class DownloadFromShazam {
    @FXML
    public TextField songSearchText;

    public static final String APIKEY = "b15c0784e0msh02700e180ecefc5p19cc28jsn8a35d86e826f";
    private static final String MONGO_CONNECTION = "mongodb+srv://shihabnewaz563:ACunity65@data.4wtqusn.mongodb.net/?retryWrites=true&w=majority&appName=Data";
    private static final String DATABASE_NAME = "MusicApp";
    private static final String COLLECTION_NAME = "musicInfo";

    private final MongoClient mongoClient = MongoClients.create(MONGO_CONNECTION);
    private final MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
    private final MongoCollection<Document> songCollection = database.getCollection(COLLECTION_NAME);

    public void callShazamAPI() throws IOException, InterruptedException, URISyntaxException {
        String song = songSearchText.getText();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://shazam.p.rapidapi.com/search?term=" + song + "&locale=en-US&offset=0&limit=5"))
                .header("x-rapidapi-key", APIKEY)
                .header("x-rapidapi-host", "shazam.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        songDownload(response);
    }

    public void songDownload(HttpResponse<String> response) throws URISyntaxException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
        JsonObject tracks = jsonObject.getAsJsonObject("tracks");
        JsonArray hits = tracks.getAsJsonArray("hits");

        // Iterate through hits array
        for (JsonElement hitElement : hits) {
            JsonObject hit = hitElement.getAsJsonObject();
            JsonObject track = hit.getAsJsonObject("track");
            JsonObject hub = track.getAsJsonObject("hub");
            String title = track.get("title").getAsString();
            String artist = track.get("subtitle").getAsString();
            JsonObject share = track.getAsJsonObject("share");
            String imageString = share.get("image").getAsString();
            URI imageURI = new URI(imageString);
            URL imageURL = imageURI.toURL();
            JsonArray actions = hub.getAsJsonArray("actions");

            // Iterate through actions to find the one with type "uri"
            for (JsonElement actionElement : actions) {
                JsonObject action = actionElement.getAsJsonObject();
                if (action.get("type").getAsString().equals("uri")) {
                    String uriString = action.get("uri").getAsString();
                    URI uri = new URI(uriString);
                    URL fileURL = uri.toURL();
                    String musicDirName = "Music_Downloads" + File.separator;
                    String imageDirName = "Image_Downloads" + File.separator;
                    String fileName = title + ".m4a";
                    String imageName = title + ".jpg";
                    downloadFile(fileURL, musicDirName + fileName);
                    downloadFile(imageURL, imageDirName + imageName);

                    // Store song metadata and file reference in MongoDB
                    Document songMetadata = new Document("title", title)
                            .append("artist", artist)
                            .append("filePath", musicDirName + fileName);
                    songCollection.insertOne(songMetadata);

                    return;
                }
            }
        }
    }

    public static void downloadFile(URL url, String fileName) throws IOException {
        FileUtils.copyURLToFile(url, new File(fileName));
    }

    public void songSearch(ActionEvent actionEvent) throws IOException, URISyntaxException, InterruptedException {
        callShazamAPI();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Download Complete");
        alert.setHeaderText(null);
        alert.setContentText("The file has been downloaded successfully.");
        alert.showAndWait();
    }
}
