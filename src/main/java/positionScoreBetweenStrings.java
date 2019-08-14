import java.util.ArrayList;

// need to make editDistance and nGram : public in fuzzyMatch.java to run this code!!

public class positionScoreBetweenStrings extends fuzzyMatch {
    public static void main(String[] args) {
        System.out.println(editDistance("Hello", "Hero"));

        String string1 = "one two";
        String string2 = "two one";

        string1 = string1.toLowerCase().trim().replaceAll(" +", " ");
        string2 = string2.toLowerCase().trim().replaceAll(" +", " ");

        ArrayList<String> ngram1 = nGram(1,string1);
        ArrayList<String> ngram2 = nGram(1,string2);
        System.out.println(ngram1 + "\n" +ngram2);

        ArrayList<String> intersection = new ArrayList<String>(ngram1);
        intersection.retainAll(ngram2);
        double commonTerms = intersection.size();

        int longestDist = ngram1.size(), shortestDist = ngram2.size();
        if(ngram2.size() > ngram1.size()) {
            longestDist = ngram2.size();
            shortestDist = ngram1.size();
        }
//        System.out.println(shortestStr);
        double pos = 0;
//        System.out.println(ngram2.get(2) + " and " + ngram1.get(2));

        // can get distance between two
        System.out.println("index of: "+ngram1.indexOf(ngram2.get(0)));
        System.out.println("index of: " +ngram2.indexOf(ngram1.get(0)));
        int offset = -1, counter=0, diff=0;


        for(int i =0; i<shortestDist; i++){
            if(ngram1.indexOf(ngram2.get(i)) != -1) {
//                System.out.println(ngram1.get(i));
//                System.out.println("intersection: "+intersection);
                pos++;
                // takes order into consideration (ex: this is my is vs. this is my friend)
                if(ngram1.get(i).equals(ngram2.get(i)) && ngram1.indexOf(ngram2.get(i)) == ngram2.indexOf(ngram1.get(i))){
                    // don't count words that have the same index and same word
                    pos--;
                    // indices are not equal
                } else if(ngram1.indexOf(ngram2.get(i)) != ngram2.indexOf(ngram1.get(i))) {
                    pos--;
                }
                // if only the first index is not equal but the rest are


//                else if((ngram1.indexOf(ngram2.get(0)) == -1 || ngram2.indexOf(ngram1.get(0)) == -1) && ngram1.indexOf(ngram2.get(i)) != ngram2.indexOf(ngram1.get(i))) {
//                    System.out.println("I wanna");
//                    pos--;
//                }
//                else if(ngram1.contains(ngram2.get(i)) && ngram1.indexOf(ngram2.get(0)) != ngram2.indexOf(ngram1.get(0))) {
//                    System.out.println("here?");
//                    pos--;
//                }
//                else if(diff == 1) {
//                    System.out.println("shout");
//                }

//                if(!intersection.contains(ngram1.get(i))) {
//
//                    System.out.println(ngram1.get(i));
////                    ngram1.remove(ngram1.get(i));
////                    i = ngram1.indexOf(ngram1.get(i))
//
//                    System.out.println("array: "+ngram1);
//
//                }
            }
        }
        System.out.println("Number of mismatches: "+pos);

    }
}

















//        System.out.println("Common terms: " +commonTerms);
//        System.out.println("Common items: "+intersection);

//        int pos =0;
// has time complexity of O(n^2)
//        for(int i=0; i<ngram1.size(); i++) {
//            for(int j=0; j<ngram2.size(); j++) {
//                int longerDist =i, shorterDist=j;
//                if(j>i) {
//                    longerDist = j;
//                    shorterDist = i;
//                }
//                int diff = longerDist - shorterDist;
//                if(ngram1.get(i).equals(ngram2.get(j))) {
//                    System.out.println("True: [" +i+"] ["+j+"]" + " - distance: "+diff);
//                    if(diff == 0) {
//                        pos--;
//                    }
//                    pos++;
//                } else {
//                    System.out.println("False: [" +i+"] ["+j+"]" + " - distance: "+diff);
//                }
//            }
//        }
//        System.out.println("Total number of positions to replace: " +pos);
