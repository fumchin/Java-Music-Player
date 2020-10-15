import java.util.*;

public class BeatDetection {
    private int bpm;

    public BeatDetection() {

    }

    public int findBPM(ArrayList<Double>[] signal_modify) {
        ArrayList<Integer> beat_location = new ArrayList<Integer>();
        /* first calculate 43 instant sound energy */
        ArrayList<Double> instantEnergy = new ArrayList<Double>();
        int count = 0;
        for (int arrNum = 0; arrNum < 43; arrNum++) {
            double temp_instantEnergy = 0;
            for (int col = 0; col < 1024; col++) {
                for (int row = 0; row < signal_modify.length; row++) {
                    temp_instantEnergy += Math.pow(signal_modify[row].get(count + col), 2);
                }
            }
            count += 1024;
            instantEnergy.add(temp_instantEnergy);
        }
        /* start */
        double avg_energy = 0;
        double new_instantEnergy = 0;
        while (count < signal_modify[0].size() - 1024) {
            /* calcualte history energy, last 43 energy */
            for (int i = 0; i < 43; i++) {
                avg_energy += instantEnergy.get(i);
            }
            avg_energy /= 43;
            /* calculate new instant energy */
            for (int col = 0; col < 1024; col++) {
                for (int row = 0; row < signal_modify.length; row++) {
                    new_instantEnergy += Math.pow(signal_modify[row].get(count + col), 2);
                }
            }

            /* decide whether it is beat position */
            if (new_instantEnergy > 1.8 * avg_energy) {
                beat_location.add(count - 1024);
            }

            /* replace oldest energy with newest */
            instantEnergy.add(new_instantEnergy);
            instantEnergy.remove(0); // oldest one
            count += 1024;
            /* repeat till end */
            new_instantEnergy = 0;
            avg_energy = 0;
        }

        for (int i = 0; i < instantEnergy.size() - 1; i++) {
            int temp = beat_location.get(i + 1) - beat_location.get(i);
            System.out.println((double) 60 / ((double) temp / WavFile.getSampleRate()));
        }
        return bpm;
    }
}