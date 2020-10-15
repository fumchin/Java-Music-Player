
/* use this class to implement equalizer
first: get the signal form playController
second: do fft after pressing apply, and pass back to playcontroller in order to preview  */
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

public class TenEQController extends FFTImplement {

    @FXML
    private Slider sl31;
    @FXML
    private Slider sl62;
    @FXML
    private Slider sl125;
    @FXML
    private Slider sl250;
    @FXML
    private Slider sl500;
    @FXML
    private Slider sl1k;
    @FXML
    private Slider sl2k;
    @FXML
    private Slider sl4k;
    @FXML
    private Slider sl8k;
    @FXML
    private Slider sl16k;
    @FXML
    private TextField tf31;
    @FXML
    private TextField tf125;
    @FXML
    private TextField tf62;
    @FXML
    private TextField tf250;
    @FXML
    private TextField tf500;
    @FXML
    private TextField tf1k;
    @FXML
    private TextField tf2k;
    @FXML
    private TextField tf4k;
    @FXML
    private TextField tf8k;
    @FXML
    private TextField tf16k;

    @FXML
    private Button btnReset;

    @FXML
    private ComboBox styleComboBox;
    ObservableList<String> styleList = FXCollections.observableArrayList("Low Pass", "High Pass", "Rock");

    private int band31 = 31;
    private int band62 = 62;
    private int band125 = 125;
    private int band250 = 250;
    private int band500 = 500;
    private int band1k = 1000;
    private int band2k = 2000;
    private int band4k = 4000;
    private int band8k = 8000;
    private int band16k = 16000;

    private double dBGain31 = 0;
    private double dBGain62 = 0;
    private double dBGain125 = 0;
    private double dBGain250 = 0;
    private double dBGain500 = 0;
    private double dBGain1k = 0;
    private double dBGain2k = 0;
    private double dBGain4k = 0;
    private double dBGain8k = 0;
    private double dBGain16k = 0;

    // private ArrayList<Double>[] signal_modify; // use to modify signal
    private ArrayList<Double>[] signal_EQ_save; // for save origin signal,restore later
    private ArrayList<Double>[] temp;
    private PlayerController PlayerController;
    /*
     * we change sampleNum corresponding to size of signal_modify[] that pass in,
     * try to cover whole signal_modify[].size()
     */

    public TenEQController() {
        super((int) Math.pow(2, 16));
    }

    /*
     * this function is used to apply user's EQ parameters on signal_modify and pass
     * it back to PlayerController
     */
    @FXML
    void applyButtonPressed(ActionEvent event) throws Exception {
        // make a frequency map

        // double[] fm = makeFrequencyMap();
        signal_modify = signal_EQ_save;
        int length = signal_modify[0].size();

        temp = new ArrayList[signal_modify.length];
        for (int channel = 0; channel < signal_modify.length; channel++) {
            temp[channel] = new ArrayList<Double>();
        }

        fft_signal_arr = new Complex[signal_modify[0].size()][];
        part_signal_arr = new Complex[signal_modify[0].size()][];
        // clone signal arraylist to another list (don't change original one)

        /* find thd gain in each frequency band */
        dBGain31 = Math.pow(10, (double) sl31.getValue() / 10.0);
        dBGain62 = Math.pow(10, (double) sl62.getValue() / 10.0);
        dBGain125 = Math.pow(10, (double) sl125.getValue() / 10.0);
        dBGain250 = Math.pow(10, (double) sl250.getValue() / 10.0);
        dBGain500 = Math.pow(10, (double) sl500.getValue() / 10.0);
        dBGain1k = Math.pow(10, (double) sl1k.getValue() / 10.0);
        dBGain2k = Math.pow(10, (double) sl2k.getValue() / 10.0);
        dBGain4k = Math.pow(10, (double) sl4k.getValue() / 10.0);
        dBGain8k = Math.pow(10, (double) sl8k.getValue() / 10.0);
        dBGain16k = Math.pow(10, (double) sl16k.getValue() / 10.0);

        /* start to do some adjustments */
        int count = 0;
        double time = 0;
        while (length > WavFile.getSampleRate()) {
            // find sampleRate
            int pow = 0;
            while (Math.pow(2, pow) < length) {
                pow++;
            }
            pow--;
            sampleNum = (int) Math.pow(2, pow);
            // System.out.println(x);
            for (int row = 0; row < signal_modify.length; row++) {
                part_signal_arr[row] = new Complex[sampleNum];
                fft_signal_arr[row] = new Complex[sampleNum];
            }
            // for (count = 0; count < signal_modify[0].size() - sampleNum; count +=
            // sampleNum / 3) {

            // put arraylist into complex 2d array
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < signal_modify.length; row++) {
                    part_signal_arr[row][col] = new Complex(signal_modify[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            // fft -> filter ->ifft
            // fft
            for (int row = 0; row < signal_modify.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // filter
            for (int col_filter = 0; col_filter < sampleNum; col_filter++) {
                for (int row_filter = 0; row_filter < signal_modify.length; row_filter++) {
                    double fre = ((double) (col_filter) * (double) WavFile.getSampleRate() / sampleNum);
                    if (col_filter >= sampleNum / 2) {
                        fre = ((double) (sampleNum - col_filter) * (double) WavFile.getSampleRate() / sampleNum);
                    }
                    /* ============================== */
                    /* modify signal by frquency here */
                    /* ============================== */
                    double k = 0;
                    if (fre < band31) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    } else if ((fre > band31) && (fre < band62)) {
                        k = ((dBGain62 - dBGain31) / (band62 - band31)) * (fre - band31) + dBGain31;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band62) && (fre < band125)) {
                        k = ((dBGain125 - dBGain62) / (band125 - band62)) * (fre - band62) + dBGain62;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band125) && (fre < band250)) {
                        k = ((dBGain250 - dBGain125) / (band250 - band125)) * (fre - band125) + dBGain125;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band250) && (fre < band500)) {
                        k = ((dBGain500 - dBGain250) / (band500 - band250)) * (fre - band250) + dBGain250;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band500) && (fre < band1k)) {
                        k = ((dBGain1k - dBGain500) / (band1k - band500)) * (fre - band500) + dBGain500;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band1k) && (fre < band2k)) {
                        k = ((dBGain2k - dBGain1k) / (band2k - band1k)) * (fre - band1k) + dBGain1k;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band2k) && (fre < band4k)) {
                        k = ((dBGain4k - dBGain2k) / (band4k - band2k)) * (fre - band2k) + dBGain2k;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band4k) && (fre < band8k)) {
                        k = ((dBGain8k - dBGain4k) / (band8k - band4k)) * (fre - band4k) + dBGain4k;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band8k) && (fre < band16k)) {
                        k = ((dBGain16k - dBGain8k) / (band16k - band8k)) * (fre - band8k) + dBGain8k;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band16k)) {
                        k = dBGain16k;
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    }
                    // System.out.println(k);

                }
            }
            // ifft
            for (int row = 0; row < signal_modify.length; row++) {
                part_signal_arr[row] = FFT.ifft(fft_signal_arr[row]);
            }

            // add into signal modify
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < signal_modify.length; row++) {
                    // if (col != (sampleNum - 2) / 2) {
                    temp[row].add(part_signal_arr[row][col].re());
                    // }
                }
            }
            count += sampleNum;
            length -= sampleNum;
        }
        signal_modify = temp;
        PlayerController.callbackSignal(signal_modify);

    }

    @FXML
    public void btnResetClick(ActionEvent event) {
        sl31.setValue(0);
        sl62.setValue(0);
        sl125.setValue(0);
        sl250.setValue(0);
        sl500.setValue(0);
        sl1k.setValue(0);
        sl2k.setValue(0);
        sl4k.setValue(0);
        sl8k.setValue(0);
        sl16k.setValue(0);
        PlayerController.callbackSignal(signal_EQ_save);

    }

    /* this function is used to make frequency map for signal_ */
    /* this function is used to catch reference that passed from PlayerController */
    public void passSignal(PlayerController PlayerController, ArrayList<Double>[] input) {
        this.signal_modify = input;
        this.PlayerController = PlayerController;
        // store original signal in signal_EQ_save
        try {
            signal_EQ_save = new ArrayList[signal_modify.length];
            for (int channel = 0; channel < signal_modify.length; channel++) {
                signal_EQ_save[channel] = new ArrayList(signal_modify[channel]);
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }

    }

    /* inititalize */
    public void initialize() {
        // 31Hz band slider and textfield binding
        tf31.textProperty().bind(sl31.valueProperty().asString("%.0f"));

        // 62Hz band slider and textfield binding
        tf62.textProperty().bind(sl62.valueProperty().asString("%.0f"));

        // 125Hz band slider and textfield binding
        tf125.textProperty().bind(sl125.valueProperty().asString("%.0f"));

        // 250Hz band slider and textfield binding
        tf250.textProperty().bind(sl250.valueProperty().asString("%.0f"));

        // 500Hz band slider and textfield binding
        tf500.textProperty().bind(sl500.valueProperty().asString("%.0f"));

        // 1kHz band slider and textfield binding
        tf1k.textProperty().bind(sl1k.valueProperty().asString("%.0f"));

        // 2kHz band slider and textfield binding
        tf2k.textProperty().bind(sl2k.valueProperty().asString("%.0f"));

        // 4kHz band slider and textfield binding
        tf4k.textProperty().bind(sl4k.valueProperty().asString("%.0f"));

        // 8kHz band slider and textfield binding
        tf8k.textProperty().bind(sl8k.valueProperty().asString("%.0f"));

        // 16kHz band slider and textfield binding
        tf16k.textProperty().bind(sl16k.valueProperty().asString("%.0f"));
        // get signal data
        // signal_modify = TenEQ.getSignal();
        styleComboBox.setItems(styleList);
        styleComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                EQ eq = new EQ();
                if (t1.equals("Low Pass")) {
                    // copy
                    sl31.setValue(0);
                    sl62.setValue(0);
                    sl125.setValue(0);
                    sl250.setValue(-100);
                    sl500.setValue(-100);
                    sl1k.setValue(-100);
                    sl2k.setValue(-100);
                    sl4k.setValue(-100);
                    sl8k.setValue(-100);
                    sl16k.setValue(-100);
                } else if (t1.equals("High Pass")) {
                    sl31.setValue(-100);
                    sl62.setValue(-100);
                    sl125.setValue(-100);
                    sl250.setValue(-100);
                    sl500.setValue(-100);
                    sl1k.setValue(-100);
                    sl2k.setValue(-100);
                    sl4k.setValue(0);
                    sl8k.setValue(0);
                    sl16k.setValue(0);
                } else if (t1.equals("Rock")) {
                    sl31.setValue(0);
                    sl62.setValue(2);
                    sl125.setValue(5);
                    sl250.setValue(0);
                    sl500.setValue(-2);
                    sl1k.setValue(2);
                    sl2k.setValue(-2);
                    sl4k.setValue(5);
                    sl8k.setValue(4);
                    sl16k.setValue(4);
                }

            }
        });

    }

    public void start(Stage primaryStage) {
    }

}