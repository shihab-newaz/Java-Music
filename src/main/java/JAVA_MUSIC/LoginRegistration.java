package JAVA_MUSIC;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import javafx.concurrent.Task;
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
import java.util.Objects;

public class LoginRegistration {
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

    private static final String MONGO_CONNECTION = "mongodb+srv://shihabnewaz563:ACunity65@data.4wtqusn.mongodb.net/?retryWrites=true&w=majority&appName=Data";
    private final MongoClient mongoClient = MongoClients.create(MONGO_CONNECTION);
    private final String databaseName = "UserDATA";
    private final String collectionName = "registration";
    private final MongoCollection<Document> USER_DATA = mongoClient.getDatabase(databaseName).getCollection(collectionName);

    public void user_registration(ActionEvent actionEvent) throws IOException {
        Stage stage2 = new Stage();
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/register.fxml")));
        stage2.setScene(new Scene(parent));
        stage2.show();
    }

    public void signIn(ActionEvent actionEvent) {
        Task<Boolean> authTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String userOrEmail = userTextField.getText();
                String password = passwordTextField.getText();
                return authenticateUser(userOrEmail, password);
            }
        };

        authTask.setOnSucceeded(event -> {
            if (authTask.getValue()) {
                showMainPlayerUI();
            } else {
                showAlert("Invalid username or password", Alert.AlertType.WARNING);
            }
        });

        authTask.setOnFailed(event -> {
            showAlert("Authentication failed", Alert.AlertType.ERROR);
        });

        new Thread(authTask).start();
    }

    private boolean authenticateUser(String userOrEmail, String password) {
        Document userDocument = USER_DATA.find(Filters.or(Filters.eq("username", userOrEmail), Filters.eq("email", userOrEmail))).first();
        return userDocument != null && password.equals(userDocument.getString("password"));
    }

    private void showMainPlayerUI() {
        try {
            Stage stage3 = new Stage();
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainPlayerUI.fxml")));
            stage3.setScene(new Scene(parent));
            stage3.setMinWidth(1024);
            stage3.setMinHeight(250);
            stage3.setMaxWidth(1200);
            stage3.setMaxHeight(350);
            stage3.show();
            Main.stage1.close();
        } catch (IOException e) {
            showAlert("Failed to load the main player UI", Alert.AlertType.ERROR);
        }
    }

    public void signUp(ActionEvent actionEvent) {
        Task<Void> registerTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String username = fullName.getText();
                String email = signUpEmail.getText();
                String password = Registration_Password.getText();

                if (isUsernameTaken(username) || isEmailTaken(email)) {
                    showAlert("Username or email already taken", Alert.AlertType.WARNING);
                    return null;
                }

                Document newUser = new Document("username", username).append("email", email).append("password", password);

                USER_DATA.insertOne(newUser);
                return null;
            }
        };

        registerTask.setOnSucceeded(event -> {
            showAlert("Registration successful", Alert.AlertType.INFORMATION);
        });

        registerTask.setOnFailed(event -> {
            showAlert("Registration failed", Alert.AlertType.ERROR);
        });

        new Thread(registerTask).start();
    }

    private boolean isUsernameTaken(String username) {
        return USER_DATA.countDocuments(Filters.eq("username", username)) > 0;
    }

    private boolean isEmailTaken(String email) {
        return USER_DATA.countDocuments(Filters.eq("email", email)) > 0;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}