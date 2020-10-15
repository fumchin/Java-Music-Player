import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class Player extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("player.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("player"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();

        // PlayerController PC = new PlayerController();

        // stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        // @Override
        // public void handle(WindowEvent event) {
        // PC.PlayStop();
        // }
        // });
    }

    public static void main(String[] args) {
        launch(args);
    }
}