import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.*;
import javafx.scene.input.MouseEvent;

public class playercontroller {

    @FXML
    private Slider slTime;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnPlay;
    @FXML
    private Slider slVolume;
    @FXML
    private Label lbVolume;
    @FXML
    private Button btnOpen;
    @FXML
    private Label lbCurrentTime;
    @FXML
    private Slider slSpeed;
    @FXML
    private Label lbSpeed;
    @FXML
    private MediaView mView;
    @FXML
    private Pane pane;
    @FXML
    private Canvas waveformCanvas1;
    @FXML
    private Canvas waveformCanvas2;
    @FXML
    private Canvas timeLineCanvas1;
    @FXML
    private Canvas timeLineCanvas2;

    private Double endTime = new Double(0);
    private Double currentTime = new Double(0);
    private java.io.File file = new java.io.File("init.mp3");
    private Media media = new Media(file.toURI().toString());
    private MediaPlayer mplayer = new MediaPlayer(media);
    FileChooser fileChooser = new FileChooser();

    // wavfile
    private WavFile wf;
    private ArrayList<Double>[] signal;
    private ArrayList<Double>[] signal_modify;
    private ArrayList<Double>[] signal_temp;
    // some useful signal properties
    private int sampleRate;
    private int interval;

    public void start(Stage primarytStage) {

        mView.fitWidthProperty().bind(pane.widthProperty());
        mView.fitHeightProperty().bind(pane.heightProperty());

        mplayer.setOnEndOfMedia(() -> {
            mplayer.stop();
            btnPlay.setText("Play");
        });

        fileChooser.setTitle("Open Media...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP4 Video", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3 Music", "*.mp3"),
                new FileChooser.ExtensionFilter("WMV Music", "*.wmv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    int vol = 50;
    double speed = 1;

    public void initialize() {
        slVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                vol = newValue.intValue();
                signal = wf.getSignal();
                signal_temp = new ArrayList[signal.length];
                for (int channel = 0; channel < signal.length; channel++) {
                    signal_temp[channel] = new ArrayList(signal[channel]);
                    for (int x = 0; x < signal_temp[channel].size(); x++) {
                        signal_temp[channel].set(x, signal_temp[channel].get(x) * ((double) vol / 50));
                    }
                }
                drawWaveform(signal_temp);
                lbVolume.setText(String.valueOf(vol));
            }
        });

        slSpeed.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                speed = newValue.doubleValue();
                speed = Double.parseDouble(String.format("%.2f", speed));
                lbSpeed.setText(String.valueOf(speed));
            }
        });
    }

    @FXML
    void PlayClick(ActionEvent event) {
        if (btnPlay.getText().equals("Play")) {
            btnPlay.setText("Pause");
            mplayer.play();
        } else {
            btnPlay.setText("Play");
            mplayer.pause();
        }
    }

    @FXML
    void StopClick(ActionEvent event) {
        mplayer.stop();
        btnPlay.setText("Play");
    }

    @FXML
    void btnOpenClick(ActionEvent event) throws IOException {
        double sp = slSpeed.getValue();
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            mplayer.stop();
            btnPlay.setText("Pause");
            media = new Media(file.toURI().toString());
            mplayer = new MediaPlayer(media);
            mView.setMediaPlayer(mplayer);
            mplayer.setOnReady(() -> {
                endTime = mplayer.getStopTime().toSeconds();
            });
            mplayer.setOnEndOfMedia(() -> {
                mplayer.stop();
                mplayer.seek(Duration.ZERO);
                btnPlay.setText("Play");
            });
            mplayer.currentTimeProperty().addListener(ov -> {
                currentTime = mplayer.getCurrentTime().toSeconds();
                lbCurrentTime.setText(Seconds2Str(currentTime) + "/" + Seconds2Str(endTime));
                // draw current time line
                drawCurrentTimeLine(currentTime);
                slTime.setValue(currentTime / endTime * 100);
            });
            slTime.valueProperty().addListener(ov -> {
                if (slTime.isValueChanging()) {
                    mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));
                }
            });
            mplayer.volumeProperty().bind(slVolume.valueProperty().divide(100));
            mplayer.setRate(1);
            slSpeed.valueProperty().addListener(ov -> {
                if (slSpeed.isValueChanging()) {
                    mplayer.setRate(slSpeed.getValue());
                }
            });

            // draw waveform

            wf = new WavFile();
            wf.read(file.getAbsolutePath());
            signal = wf.getSignal();
            interval = signal[0].size() / (int) waveformCanvas1.getWidth();
            sampleRate = wf.getSampleRate();
            drawWaveform(signal);
            mplayer.play();

        }
    }

    @FXML
    void timeLine1MousePressed(MouseEvent event) {
        double x = event.getX();
        // find the time correspond to the x
        double timeClick = (x * interval) / wf.getSampleRate();
        slTime.setValue(timeClick / endTime * 100);
        drawCurrentTimeLine(timeClick);
        mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));

    }

    @FXML
    void timeLine2MousePressed(MouseEvent event) {
        double x = event.getX();
        // find the time correspond to the x
        double timeClick = (x * interval) / wf.getSampleRate();
        slTime.setValue(timeClick / endTime * 100);
        drawCurrentTimeLine(timeClick);
        mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));

    }

    private String Seconds2Str(Double seconds) {
        Integer count = seconds.intValue();
        Integer Hours = count / 3600;
        count = count % 3600;
        Integer Minutes = count / 60;
        count = count % 60;
        String str = Hours.toString() + ":" + Minutes.toString() + ":" + count.toString();
        return str;
    }

    private void drawWaveform(ArrayList<Double>[] input) {
        // clear
        GraphicsContext gc1 = waveformCanvas1.getGraphicsContext2D();
        GraphicsContext gc2 = waveformCanvas2.getGraphicsContext2D();
        gc1.clearRect(0, 0, waveformCanvas1.getWidth(), waveformCanvas1.getHeight());
        gc2.clearRect(0, 0, waveformCanvas2.getWidth(), waveformCanvas2.getHeight());
        int max = 100;
        int y_base = (int) waveformCanvas1.getHeight() / 2;
        gc1.strokeLine(0, y_base, waveformCanvas1.getWidth(), y_base);
        gc1.strokeRect(0, 0, waveformCanvas1.getWidth(), waveformCanvas1.getHeight());
        gc2.strokeRect(0, 0, waveformCanvas2.getWidth(), waveformCanvas2.getHeight());
        gc2.strokeLine(0, y_base, waveformCanvas2.getWidth(), y_base);
        for (int x = 0; x < waveformCanvas1.getWidth(); x++) {
            for (int channel = 0; channel < input.length; channel++) {
                if (channel % 2 == 0) {
                    gc1.strokeLine(x, y_base - (int) (input[channel].get(x * interval) * max), x + 1,
                            y_base - (int) (input[channel].get((x + 1) * interval) * max));
                } else if (channel % 2 != 0) {
                    gc2.strokeLine(x, y_base - (int) (input[channel].get(x * interval) * max), x + 1,
                            y_base - (int) (input[channel].get((x + 1) * interval) * max));
                }
            }
        }
    }

    private void drawCurrentTimeLine(double time) {
        GraphicsContext gc1 = timeLineCanvas1.getGraphicsContext2D();
        GraphicsContext gc2 = timeLineCanvas2.getGraphicsContext2D();
        gc1.clearRect(0, 0, timeLineCanvas1.getWidth(), timeLineCanvas1.getHeight());
        gc2.clearRect(0, 0, timeLineCanvas2.getWidth(), timeLineCanvas2.getHeight());
        // static double lastTime;
        int sampleRate = wf.getSampleRate();
        int interval = signal[0].size() / (int) waveformCanvas1.getWidth();
        double x = ((double) sampleRate * time) / (double) interval;
        gc1.strokeLine(x, 0, x, timeLineCanvas1.getHeight());
        gc2.strokeLine(x, 0, x, timeLineCanvas2.getHeight());

        // lastTime = time;
    }

}
