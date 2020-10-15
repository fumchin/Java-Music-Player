import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.event.ActionEvent;
import java.util.*;

public class FrequencyAnalysisController extends FFTImplement {
    @FXML
    private Canvas canvas1;
    @FXML
    private Button btnAnalysis;

    /* something necessary for signal provessign */
    protected ArrayList<double[][]> fft_signal = new ArrayList<double[][]>();
    protected int index[][]; // WavFileeform
    private PlayerController PlayerController;
    private final double C_2 = 65.41;
    private final double Db_2 = 69.30;
    private final double D_2 = 73.42;
    private final double Eb_2 = 77.78;
    private final double E_2 = 82.41;
    private final double F_2 = 87.31;
    private final double Gb_2 = 92.50;
    private final double G_2 = 98;
    private final double Ab_2 = 103.83;
    private final double A_2 = 110;
    private final double Bb_2 = 116.54;
    private final double B_2 = 123;
    private final double C_3 = 2 * C_2;
    private final double Db_3 = 2 * Db_2;
    private final double D_3 = 2 * D_2;
    private final double Eb_3 = 2 * Eb_2;
    private final double E_3 = 2 * E_2;
    private final double F_3 = 2 * F_2;
    private final double Gb_3 = 2 * Gb_2;
    private final double G_3 = 2 * G_2;
    private final double Ab_3 = 2 * Ab_2;
    private final double A_3 = 2 * A_2;
    private final double Bb_3 = 2 * Bb_2;
    private final double B_3 = 2 * B_2;

    private double C_sum = 0;
    private double Db_sum = 0;
    private double D_sum = 0;
    private double Eb_sum = 0;
    private double E_sum = 0;
    private double F_sum = 0;
    private double Gb_sum = 0;
    private double G_sum = 0;
    private double Ab_sum = 0;
    private double A_sum = 0;
    private double Bb_sum = 0;
    private double B_sum = 0;
    private String rootString;
    private String lastRootString = "";
    private String chordName;

    // self constructor
    public FrequencyAnalysisController() {
        super((int) Math.pow(2, 16));
    }

    // get signal form playerController
    public void passSignal(PlayerController PlayerController, ArrayList<Double>[] input) {
        this.PlayerController = PlayerController;
        // store original signal in signal_EQ_save
        try {
            signal_modify = new ArrayList[input.length];
            for (int channel = 0; channel < input.length; channel++) {
                signal_modify[channel] = new ArrayList(input[channel]);
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    /*
     * return List< location(time) where chord locate and,chor chord string itself>
     */
    public List<Map.Entry<Double, String>> signalAnalysis(ArrayList<Double>[] input) {
        Map<Double, String> result_map = new LinkedHashMap<Double, String>();
        part_signal_arr = new Complex[WavFile.getNumChannels()][sampleNum];
        fft_signal_arr = new Complex[WavFile.getNumChannels()][];
        for (int count = 0; count < input[0].size() - sampleNum; count += sampleNum / 3) {
            // initialize
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < WavFile.getNumChannels(); row++) {
                    part_signal_arr[row][col] = new Complex(input[row].get(count + col), 0);
                }
            }
            // do fft
            for (int row = 0; row < WavFile.getNumChannels(); row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // make map
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            for (int col = 0; col < sampleNum; col++) {
                map.put(col, Math.abs(fft_signal_arr[0][col].re()));
            }
            List<Map.Entry<Integer, Double>> list_Data = new ArrayList<Map.Entry<Integer, Double>>(map.entrySet());
            Collections.sort(list_Data, new Comparator<Map.Entry<Integer, Double>>() {
                public int compare(Map.Entry<Integer, Double> entry1, Map.Entry<Integer, Double> entry2) {
                    if ((Double) entry1.getValue() > (Double) entry2.getValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            Map<Integer, Double> linkedHashMap = new LinkedHashMap<>();
            int k = 0;
            for (Map.Entry e : list_Data) {
                if (k == 6) {
                    // System.out.println();
                    break;
                }
                linkedHashMap.put((Integer) e.getKey(), (Double) e.getValue());
                Integer temp = (Integer) e.getKey();
                if (temp >= sampleNum / 2) {
                    temp = sampleNum - temp;
                }
                // System.out.print(temp * WavFile.getSampleRate() / sampleNum + ": " + (Double)
                // e.getValue() + "\t");
                k++;
            }
            // make another map
            rootString = getRoot(list_Data);
            getScalePower(list_Data);
            Map<String, Double> scaleMap = new HashMap<String, Double>();
            scaleMap.put("C", C_sum);
            scaleMap.put("Db", Db_sum);
            scaleMap.put("D", D_sum);
            scaleMap.put("Eb", Eb_sum);
            scaleMap.put("E", E_sum);
            scaleMap.put("F", F_sum);
            scaleMap.put("Gb", Gb_sum);
            scaleMap.put("G", G_sum);
            scaleMap.put("Ab", Ab_sum);
            scaleMap.put("A", A_sum);
            scaleMap.put("Bb", Bb_sum);
            scaleMap.put("B", B_sum);
            List<Map.Entry<String, Double>> list_scaleData = new ArrayList<Map.Entry<String, Double>>(
                    scaleMap.entrySet());
            Collections.sort(list_scaleData, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                    if ((Double) entry1.getValue() > (Double) entry2.getValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            k = 0;
            Map<String, Double> linkedScaleHashMap = new LinkedHashMap<>();
            List<Map.Entry<String, Double>> sorted_list = new ArrayList<Map.Entry<String, Double>>();
            for (Map.Entry e : list_scaleData) {
                if (k % 5 == 0) {
                    break;
                }
                // sorted_list.add(e);
                linkedScaleHashMap.put((String) e.getKey(), (Double) e.getValue());
                k++;
            }
            // System.out.println(sorted_list.size());
            for (int i = 0; i < 6; i++) {
                list_scaleData.remove(list_scaleData.size() - 1);
            }
            if (rootString.compareTo(lastRootString) == 0) {
                chordName = "";
            } else {
                chordName = ChordComposition.findChord(list_scaleData, rootString);
            }
            lastRootString = new String(rootString);

            // System.out.println(C_sum + "\t" + Db_sum + "\t" + D_sum + "\t" +
            // Eb_sum + "\t" + E_sum + "\t"
            // + F_sum + "\n" + Gb_sum + "\t" + G_sum + "\t" + Ab_sum + "\t" + A_sum
            // + "\t" + Bb_sum
            // + "\t" + B_sum);
            // add count(location point) and ChordName into List
            if (chordName.compareTo("") != 0) {
                result_map.put((double) (count + sampleNum / 3) / WavFile.getSampleRate(), chordName);
            }
            // System.out.println(chordName);
            // System.out.println("============================================================================");
            C_sum = 0;
            Db_sum = 0;
            D_sum = 0;
            Eb_sum = 0;
            E_sum = 0;
            F_sum = 0;
            Gb_sum = 0;
            G_sum = 0;
            Ab_sum = 0;
            A_sum = 0;
            Bb_sum = 0;
            B_sum = 0;
        }
        List<Map.Entry<Double, String>> result = new ArrayList<Map.Entry<Double, String>>(result_map.entrySet());
        /* delete those duplcate */
        Map.Entry<Double, String> a, b;
        for (int i = 1; i < result.size(); i++) {
            a = result.get(i - 1);
            b = result.get(i);
            if (a.getValue().compareTo(b.getValue()) == 0) {
                result.remove(i--);
            }
        }
        // System.out.println(result);
        return result;

    }

    public void getScalePower(List<Map.Entry<Integer, Double>> input) {
        int index = 0;
        int fre = 0;
        double range = 0.5;
        for (Map.Entry e : input) {
            index = (int) e.getKey();
            if (index >= sampleNum / range) {
                index = sampleNum - index;
            }
            fre = (int) (index * WavFile.getSampleRate() / sampleNum);
            if ((fre % (int) Math.round(C_2)) <= range || (fre % (int) Math.round(C_2)) >= (C_2 - range)) {
                C_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Db_2)) <= range || (fre % (int) Math.round(Db_2)) >= (Db_2 - range)) {
                Db_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(D_2)) <= range || (fre % (int) Math.round(D_2)) >= (D_2 - range)) {
                D_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Eb_2)) <= range || (fre % (int) Math.round(Eb_2)) >= (Eb_2 - range)) {
                Eb_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(E_2)) <= range || (fre % (int) Math.round(E_2)) >= (E_2 - range)) {
                E_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(F_2)) <= range || (fre % (int) Math.round(F_2)) >= (F_2 - range)) {
                F_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Gb_2)) <= range || (fre % (int) Math.round(Gb_2)) >= (Gb_2 - range)) {
                Gb_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(G_2)) <= range || (fre % (int) Math.round(G_2)) >= (G_2 - range)) {
                G_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Ab_2)) <= range || (fre % (int) Math.round(Ab_2)) >= (Ab_2 - range)) {
                Ab_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(A_2)) <= range || (fre % (int) Math.round(A_2)) >= (A_2 - range)) {
                A_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Bb_2)) <= range || (fre % (int) Math.round(Bb_2)) >= (Bb_2 - range)) {
                Bb_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(B_2)) <= range || (fre % (int) Math.round(B_2)) >= (B_2 - range)) {
                B_sum += (Double) e.getValue();
            }
        }
    }

    public String getRoot(List<Map.Entry<Integer, Double>> input) {
        int index = 0;
        int fre = 0;
        // int range = 3;
        double range = 0.5;
        String result = "";
        double max = 0;
        double temp_power = 0;
        String temp_root = "";
        for (Map.Entry e : input) {
            index = (int) e.getKey();
            if (index >= sampleNum / range) {
                index = sampleNum - index;
            }
            fre = (int) (index * WavFile.getSampleRate() / sampleNum);

            if ((fre >= E_2 - range) && (fre <= E_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("E");
            } else if ((fre >= F_2 - range) && (fre <= F_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("F");
            } else if ((fre >= Gb_2 - range) && (fre <= Gb_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("Gb");
            } else if ((fre >= G_2 - range) && (fre <= G_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("G");
            } else if ((fre >= Ab_2 - range) && (fre <= Ab_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("Ab");
            } else if ((fre >= A_2 - range) && (fre <= A_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("A");
            } else if ((fre >= Bb_2 - range) && (fre <= Bb_2 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("Bb");
            } else if ((fre >= C_3 - range) && (fre <= C_3 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("C");
            } else if ((fre >= Db_3 - range) && (fre <= Db_3 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("Db");
            } else if ((fre >= D_3 - range) && (fre <= D_3 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("D");
            } else if ((fre >= Eb_3 - range) && (fre <= Eb_3 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("Eb");
            } else if ((fre >= E_3 - range) && (fre <= E_3 + range)) {
                temp_power = (Double) e.getValue();
                temp_root = new String("E");
            }

            if (temp_power > max) {
                max = temp_power;
                result = temp_root;
            }
        }
        return result;
    }

    @FXML
    void btnAnalysisClick(ActionEvent event) {
        signalAnalysis(signal_modify);
        drawFT();
    }

    public int[] getNLargestFrequencyIndex(Complex[] input, int n) {

        int[] indexArr = new int[n];
        sampleRate = WavFile.getSampleRate();
        ArrayList<Integer> index = new ArrayList<Integer>();
        double a, b;
        // init
        for (int i = 0; i < n; i++) {
            index.add(1);
        }
        int temp = 0, max = 0;
        double fre;
        for (int i = 1; i < sampleNum / 2; i++) {
            // a = Math.pow(input[i].re(), 2) + Math.pow(input[i].im(), 2);
            a = Math.abs(input[i].re());
            // System.out.println(i + ": " + a);
            fre = ((double) i * (double) WavFile.getSampleRate() / sampleNum);
            if (i >= sampleNum / 2) {
                temp = sampleNum - i;
                fre = ((double) temp * (double) WavFile.getSampleRate() / sampleNum);
            }
            // System.out.println(fre);
            // compare with those inside index array
            for (int j = 0; j < n; j++) {
                // b = Math.pow(input[index.get(j)].re(), 2) +
                // Math.pow(input[index.get(j)].re(), 2);
                b = Math.abs(input[j].re());
                // range of fft
                if (a > b && fre > 80 && fre < 300) {
                    // if (a > b) {
                    index.add(j, temp);
                    // System.out.println(fre);
                    break;
                }
            }
            if (index.size() > n) {
                index.remove(index.size() - 1);
            }
        }
        for (int i = 0; i < n; i++) {
            System.out.print(index.get(i) + "\t");
            if ((i + 1) % n == 0) {
                System.out.println();
            }
            indexArr[i] = index.get(i);
        }
        return indexArr;
    }

    private void drawFT() {
        GraphicsContext gc1 = canvas1.getGraphicsContext2D();
        gc1.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
        /* f = 2*f for every octive */
        double C_2 = 65.41;
        double Db_2 = 69.30;
        double D_2s = 73.42;
        double Eb_2 = 77.78;
        double E_2 = 82.41;
        double F_2 = 87.31;
        double Gb_2 = 92.50;
        double G_2 = 98;
        double Ab_2 = 103.83;
        double A_2 = 110;
        double Bb_2 = 116.54;
        double B_2 = 123;

        // double E_3 = 164;
        // double F_3 = 174;
        // double Fsharp_3 = 185;
        // double G_3 = 196;
        // double Gsharp_3 = 207.65;
        // double A_3 = 220;
        // double Asharp_3 = 233.08;
        // double B_3 = 247;
        // double C_4 = 261;
        // double Csharp_4 = 277.18;
        // double D_4 = 293;
        // double Dsharp_4 = 311.13;
        // double E_4 = 329;
        // double F_4 = 349.23;
        // double Fsharp_4 = 269.99;
        // double G_4 = 392;
        // C_major
        // gc1.setStroke(Color.GREEN);
        // gc1.strokeLine(0, canvas1.getHeight() - C_note3, canvas1.getWidth(),
        // canvas1.getHeight() - C_note3);
        // gc1.strokeLine(0, canvas1.getHeight() - E_note3, canvas1.getWidth(),
        // canvas1.getHeight() - E_note3);
        // gc1.strokeLine(0, canvas1.getHeight() - G_note3, canvas1.getWidth(),
        // canvas1.getHeight() - G_note3);

        // G
        // gc1.setStroke(Color.RED);
        // gc1.strokeLine(0, canvas1.getHeight() - G_note2, canvas1.getWidth(),
        // canvas1.getHeight() - G_note2);
        // gc1.strokeLine(0, canvas1.getHeight() - B_note2, canvas1.getWidth(),
        // canvas1.getHeight() - B_note2);
        // gc1.strokeLine(0, canvas1.getHeight() - D_note3, canvas1.getWidth(),
        // canvas1.getHeight() - D_note3);
        gc1.setStroke(Color.RED);
        // gc1.strokeLine(0, canvas1.getHeight() - E_2, canvas1.getWidth(),
        // canvas1.getHeight() - E_2);
        // gc1.strokeLine(0, canvas1.getHeight() - F_2, canvas1.getWidth(),
        // canvas1.getHeight() - F_2);
        // gc1.strokeLine(0, canvas1.getHeight() - G_2, canvas1.getWidth(),
        // canvas1.getHeight() - G_2);
        // gc1.strokeLine(0, canvas1.getHeight() - A_3, canvas1.getWidth(),
        // canvas1.getHeight() - A_3);
        // gc1.strokeLine(0, canvas1.getHeight() - B_3, canvas1.getWidth(),
        // canvas1.getHeight() - B_3);
        // gc1.strokeLine(0, canvas1.getHeight() - C_3, canvas1.getWidth(),
        // canvas1.getHeight() - C_3);
        // gc1.strokeLine(0, canvas1.getHeight() - D_3, canvas1.getWidth(),
        // canvas1.getHeight() - D_3);
        // gc1.strokeLine(0, canvas1.getHeight() - F_3, canvas1.getWidth(),
        // canvas1.getHeight() - F_3);
        // gc1.strokeLine(0, canvas1.getHeight() - G_3, canvas1.getWidth(),
        // canvas1.getHeight() - G_3);

        gc1.setFill(Color.BLACK);
        for (int time = 0; time < fft_signal.size(); time++) {
            // channel number
            for (int channel = 0; channel < fft_signal.get(time).length; channel++) {
                for (int freNum = 0; freNum < fft_signal.get(time)[0].length; freNum++) {
                    if (channel % 2 == 0) {
                        gc1.fillRect(time, canvas1.getHeight() - (int) fft_signal.get(time)[channel][freNum], 3, 1);
                    }
                }
            }
        }

    }
}

class compare implements Comparator<Double> {
    public int compare(Double a, Double b) {
        if (a > b) {
            return -1;
        }
        return 1;
    }
}