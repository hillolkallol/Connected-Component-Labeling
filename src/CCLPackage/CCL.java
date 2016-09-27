/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CCLPackage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author KD
 */

public class CCL {
    
    private int[][] labeling;
    private static int newLabel = 1;
    private int minLabel;
    private static BufferedImage input;
    private boolean flag = false; //finding out whether I need to increment newLabel value or not
    
    public void processing(){
        
        labeling = new int[input.getWidth()][input.getHeight()]; //initialize the 'labeling' as the size of the original image
        
        //first pass
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
                minLabel = newLabel;
                
                //finding out the minimum labeled neighbours (privious neighbour and three from the top) with same RGB color
                if(j-1>=0 && (input.getRGB(i, j) == input.getRGB(i, j-1))){
                        minLabel = labeling[i][j-1];
                        flag = true;
                }
                if(i-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j))){
                        minLabel = labeling[i-1][j];
                        flag = true;
                }
                if(i-1>=0 && j-1>=0 && (input.getRGB(i, j) == input.getRGB(i-1, j-1))){
                        minLabel = labeling[i-1][j-1];
                        flag = true;
                }
                if(i-1>=0 && j+1<input.getHeight() && (input.getRGB(i, j) == input.getRGB(i-1, j+1))){
                        minLabel = labeling[i-1][j+1];
                        flag = true;
                }
                
                if(flag == false){
                    newLabel++;
                }
                flag = false;
                labeling[i][j] = minLabel;               
            }
        }
        
        //second pass
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
                
                minLabel = labeling[i][j];
                
                //finding out the minimum labeled neighbours (from all side of the pixel) with same RGB color
                if(j-1>=0 && input.getRGB(i, j) == input.getRGB(i, j-1)){
                    if(minLabel > labeling[i][j-1])
                        minLabel = labeling[i][j-1];
                }
                if(i-1>=0 && j-1>=0 && input.getRGB(i, j) == input.getRGB(i-1, j-1)){
                    if(minLabel > labeling[i-1][j-1])
                        minLabel = labeling[i-1][j-1];
                }
                if(i-1>=0 && input.getRGB(i, j) == input.getRGB(i-1, j)){
                    if(minLabel > labeling[i-1][j])
                        minLabel = labeling[i-1][j];
                }
                if(i-1>=0 && j+1<input.getHeight() && input.getRGB(i, j) == input.getRGB(i-1, j+1)){
                    if(minLabel > labeling[i-1][j+1])
                        minLabel = labeling[i-1][j+1];
                }
                if(j+1<input.getHeight() && input.getRGB(i, j) == input.getRGB(i, j+1)){
                    if(minLabel > labeling[i][j+1])
                        minLabel = labeling[i][j+1];
                }
                if(i+1<input.getWidth() && j+1<input.getHeight() && input.getRGB(i, j) == input.getRGB(i+1, j+1)){
                    if(minLabel > labeling[i+1][j+1])
                        minLabel = labeling[i+1][j+1];
                }
                if(i+1<input.getWidth() && input.getRGB(i, j) == input.getRGB(i+1, j)){
                    if(minLabel > labeling[i+1][j])
                        minLabel = labeling[i+1][j];
                }
                if(i+1<input.getWidth() && j-1>=0 && input.getRGB(i, j) == input.getRGB(i+1, j-1)){
                    if(minLabel > labeling[i+1][j-1])
                        minLabel = labeling[i+1][j-1];
                }
                labeling[i][j] = minLabel;
            }
        }
    }
    
    public void output() throws IOException{
        
        for(int value=1; value<newLabel; value++){
            //creating a new temporary buffered image with transparent background
            BufferedImage OutputImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TRANSLUCENT);
            for(int i=0; i<input.getWidth(); i++){
                for(int j=0; j<input.getHeight(); j++){
                    if(labeling[i][j] == value){ //painting into new image with the original RGB with same labeling
                        OutputImage.setRGB(i, j, input.getRGB(i, j));
                    }
                }
            }
            // getting new images
            ImageIO.write(OutputImage, "png", new File("a" + value + "." + "png"));
        }
    }
    
    
    public static void main(String[] args) throws IOException {
    	CCL ccl = new CCL();
    	try {
                input = ImageIO.read(new File(CCL.class.getResource("a.png").toURI()));
                //System.out.println(input.getRGB(9, 9));
                ccl.processing();
                //System.out.println(newLabel);
                ccl.output();
                
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
