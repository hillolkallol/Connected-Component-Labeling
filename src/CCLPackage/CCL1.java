/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CCLPackage;

import java.io.IOException;

/**
 *
 * @author KD
 */

public class CCL1 {
    
    private int[][] labeling;
    private static int newLabel = 1;
    private int minLabel;
    private static int[][] input;
    private boolean flag = false; //finding out whether I need to increment newLabel value or not
    private int width;
    private int height;
    private int[][] eLabels; //label equivalence relationships
    
    public void firstPass(){
        
        width = input.length;
        height = input[0].length;
        labeling = new int[width][height]; //initialize the 'labeling' as the size of the original image
        eLabels = new int[width][height];
        
        //first pass
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                minLabel = newLabel;
                
                //finding out the minimum labeled neighbours (privious neighbour and three from the top) with same RGB color
                if(j-1>=0 && (input[i][j] == input[i][j-1])){
                    if(minLabel > labeling[i][j-1]){    
                        minLabel = labeling[i][j-1];
                        flag = true;
                    }
                }
                if(i-1>=0 && (input[i][j] == input[i-1][j])){
                    if(minLabel > labeling[i-1][j]) {
                        minLabel = labeling[i-1][j];
                        flag = true;
                    }
                }
                if(i-1>=0 && j-1>=0 && (input[i][j] == input[i-1] [j-1])){
                    if(minLabel > labeling[i-1][j-1]) {
                        minLabel = labeling[i-1][j-1];
                        flag = true;
                    }
                }
                if(i-1>=0 && j+1<height && (input[i][j] == input[i-1] [j+1])){
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
                
                if(j-1>=0 && (input[i][j] == input[i][j-1])){
                    if(!isExist(labeling[i][j], labeling[i][j-1]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i][j-1];
                    if(!isExist(labeling[i][j-1], labeling[i][j]))
                        eLabels[labeling[i][j-1]][isEmpty(labeling[i][j-1])] = labeling[i][j];
                }
                if(i-1>=0 && (input[i][j] == input[i-1][j])){
                    if(!isExist(labeling[i][j], labeling[i-1][j]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i-1][j];
                    if(!isExist(labeling[i-1][j], labeling[i][j]))
                        eLabels[labeling[i-1][j]][isEmpty(labeling[i-1][j])] = labeling[i][j];
                }
                if(i-1>=0 && j-1>=0 && (input[i][j] == input[i-1] [j-1])){
                    if(!isExist(labeling[i][j], labeling[i-1] [j-1]))
                        eLabels[labeling[i][j]][isEmpty(labeling[i][j])] = labeling[i-1] [j-1];
                    if(!isExist(labeling[i-1][j-1], labeling[i][j]))
                        eLabels[labeling[i-1][j-1]][isEmpty(labeling[i-1][j-1])] = labeling[i][j];
                }
                if(i-1>=0 && j+1<height && (input[i][j] == input[i-1] [j+1])){
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
            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    System.out.print(labeling[i][j]);
                }
                System.out.println();
            }
            System.out.println("---------------------");
    }
    
    private boolean isExist(int x, int i0) {
        boolean mark = false;
        for (int a=0; a<eLabels[x].length; a++){
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
                System.out.print(min);
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
    
    public static void main(String[] args) throws IOException {
    	CCL1 ccl = new CCL1();
    	try {
                input = new int[][] {
                    {255, 0, 0, 0},
                    {255, 0, 255, 0},
                    {255, 0, 255, 0},
                    {255, 0, 255, 0},
                    {255, 255, 255, 0},
                    {0, 0, 0, 0},
                    {0, 255, 255, 0},
                    {0, 255, 255, 0},
                    {0, 0, 0, 0}
                };
                ccl.firstPass();
                ccl.secondPass();
                System.out.println("after 2nd pass:");
                ccl.output();
                
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}

/*
input 1:
{
{255, 0, 0, 0},
{255, 0, 255, 0},
{255, 0, 255, 0},
{255, 0, 255, 0},
{255, 255, 255, 0}
}

output 1:
1222
1212
1212
1212
1112

input 2:
{
{255, 0, 0, 0},
{255, 0, 255, 0},
{255, 0, 255, 0},
{255, 0, 255, 0},
{255, 255, 255, 0},
{0, 0, 0, 0},
{0, 255, 255, 0},
{0, 255, 255, 0},
{0, 0, 0, 0}
}

output 2:
1222
1212
1212
1212
1112
2222
2552
2552
2222
*/