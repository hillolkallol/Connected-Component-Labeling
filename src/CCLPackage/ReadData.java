/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CCLPackage;

/**
 *
 * @author KD
 */

import java.io.IOException;
import static jdk.nashorn.internal.objects.NativeMath.log;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class ReadData {
   public static void main(String[] args) throws InvalidRangeException {

        String filename = "FOP120160101_000203_V07";
        NetcdfFile ncfile = null;
        int[][] refArrayInt;
        try {
            ncfile = NetcdfFile.open(filename);
            
            String varReflectivity = "Reflectivity"; //Reflectivity
            Variable v1 = ncfile.findVariable(varReflectivity);
            if (null == v1) return;
            try {
                Array data = v1.read();
                String s = NCdumpW.toString(data);
                String[][] refArray = create2DArray(s);
                refArrayInt = new int[refArray.length-2][refArray[5].length-2];
                for(int i=3; i<refArray.length-2; i++){
                    for(int j=1; j<refArray[i].length-2; j++){
                        if(refArray[i][j]==null)
                            refArrayInt[i-3][j-1] = 0;
                        else
                            refArrayInt[i-3][j-1] = Integer.parseInt(refArray[i][j]);
                    }
                }
                
                System.out.println(refArrayInt.length);
                System.out.println(refArrayInt[0].length);

                for(int i=0; i<refArrayInt.length; i++){
                    for(int j=0; j<refArrayInt[i].length; j++){
                        System.out.print(refArrayInt[i][j] + " ");
                    }
                    System.out.println();
                }
            } catch (IOException ioe) {
                log("trying to read " + varReflectivity, ioe);
            }
            
            //windSpeed
            String varRadialVelocity = "RadialVelocity";
            Variable v2 = ncfile.findVariable(varRadialVelocity);
                if (null == v2) return;
            try {
                Array data = v2.read();
                int[] radialVelocity = (int []) data.get1DJavaArray(int.class);
                System.out.println(radialVelocity[9]);
                //NCdumpW.printArray(data, varRadialVelocity, System.out, null);
            } catch (IOException ioe) {
                log("trying to read " + varRadialVelocity, ioe);
            }
            
        } catch (IOException ioe) {
                log("trying to open " + filename, ioe);
        } finally { 
            if (null != ncfile) try {
                ncfile.close();
            } catch (IOException ioe) {
                log("trying to close " + filename, ioe);
            }
        }
    }
   
   public static String[][] create2DArray(String s) throws IOException {
        String content = s;
        // get the lines
        String[] lines = content.split("\\r?\\n"); // split on new lines
        // get the max amt of nums in the file in a single line
        int maxInLine = 0;
        for (String x : lines) {
            String[] temp = x.split(",\\s+"); // split on whitespace
            if (temp.length > maxInLine) {
                maxInLine = temp.length;
            }
        }
        String[][] finalArray = new String[lines.length][maxInLine]; // declare and instantiate the array of arrays
        // standard double for loop to fill up your 2D array
        for (int i = 0; i < lines.length; i++) {
            String[] temp = lines[i].split(",\\s+"); // split on whitespace
            for (int j = 0; j < temp.length; j++) {
                finalArray[i][j] = temp[j];
            }
        }
        return finalArray;
    }
}