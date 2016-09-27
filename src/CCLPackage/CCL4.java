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
import java.util.Collection;
import javax.imageio.ImageIO;

/**
 *
 * @author KD
 */

public class CCL4 {
    
    private int[][] labeling;
    private static int newLabel = 1;
    private int minLabel;
    private static BufferedImage image;
    private static BufferedImage input;
    private boolean flag = false; //finding out whether I need to increment newLabel value or not
    private int width;
    private int height;
    Multimap<Integer, Integer> equLabels = ArrayListMultimap.create(); //label equivalence relationships
    
    public void firstPass(){
        
        width = input.getWidth();
        height = input.getHeight();
        labeling = new int[width][height]; //initialize the 'labeling' as the size of the original image
        
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
                        equLabels.put(labeling[i][j], labeling[i][j-1]);
                    if(!isExist(labeling[i][j-1], labeling[i][j]))
                        equLabels.put(labeling[i][j-1], labeling[i][j]);
                }
                if(i-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j))){
                    if(!isExist(labeling[i][j], labeling[i-1][j]))
                        equLabels.put(labeling[i][j], labeling[i-1][j]);
                    if(!isExist(labeling[i-1][j], labeling[i][j]))
                        equLabels.put(labeling[i-1][j], labeling[i][j]);
                }
                if(i-1>=0 && j-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j-1))){
                    if(!isExist(labeling[i][j], labeling[i-1] [j-1]))
                        equLabels.put(labeling[i][j], labeling[i-1] [j-1]);
                    if(!isExist(labeling[i-1][j-1], labeling[i][j]))
                        equLabels.put(labeling[i-1][j-1], labeling[i][j]);
                }
                if(i-1>=0 && j+1<input.getHeight() && (input.getRGB(i, j) == input.getRGB(i-1, j+1))){
                    if(!isExist(labeling[i][j], labeling[i-1] [j+1]))
                        equLabels.put(labeling[i][j], labeling[i-1] [j+1]);
                    if(!isExist(labeling[i-1][j+1], labeling[i][j]))
                        equLabels.put(labeling[i-1][j+1], labeling[i][j]);
                }
            }
        }
    }
    
    public void secondPass(){
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
                ImageIO.write(OutputImage, "png", new File("one" + value + "." + "png"));
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
    
    private int minimum(int x){
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
    
    public static BufferedImage thresholdImage(BufferedImage image, int threshold) {
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
        
    	CCL4 cc4 = new CCL4();
    	try {
            image = ImageIO.read(new File(CCL.class.getResource("one.png").toURI()));
            input = thresholdImage(image, 128);
            ImageIO.write(thresholdImage(image, 128), "png", new File("one" + "." + "png"));
            cc4.firstPass();
            cc4.secondPass();
            //System.out.println("after 2nd pass:");
            cc4.output();
                
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
