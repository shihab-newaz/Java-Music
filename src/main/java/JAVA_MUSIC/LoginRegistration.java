package JAVA_MUSIC;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginRegistration {
    public static final String MONGO_CONNECTION = "mongodb+srv://shihabnewaz:password321@cluster0.96q0n.mongodb.net/UserDATA?retryWrites=true&w=majority";
    @FXML
    public TextField fullName;
    @FXML
    public TextField signUpEmail;
    @FXML
    public Button submitButton;
    @FXML
    public PasswordField Registration_Password;
    @FXML
    public TextField userTextField;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button SignInButton;
    MongoClient mongoClient = MongoClients.create(MONGO_CONNECTION);
    MongoCollection<Document> USER_DATA = mongoClient.getDatabase("UserDATA").getCollection("registration");
    List<Document> userData = USER_DATA.find().into(new ArrayList<>());


    public void user_register(ActionEvent actionEvent) throws IOException {
        Stage stage2 = new Stage();
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/register.fxml")));
        stage2.setScene(new Scene(parent));
        stage2.show();
    }

    public void signInButton(ActionEvent actionEvent) throws IOException {
        boolean correct = false;
        for (Document document : userData) {

            if (userTextField.getText().equals(document.getString("username"))) {
                correct = isCorrect(correct, document);

            } else if (userTextField.getText().equals(document.getString("email"))) {
                correct = isCorrect(correct, document);
            }
            if (!correct) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.show();
            }
        }
    }

    private boolean isCorrect(boolean correct, Document document) throws IOException {
        if (passwordTextField.getText().equals(document.getString("password"))) {
            Stage stage3 = new Stage();
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainPlayerUI.fxml")));
            stage3.setScene(new Scene(parent));
            stage3.show();
            correct = true;
            Main.stage1.close();
        }
        return correct;
    }


    public void submit(ActionEvent actionEvent) throws IOException {
        boolean bool1 = true, bool2 = true;
        for (Document document : userData) {
            if (document.getString("username") != null) {

                if (document.getString("username").equals(fullName.getText())) {
                    Alert alert1 = new Alert(Alert.AlertType.WARNING);
                    bool1 = false;
                    alert1.show();
                    break;
                }
            }
            for (Document document1 : userData) {
                if (document1.getString("email") != null) {

                    if (document1.getString("email").equals(fullName.getText())) {
                        Alert alert2 = new Alert(Alert.AlertType.WARNING);
                        bool2 = false;
                        alert2.show();
                        break;
                    }
                }
            }
        }
        if (bool1 && bool2) {
            Document ADD_SIGN_UP_DATA = new Document("username", fullName.getText()).append("email", signUpEmail.getText()).
                    append("password", Registration_Password.getText());
            USER_DATA.insertOne(ADD_SIGN_UP_DATA);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.show();

        }
    }

}

