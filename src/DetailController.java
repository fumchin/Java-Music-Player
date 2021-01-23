import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ContentDisplay;

public class DetailController {
    @FXML
    private Label detailLabel;

    private PlayerController playerController;
    private WavFile wavFileInfo = new WavFile();

    public void showInfo() {
        detailLabel.setContentDisplay(ContentDisplay.TOP);
        detailLabel.setText("File name:\t" + wavFileInfo.getFileInfo());
        detailLabel.setFont(new Font("Verdana", 15));
    }

    public void passWavInfo(PlayerController playerController, WavFile wavFileInfo){
        this.playerController = playerController;
        this.wavFileInfo  = wavFileInfo;
    }

}