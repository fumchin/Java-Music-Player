import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RecordController {

    TargetDataLine targetDataLine;
    AudioFormat audioFormat;
    Stage stage = new Stage();

    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;
    @FXML
    private Label label;
    @FXML
    private TextField tfName;

    String name;

    @FXML
    void StartClick(ActionEvent event) {
        label.setText("Recording...");
        btnStart.setText("Recording");
        captureAudio();
    }

    @FXML
    void StopClick(ActionEvent event) throws IOException {
        label.setText("Press 'start' to start recording");
        btnStart.setText("start");
        closeCaptureAudio();
    }

    public void closeCaptureAudio() throws IOException {
        targetDataLine.stop();
        targetDataLine.close();
    }

    public void captureAudio() {
        try {

            audioFormat = getAudioFormat();
            name = tfName.getText();

            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

            new CaptureThread().start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private AudioFormat getAudioFormat() {

        float sampleRate = 44000F;

        int sampleSizeInBits = 16;

        int channels = 2;
        // true,false
        boolean signed = true;

        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    class CaptureThread extends Thread {
        public void run() {

            AudioFileFormat.Type fileType = null;

            File audioFile = null;
            fileType = AudioFileFormat.Type.WAVE;
            audioFile = new File(name + ".wav");
            try {

                targetDataLine.open(audioFormat);

                targetDataLine.start();

                AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
