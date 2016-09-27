/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CCLPackage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.Collection;
import javax.imageio.ImageIO;

/**
 *
 * @author KD
 */

public class CCL5 {
    
    private int[][] labeling;
    private static int newLabel = 1;
    private int minLabel;
    private static BufferedImage input;
    private static int[][] imageArray;
    private boolean flag = false; //finding out whether I need to increment newLabel value or not
    private static int width;
    private static int height;
    Multimap<Integer, Integer> equLabels = ArrayListMultimap.create(); //label equivalence relationships
    
    private void firstPass(int threshold, int parameter) {
        
        width = input.getWidth();
        height = input.getHeight();
        labeling = new int[width][height]; //initialize the 'labeling' as the size of the original image
        
        //first pass
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                minLabel = newLabel;
                
                //finding out the minimum labeled neighbours (privious neighbour and three from the top) with same RGB color
                if(j-1>=0 && ifBelowThreshold(imageArray[i][j], imageArray[i][j-1], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i][j-1], parameter)){
                    if(minLabel > labeling[i][j-1] && labeling[i][j-1]!=0){    
                        minLabel = labeling[i][j-1];
                    }
                    flag = true;
                }
                if(i-1>=0 && ifBelowThreshold(imageArray[i][j], imageArray[i-1][j], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i-1][j], parameter)){
                    if(minLabel > labeling[i-1][j] && labeling[i-1][j]!=0) {
                        minLabel = labeling[i-1][j];
                    }
                    flag = true;
                }
                if(i-1>=0 && j-1>=0 && ifBelowThreshold(imageArray[i][j], imageArray[i-1][j-1], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i-1][j-1], parameter)){
                    if(minLabel > labeling[i-1][j-1] && labeling[i-1][j-1]!=0) {
                        minLabel = labeling[i-1][j-1];
                    }
                    flag = true;
                }
                if(i-1>=0 && j+1<input.getHeight() && ifBelowThreshold(imageArray[i][j], imageArray[i-1][j+1], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i-1][j+1], parameter)){
                    if(minLabel > labeling[i-1][j+1] && labeling[i-1][j+1]!=0){
                        minLabel = labeling[i-1][j+1];
                    }
                    flag = true;
                }
                
                if(flag == false){
                    labeling[i][j] = 0;
                    newLabel++;
                }
                else {
                    labeling[i][j] = minLabel;
                    //System.out.println("yes!");
                }
                flag = false;
                
                
                if(j-1>=0 && ifBelowThreshold(imageArray[i][j], imageArray[i][j-1], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i][j-1], parameter)){
                    if(labeling[i][j]==0){
                        labeling[i][j] = newLabel;
                        newLabel++;
                    }
                    if(labeling[i][j-1]==0){
                        labeling[i][j-1] = newLabel;
                        newLabel++;
                    }
                    if(!isExist(labeling[i][j], labeling[i][j-1]))
                        equLabels.put(labeling[i][j], labeling[i][j-1]);
                    if(!isExist(labeling[i][j-1], labeling[i][j]))
                        equLabels.put(labeling[i][j-1], labeling[i][j]);
                }
                if(i-1>=0 && ifBelowThreshold(imageArray[i][j], imageArray[i-1][j], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i-1][j], parameter)){
                    if(labeling[i][j]==0){
                        labeling[i][j] = newLabel;
                        newLabel++;
                    }
                    if(labeling[i-1][j]==0){
                        labeling[i-1][j] = newLabel;
                        newLabel++;
                    }
                    if(!isExist(labeling[i][j], labeling[i-1][j]))
                        equLabels.put(labeling[i][j], labeling[i-1][j]);
                    if(!isExist(labeling[i-1][j], labeling[i][j]))
                        equLabels.put(labeling[i-1][j], labeling[i][j]);
                }
                if(i-1>=0 && j-1>=0 && ifBelowThreshold(imageArray[i][j], imageArray[i-1][j-1], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i-1][j-1], parameter)){
                    if(labeling[i][j]==0){
                        labeling[i][j] = newLabel;
                        newLabel++;
                    }
                    if(labeling[i-1] [j-1]==0){
                        labeling[i-1] [j-1] = newLabel;
                        newLabel++;
                    }
                    if(!isExist(labeling[i][j], labeling[i-1] [j-1]))
                        equLabels.put(labeling[i][j], labeling[i-1] [j-1]);
                    if(!isExist(labeling[i-1][j-1], labeling[i][j]))
                        equLabels.put(labeling[i-1][j-1], labeling[i][j]);
                }
                if(i-1>=0 && j+1<input.getHeight() && ifBelowThreshold(imageArray[i][j], imageArray[i-1][j+1], threshold) && ifAboveParameter(imageArray[i][j], imageArray[i-1][j+1], parameter)){
                    if(labeling[i][j]==0){
                        labeling[i][j] = newLabel;
                        newLabel++;
                    }
                    if(labeling[i-1] [j+1]==0){
                        labeling[i-1] [j+1] = newLabel;
                        newLabel++;
                    }
                    if(!isExist(labeling[i][j], labeling[i-1] [j+1]))
                        equLabels.put(labeling[i][j], labeling[i-1] [j+1]);
                    if(!isExist(labeling[i-1][j+1], labeling[i][j]))
                        equLabels.put(labeling[i-1][j+1], labeling[i][j]);
                }
            }
        }
        
        /*
            for(int i=0; i<input.getWidth(); i++){
                for(int j=0; j<input.getHeight(); j++){
                    System.out.print(labeling[i][j] + " ");
                }
                System.out.println();
            }
            
                for(int x=1; x<equLabels.size(); x++){
                    Collection<Integer> col = equLabels.get(x);
                    Integer[] values = col.toArray (new Integer[col.size()]);
                    for(int y=0; y<col.size(); y++){
                        System.out.print(values[y]);
                    }
                    System.out.println();
                }*/
    }
    
    private void secondPass() {
        //second pass
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                outerloop:
                for(int x=1; x<equLabels.size(); x++){
                    Collection<Integer> col = equLabels.get(x);
                    Integer[] values = col.toArray (new Integer[col.size()]);
                    for(int y=0; y<col.size(); y++){
                        if(labeling[i][j]==values[y]){
                            labeling[i][j] = minimum(x);
                            break outerloop;
                        }
                    }
                }
            }
        }
        
            /*for(int i=0; i<input.getWidth(); i++){
                for(int j=0; j<input.getHeight(); j++){
                    System.out.print(labeling[i][j] + " ");
                }
                System.out.println();
            }*/
    }
    
    private void output() throws IOException {
        
        for(int value=1; value<=newLabel; value++){
            boolean yes = false;
            //creating a new temporary buffered image with transparent background
            BufferedImage OutputImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TRANSLUCENT);
            for(int i=0; i<input.getWidth(); i++){
                for(int j=0; j<input.getHeight(); j++){
                    if(labeling[i][j] == value){ //painting into new image with the original RGB with same labeling
                        OutputImage.setRGB(i, j, input.getRGB(i, j));
                        yes = true;
                    }
                }
            }
            // getting new images
            if(yes)
                ImageIO.write(OutputImage, "png", new File("two" + value + "." + "png"));
        }
    }
    
    private boolean isExist(int x, int i0) {
        boolean mark = false;
        Collection<Integer> col = equLabels.get(x);
        Integer[] values = col.toArray (new Integer[col.size()]);
        for(int y=0; y<col.size(); y++){
            if(values[y] == i0){
                mark = true;
                break;
            }
            else 
                mark = false;
            }
        return mark;
    }
    
    private int minimum(int x) {
        int min = x;
        Collection<Integer> col = equLabels.get(x);
        Integer[] values = col.toArray (new Integer[col.size()]);
        for(int y=0; y<col.size(); y++){
            if(values[y]<min && values[y]!=0){
                min = values[y];
                //System.out.print(min);
            }
        }
        return min;
    }
    
    private boolean ifBelowThreshold(int cRGB, int nRGB, int threshold) {
        boolean value = false;
        if(abs(cRGB-nRGB)<threshold){
            value = true;
        }
        return value;
    }
    
    private boolean ifAboveParameter(int cRGB, int nRGB, int parameter) {
        boolean value = false;
        if(cRGB>parameter && nRGB>parameter){
            value = true;
        }
        return value;
    }
    
    private static BufferedImage convertToDecimal(BufferedImage image) {
        imageArray = new int[image.getWidth()][image.getHeight()];
        BufferedImage tImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        tImage.getGraphics().drawImage(image, 0, 0, null);
        WritableRaster raster = tImage.getRaster();
        int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                imageArray[i][y] = pixels[i];
            }
            raster.setPixels(0, y, image.getWidth(), 1, pixels);
        }
        return tImage;
    }
    
    private static BufferedImage thresholdImage(BufferedImage image, int threshold) {
        BufferedImage tImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        tImage.getGraphics().drawImage(image, 0, 0, null);
        WritableRaster raster = tImage.getRaster();
        int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] < threshold) pixels[i] = 0;
                else pixels[i] = 255;
            }
            raster.setPixels(0, y, image.getWidth(), 1, pixels);
        }
        return tImage;
    }
    
    public static void main(String[] args) throws IOException {
        
    	CCL5 ccl5 = new CCL5();
    	try {
            input = ImageIO.read(new File(CCL.class.getResource("kallol.jpg").toURI()));
            convertToDecimal(input);
            
            /*for(int i=0; i<input.getWidth(); i++){
                for(int j=0; j<input.getHeight(); j++){
                    System.out.print(imageArray[i][j] + " ");
                }
                System.out.println();
            }*/

            //ImageIO.write(convertToDecimal(image), "png", new File("one" + "." + "png"));
            ccl5.firstPass(128, 100);
            ccl5.secondPass();
            //System.out.println("after 2nd pass:");
            ccl5.output();
                
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
