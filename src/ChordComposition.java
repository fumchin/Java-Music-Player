
/* this is a very simple chord finder,first note that pass in should root note */
import java.util.*;

public class ChordComposition {
    public static enum Note {
        C(1), Db(1.5), D(2), Eb(2.5), E(3), F(3.5), Gb(4), G(4.5), Ab(5), A(5.5), Bb(6), B(6.5);

        private double index;

        Note(double index) {
            this.index = index;
        }

        public double getIndex() {
            return index;
        }
    }

    public static String findChord(List<Map.Entry<String, Double>> list_scaleData, String rootString) {
        String result = "";
        double rootIndex = 0;
        double thirdMajorIndex = 0;
        double thirdMinIndex = 0;
        double octaveIndex = 0;
        double octaveDimIndex = 0;
        // double octaveIndex = 0;
        /* step 1: find root note (should be first note, and amplitude should > 0) */

        /* output test */
        // for (Map.Entry<String, Double> e : list_scaleData) {
        // System.out.print(e + "\t");
        // }
        // System.out.println();

        Map.Entry<String, Double> eFirst = list_scaleData.get(0);
        if ((Double) eFirst.getValue() > 0) {
            result += rootString;
            rootIndex = Note.valueOf(rootString).getIndex();
            // System.out.println(result);

            /* step 2: find Thirds & Octave */
            for (Map.Entry<String, Double> eTO : list_scaleData) {
                Note n = Note.valueOf((String) eTO.getKey());
                // Third
                if (thirdMajorIndex == 0 && thirdMinIndex == 0) {
                    if (((Double) n.getIndex() - rootIndex) == 2 || ((Double) n.getIndex() - rootIndex) == -4) {
                        thirdMajorIndex = n.getIndex();
                        result += "";
                    } else if (((Double) n.getIndex() - rootIndex) == 1.5
                            || ((Double) n.getIndex() - rootIndex) == -4.5) {
                        thirdMinIndex = n.getIndex();
                        result += "m";
                    }
                }
                // Octvae
                if (octaveIndex == 0 && octaveDimIndex == 0) {
                    if (((Double) n.getIndex() - rootIndex) == 3.5 || ((Double) n.getIndex() - rootIndex) == -2.5) {
                        octaveIndex = n.getIndex();
                        result += "";
                    } else if ((((Double) n.getIndex() - rootIndex) == 3 || ((Double) n.getIndex() - rootIndex) == -3)
                            && thirdMinIndex != 0) {
                        octaveDimIndex = n.getIndex();
                        result = (rootString + "dim");
                    }
                }

            }
        }

        return result;
    }

}