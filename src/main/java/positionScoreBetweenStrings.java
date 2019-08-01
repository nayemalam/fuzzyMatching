import sun.jvm.hotspot.ui.treetable.AbstractTreeTableModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class positionScoreBetweenStrings extends fuzzyMatch {
    public static void main(String[] args) {
        System.out.println(editDistance("Hello", "Hero"));

        String string1 = "this is my";
        String string2 = "this my is";

        string1 = string1.toLowerCase().trim().replaceAll(" +", " ");
        string2 = string2.toLowerCase().trim().replaceAll(" +", " ");

        ArrayList<String> ngram1 = nGram(1,string1);
        ArrayList<String> ngram2 = nGram(1,string2);
        System.out.println(ngram1 + "\n" +ngram2);

        ArrayList<String> intersection = new ArrayList<String>(ngram1);
        intersection.retainAll(ngram2);
        double commonTerms = intersection.size();
        System.out.println("Common terms: " +commonTerms);
        System.out.println("Common items: "+intersection);
//        for(String token : ngram1) {
//            System.out.println(token);
//            for(String token2 : ngram2) {
//                if(token.equals(token2)) {
//                    System.out.println("True: [" +token+"] ["+token2+"]");
//                } else {
//                    System.out.println("False: [" +token+"] ["+token2+"]");
//                }
//            }
//        }
        int pos =0;
        for(int i=0; i<ngram1.size(); i++) {
            for(int j=0; j<ngram2.size(); j++) {
                int longerDist =i, shorterDist=j;
                if(j>i) {
                    longerDist = j;
                    shorterDist = i;
                }
                int diff = longerDist - shorterDist;
                if(ngram1.get(i).equals(ngram2.get(j))) {
                    System.out.println("True: [" +i+"] ["+j+"]" + " - distance: "+diff);
                    if(diff == 0) {
                        pos--;
                    }
                    pos++;
                } else {
                    System.out.println("False: [" +i+"] ["+j+"]" + " - distance: "+diff);
                }
            }
        }
        System.out.println("Total number of positions to replace: " +pos);
    }
}
