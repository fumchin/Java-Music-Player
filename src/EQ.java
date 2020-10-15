import java.util.*;

public class EQ extends FFTImplement {
    // private ArrayList<Double>[] signal_modify;

    public EQ() {
        super((int) Math.pow(2, 16));
    }

    public ArrayList<Double>[] lowPass(ArrayList<Double>[] input) {
        fft_signal_arr = new Complex[input[0].size()][];
        part_signal_arr = new Complex[input[0].size()][];
        // clone signal arraylist to another list (don't change original one)
        for (int row = 0; row < input.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }

        signal_modify = new ArrayList[input.length];
        for (int channel = 0; channel < input.length; channel++) {
            signal_modify[channel] = new ArrayList<Double>();
        }

        int count = 0;
        double time = 0;
        double cutoff_frequency = 300;
        // double high = 160;
        // double low = 80;
        // number of frequency we get in
        int n = 1;
        for (count = 0; count < input[0].size() - sampleNum; count += sampleNum) {

            // put arraylist into complex 2d array
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    part_signal_arr[row][col] = new Complex(input[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            // fft -> filter ->ifft
            // fft
            for (int row = 0; row < input.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // filter
            for (int col_filter = 1; col_filter < sampleNum; col_filter++) {
                for (int row_filter = 0; row_filter < input.length; row_filter++) {
                    double fre = ((double) col_filter * (double) WavFile.getSampleRate() / sampleNum);
                    if (col_filter >= sampleNum / 2) {
                        // fre = ((double) (sampleNum - col_filter) * (double) WavFile.getSampleRate() /
                        // sampleNum);
                        fre = ((double) (col_filter - sampleNum / 2) * (double) WavFile.getSampleRate() / sampleNum);
                    }
                    // System.out.println(fre);
                    if ((fre > cutoff_frequency)) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    } else if (fre < 30) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    } else {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(1);
                    }

                }
            }
            // ifft
            for (int row = 0; row < input.length; row++) {
                part_signal_arr[row] = FFT.ifft(fft_signal_arr[row]);
            }

            // add into signal modify
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    signal_modify[row].add(part_signal_arr[row][col].re());
                }
            }

        }

        return signal_modify;
    }

    public ArrayList<Double>[] highPass(ArrayList<Double>[] input) {
        fft_signal_arr = new Complex[input[0].size()][];
        part_signal_arr = new Complex[input[0].size()][];
        // clone signal arraylist to another list (don't change original one)
        for (int row = 0; row < input.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }

        signal_modify = new ArrayList[input.length];
        for (int channel = 0; channel < input.length; channel++) {
            signal_modify[channel] = new ArrayList<Double>();
        }

        int count = 0;
        double time = 0;
        double cutoff_frequency = 1000;
        // double high = 160;
        // double low = 80;
        for (count = 0; count < input[0].size() - sampleNum; count += sampleNum) {

            // put arraylist into complex 2d array
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    part_signal_arr[row][col] = new Complex(input[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            // fft -> filter ->ifft
            // fft
            for (int row = 0; row < input.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // filter
            for (int col_filter = 0; col_filter < sampleNum; col_filter++) {
                for (int row_filter = 0; row_filter < input.length; row_filter++) {
                    double fre = ((double) col_filter * (double) WavFile.getSampleRate() / sampleNum);
                    if (col_filter > sampleNum / 2) {
                        fre = ((double) (sampleNum - col_filter) * (double) WavFile.getSampleRate() / sampleNum);
                    }
                    // System.out.println(fre);
                    if ((fre > cutoff_frequency)) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(1.2);
                    } else if (fre < 30) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    } else {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    }

                }
            }
            // ifft
            for (int row = 0; row < input.length; row++) {
                part_signal_arr[row] = FFT.ifft(fft_signal_arr[row]);
            }

            // add into signal modify
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    signal_modify[row].add(part_signal_arr[row][col].re());
                }
            }
        }

        return signal_modify;
    }

    public ArrayList<Double>[] rockStyle(ArrayList<Double>[] input) {
        fft_signal_arr = new Complex[input[0].size()][];
        part_signal_arr = new Complex[input[0].size()][];
        // clone signal arraylist to another list (don't change original one)
        for (int row = 0; row < input.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }

        signal_modify = new ArrayList[input.length];
        for (int channel = 0; channel < input.length; channel++) {
            signal_modify[channel] = new ArrayList<Double>();
        }

        int count = 0;
        double time = 0;
        double band1_h = 12000;
        double band1_l = 8000;
        double band2_h = 8000;
        double band2_l = 4000;
        double bass = 800;

        // double low = 80;
        for (count = 0; count < input[0].size() - sampleNum; count += sampleNum) {

            // put arraylist into complex 2d array
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    part_signal_arr[row][col] = new Complex(input[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            // fft -> filter ->ifft
            // fft
            for (int row = 0; row < input.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // filter
            for (int col_filter = 0; col_filter < sampleNum; col_filter++) {
                for (int row_filter = 0; row_filter < input.length; row_filter++) {
                    double fre = ((double) col_filter * (double) WavFile.getSampleRate() / sampleNum);
                    if (col_filter > sampleNum / 2) {
                        fre = ((double) (sampleNum - col_filter) * (double) WavFile.getSampleRate() / sampleNum);
                    }
                    // System.out.println(fre);
                    if ((fre > band1_l && fre < band1_h) || (fre > band2_l && fre < band2_h) || (fre < bass)) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(1.2);
                    } else if (fre < band2_l && fre > band2_h) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0.3);
                    }
                    if (fre < 60) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    }

                }
            }
            // ifft
            for (int row = 0; row < input.length; row++) {
                part_signal_arr[row] = FFT.ifft(fft_signal_arr[row]);
            }

            // add into signal modify
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    signal_modify[row].add(part_signal_arr[row][col].re());
                }
            }
        }

        return signal_modify;
    }

}