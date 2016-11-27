
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KD
 */
public class Combination {
    
    public static void main(String args[]){
        int x = 0;
        int[] number = {1,2,3,4,5,6};
        List<List<Integer>> combs = new ArrayList<>();
        
        for(int i=0; i<number.length; i++){
            for(int j=i+1; j<number.length; j++){
                List<Integer> row = new ArrayList<>();
                row.add(number[i]);
                row.add(number[j]);
                combs.add(x, row);
                x++;
            }
        }
        
        System.out.println(combs);
    }
}
