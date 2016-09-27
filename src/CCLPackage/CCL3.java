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
import javax.imageio.ImageIO;

/**
 *
 * @author KD
 */

public class CCL3 {
    
    private int[][] labeling;
    private static int newLabel = 1;
    private int minLabel;
    private static BufferedImage image;
    private static BufferedImage input;
    private boolean flag = false; //finding out whether I need to increment newLabel value or not
    private int width;
    private int height;
    private int[][] eLabels; //label equivalence relationships
    
    public void firstPass(){
        
        width = input.getWidth();
        height = input.getHeight();
        labeling = new int[width][height]; //initialize the 'labeling' as the size of the original image
        eLabels = new int[width][height];
        
        //first pass
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                minLabel = newLabel;
                
                //finding out the minimum labeled neighbours (privious neighbour and three from the top) with same RGB color
                if(j-1>=0 && (input.getRGB(i, j) == input.getRGB(i, j-1))){
                    if(minLabel > labeling[i][j-1]){    
                        minLabel = labeling[i][j-1];
                        flag = true;
                    }
                }
                if(i-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j))){
                    if(minLabel > labeling[i-1][j]) {
                        minLabel = labeling[i-1][j];
                        flag = true;
                    }
                }
                if(i-1>=0 && j-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j-1))){
                    if(minLabel > labeling[i-1][j-1]) {
                        minLabel = labeling[i-1][j-1];
                        flag = true;
                    }
                }
                if(i-1>=0 && j+1<input.getHeight() && (input.getRGB(i, j) == input.getRGB(i-1, j+1))){
                    if(minLabel > labeling[i-1][j+1]){
                        minLabel = labeling[i-1][j+1];
                        flag = true;
                    }
                }
                
                if(flag == false){
                    newLabel++;
                }
                flag = false;
                labeling[i][j] = minLabel;
                
                if(j-1>=0 && (input.getRGB(i, j) == input.getRGB(i, j-1))){
                    if(!isExist(labeling[i][j], labeling[i][j-1]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i][j-1];
                    if(!isExist(labeling[i][j-1], labeling[i][j]))
                        eLabels[labeling[i][j-1]][isEmpty(labeling[i][j-1])] = labeling[i][j];
                }
                if(i-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j))){
                    if(!isExist(labeling[i][j], labeling[i-1][j]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i-1][j];
                    if(!isExist(labeling[i-1][j], labeling[i][j]))
                        eLabels[labeling[i-1][j]][isEmpty(labeling[i-1][j])] = labeling[i][j];
                }
                if(i-1>=0 && j-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j-1))){
                    if(!isExist(labeling[i][j], labeling[i-1] [j-1]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i-1] [j-1];
                    if(!isExist(labeling[i-1][j-1], labeling[i][j]))
                        eLabels[labeling[i-1][j-1]][isEmpty(labeling[i-1][j-1])] = labeling[i][j];
                }
                if(i-1>=0 && j+1<input.getHeight() && (input.getRGB(i, j) == input.getRGB(i-1, j+1))){
                    if(!isExist(labeling[i][j], labeling[i-1] [j+1]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i-1] [j+1];
                    if(!isExist(labeling[i-1][j+1], labeling[i][j]))
                        eLabels[labeling[i-1][j+1]][isEmpty(labeling[i-1][j+1])] = labeling[i][j];
                }
            }
        }
    }
    
    public void secondPass(){
        //second pass
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                outerloop:
                for(int x=1; x<width; x++){
                    for(int y=0; y<height; y++){
                        if(labeling[i][j]==eLabels[x][y]){
                            labeling[i][j] = minimum(x);
                            break outerloop;
                        }
                    }
                }
            }
        }
    }
    
    public void output() throws IOException{
        
        for(int value=1; value<newLabel; value++){
            boolean yes = false;
            //creating a new temporary buffered image with transparent background
            BufferedImage OutputImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TRANSLUCENT);
            for(int i=0; i<input.getWidth(); i++){
                for(int j=0; j<input.getHeight(); j++){
                    if(labeling[i][j] == value){ //painting into new image with the original RGB with same labeling
                        OutputImage.setRGB(i, j, image.getRGB(i, j));
                        yes = true;
                    }
                }
            }
            // getting new images
            if(yes)
                ImageIO.write(OutputImage, "jpg", new File("kallol" + value + "." + "jpg"));
        }
    }
    
    private boolean isExist(int x, int i0) {
        boolean mark = false;
        for (int a=0; a<height; a++){
            if(eLabels[x][a]== i0){
                mark = true;
                break;
            }
            else 
                mark = false;
            }
        return mark;
    }
    
    private int minimum(int x){
        int min = x;
        //System.out.print(min);
        for(int y=0; y<height; y++){
            if(eLabels[x][y]<min && eLabels[x][y]!=0){
                min = eLabels[x][y];
                //System.out.print(min);
            }
        }
        return min;
    }
    
    private int isEmpty(int x){
        int index = 0;
        for(int y=0; y<height; y++){
            if(eLabels[x][y] == 0){
                index = y;
                break;
            }
        }
        return index;
    }
    
    public static BufferedImage thresholdImage(BufferedImage image, int threshold) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(image, 0, 0, null);
        WritableRaster raster = result.getRaster();
        int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] < threshold) pixels[i] = 0;
                else pixels[i] = 255;
            }
            raster.setPixels(0, y, image.getWidth(), 1, pixels);
        }
        return result;
    }
    
    public static void main(String[] args) throws IOException {
        //Multimap<String, String> myMultimap = ArrayListMultimap.create();
    	CCL3 cc3 = new CCL3();
    	try {
                image = ImageIO.read(new File(CCL.class.getResource("kallol.jpg").toURI()));
                input = thresholdImage(image, 128);
                ImageIO.write(thresholdImage(image, 128), "jpg", new File("kallol" + "." + "jpg"));
                cc3.firstPass();
                cc3.secondPass();
                //System.out.println("after 2nd pass:");
                cc3.output();
                
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}