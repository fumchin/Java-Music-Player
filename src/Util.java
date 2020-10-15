import java.util.*;


public class Util{
    public static void hanningWindow(Complex[] input){
        // first we duplicate the input array for further changes
        // ArrayList<Double>[] result;
        // result = new ArrayList[input.length];
        // for(int row=0; row<input.length; row++){
        //     result[row] = new ArrayList<>(input[row]);
        // }
        
        //start to hanning function
        int windowLength = input.length;
        double val = 0;
        // for(int row=0; row<input.length; row++){
            for(int col=0; col<windowLength; col++){
                val = input[col].re()*0.5 * (1 - Math.cos((2 * Math.PI * col)
                / (windowLength - 1)));
                input[col] = new Complex(val, 0);
            }
        // }
    }
}