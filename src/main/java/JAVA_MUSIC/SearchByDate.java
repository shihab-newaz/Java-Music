package JAVA_MUSIC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;

public class SearchByDate {
    @FXML
    public DatePicker datePicker;
    @FXML
    public ListView<String> songList;

    public ArrayList<String> songValues = new ArrayList<>();
    public LocalDate date;
    public String localDate;

    public void searchByDate(ActionEvent actionEvent) {
        date = datePicker.getValue();
        localDate = date.toString();
        final HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("https://billboard2.p.rapidapi.com/billboard_global_200?date=" + localDate))
                .header("x-rapidapi-key", "b15c0784e0msh02700e180ecefc5p19cc28jsn8a35d86e826f")
                .header("x-rapidapi-host", "billboard2.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
            String[] a1 = response.body().split("[,:/#?%&]");
            for (int i = 0; i < a1.length; i++) {
                if (a1[i].equals("\"title\"")) {
                    songValues.add(a1[i + 1]);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        for (String song : songValues) {
            songList.getItems().add(song);
        }
    }
}




