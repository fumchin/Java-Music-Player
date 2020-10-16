import java.util.*;


public class Util{
    public static Complex[] hanningWindow(Complex[] input){
        Complex[] result = new Complex[input.length];
        //start to hanning function
        int windowLength = input.length;
        double val = 0;
            for(int col=0; col<windowLength; col++){
                val = input[col].re() * 0.5 * (1 - Math.cos((2 * Math.PI * col)
                / (windowLength - 1)));
                // System.out.println(val);
                result[col] = new Complex(val, 0);
            }
        return result;
    }
}