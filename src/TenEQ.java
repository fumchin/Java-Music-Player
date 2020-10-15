import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

public class TenEQ extends Application {

    private static ArrayList<Double>[] signal_modify; // use to modify signal

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = FXMLLoader.load(getClass().getResource("tenEQ.fxml"));
        Parent root = loader.load();
        // get TenEQcontroller
        TenEQController tenEQController = loader.getController();
        // tenEQController.getSignal(signal_modify);
        Scene scene = new Scene(root);
        stage.setTitle("EQ"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // private ArrayList<Double>[] signal_EQ_save; // for save origin signal,restore
    // later

    public void setSignal(ArrayList<Double>[] input) {
        signal_modify = input;
    }

    public static ArrayList<Double>[] getSignal() {
        return signal_modify;
    }
}