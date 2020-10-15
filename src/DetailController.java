import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ContentDisplay;

public class DetailController {
    @FXML
    private Label detailLabel;

    public void initialize() {
        detailLabel.setContentDisplay(ContentDisplay.TOP);
        detailLabel.setText("File name:\t" + WavFile.getFileName());
        detailLabel.setText(detailLabel.getText() + "\nSampleRate:" + WavFile.getSampleRate());
        detailLabel.setText(detailLabel.getText() + "\nChannel numbers:" + WavFile.getNumChannels());
        detailLabel.setText(detailLabel.getText() + "\nBits Per Sample:" + WavFile.getBitsPerSample());
        detailLabel.setFont(new Font("Verdana", 15));

    }

}