import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class vedioplayer extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root =
        FXMLLoader.load(getClass().getResource("vedioplayer.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("vedio player"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();

        vediocontroller VC = new vediocontroller();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                VC.PlayStop();
            }
        });   
    }
    public static void main(String[] args) {
        launch(args);
    }
}