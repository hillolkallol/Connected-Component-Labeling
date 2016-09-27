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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try {
            ncfile = NetcdfFile.open(filename);
            String varName = "Reflectivity"; 
            Variable v = ncfile.findVariable(varName);
                if (null == v) return;
            try {
                Array data = v.read();
                int[] reflectivity = (int []) data.get1DJavaArray(int.class);
                System.out.println(reflectivity[9]);
                //NCdumpW.printArray(data, varName, System.out, null);
            } catch (IOException ioe) {
                log("trying to read " + varName, ioe);

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

}