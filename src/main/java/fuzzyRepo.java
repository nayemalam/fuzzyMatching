import info.debatty.java.stringsimilarity.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class fuzzyRepo {

    public static void main(String[] args) {
        // test strings
        String s1 = "one two three four".toLowerCase().trim().replaceAll(" +", " ");
        String s2 = "four three two one".toLowerCase().trim().replaceAll(" +", " ");

        // with Edit Distance
        FuzzyWithEditDistance(s1,s2);
        // with Dice coefficient
        FuzzyWithDiceCoefficient(s1,s2,0.05,1);

        // externals
//        System.out.println("/====================/\nExternal Libraries:\n");
//        NormalizedLevenshtein n  = new NormalizedLevenshtein();
//        JaroWinkler jw = new JaroWinkler();
//        SorensenDice sd = new SorensenDice();
//        Jaccard j = new Jaccard();
//        System.out.println("Edit Distance Similarity: " + (1-n.distance(s1,s2)));
//        // best suited for person names
//        System.out.println("Jaro-Winkler Similarity: " + (1-jw.distance(s1,s2)));
//        System.out.println("Dice Similarity: (cannot indicate how many ngrams) " + sd.similarity(s1,s2));
    }

    private static Double FuzzyWithDiceCoefficient(String string1, String string2, Double spaceScore, int numberOfGram) {
        String name1 = "one two three four";
        String name3 = "four three two one";
        String name2 = "The, quick brown fo.x jumped! Yes? Indeed( )";
        String name4 = "   Donald    Trump    ";
        String name5 = "Trump Donald";
        double result, commonTerms;

        if(name1 == null || name1.length() == 0 || name3 == null || name3.length() == 0) {
            System.err.println("CANNOT LEAVE ANY OF THE STRINGS BLANK");
            return 0.0;
        } else {
            // normalize everything
            name1 = name1.toLowerCase().trim().replaceAll(" +", " ");
            name2 = name2.toLowerCase().trim().replaceAll(" +", " ");
            name3 = name3.toLowerCase().trim().replaceAll(" +", " ");
            string1 = string1.toLowerCase().trim().replaceAll(" +", " ");
            string2 = string2.toLowerCase().trim().replaceAll(" +", " ");

            // get nGram of input strings
            ArrayList<String> ngram1 = nGram(numberOfGram, string1);
            ArrayList<String> ngram2 = nGram(numberOfGram, string2);
//            System.out.println("First diced: " + ngram1 + "\nSecond diced: " + ngram2);

            // find common elements
            ArrayList<String> intersection = new ArrayList<String>(ngram1);
            intersection.retainAll(ngram2);
            commonTerms = intersection.size();

            int longestDist = ngram1.size(), shortestDist = ngram2.size();
            if(ngram2.size() > ngram1.size()) {
                longestDist = ngram2.size();
                shortestDist = ngram1.size();
            }
            // account for positioning of words
            double pos = 0;
            for(int i = 0; i<shortestDist; i++){
                if(ngram1.indexOf(ngram2.get(i)) >=0) {
                    pos++;
                    // takes order into consideration
                    if(ngram1.get(i).equals(ngram2.get(i)) && ngram1.indexOf(ngram2.get(i)) == ngram2.indexOf(ngram1.get(i))){
                        pos--;
                    } else if(ngram1.indexOf(ngram2.get(i)) != ngram2.indexOf(ngram1.get(i))) {
                        pos--;
                    }
                }
            }
            System.out.println("Number of mismatches: "+pos);
            System.out.println("Common terms: "+intersection);
            spaceScore *= pos;
            result = diceFunction(commonTerms, spaceScore, ngram1.size(), ngram2.size());
            System.out.println("\nDICE COEFFICIENT: The similarity between (" + string1 + ", " + string2 + ") is: " + result + ", with common terms of: " + commonTerms);
        }
        return result;
    }

    private static Double diceFunction(double t, double spaceScore, int stringLength1, int stringLength2) {
        double result = 0;
        // users are able to initialize how much they'd like to score each space
        if(spaceScore < 0) {
            System.err.println("CANNOT ASSIGN A NEGATIVE SPACESCORE");
        } else {
            result = (2 * t) / (double) (stringLength1 + stringLength2) - spaceScore;
        }
        return result;
    }

    public static ArrayList<String> nGram(int n, String str) {
        ArrayList<String> ngrams = new ArrayList<String>();
        // get spaces to distinguish between 1 word vs. many words
        int numberOfSpaces = 0;
        for(char space : str.toCharArray()) {
            if(space == ' ') {
                numberOfSpaces++;
            }
        }
//        System.out.println("The number of spaces: " +numberOfSpaces);
        if(numberOfSpaces > 0) {
            // handle any other use cases
            if (str.contains(",") || str.contains("!") || str.contains("?") || str.contains("(") || str.contains(")") || str.contains(".")) {
                str = str.trim().replaceAll("[^a-zA-Z ]", "");
//            System.out.println(str);
            }
            if (str.contains(" ")) {
//            System.out.print(nGramofManyWords(n, str));
                ngrams = nGramofManyWords(n, str);
//                System.out.println(ngrams);
            }
        } else {
//            System.out.println("One of the given strings do not have any spaces");
            ngrams = nGramOfOneWord(n, str);
        }
        return ngrams;
    }

    private static ArrayList<String> nGramofManyWords(int n, String str) {
        ArrayList<String> words = new ArrayList<String>();
        // split sentence into array of substrings
        String[] split = str.split(" ");
        if(n > split.length) {
            System.err.println("One or both of the nGrams chosen is larger than the word length. Hint: word length = " +split.length);
        }
        for(int i=0; i<split.length-n +1; i++) {
            words.add(append(split, i,i+n));
        }
        return words;
    }

    private static ArrayList<String> nGramOfOneWord(int n, String str) {
//        String[] result = new String[str.length() -1];
        ArrayList<String> result = new ArrayList<String>();
        if (n>str.length()) {
            System.err.println("One or both of the nGrams chosen is larger than the word length. Hint: word length = " +str.length());
        } else {
//            System.out.println("\nThe " + n + "Gram of: '" + str + "':");
            // TODO: handle when n == 0
            for (int i = 0; i <= str.length() - n; i++) {
                if (n == 1) {
                    result.add(Character.toString(str.charAt(i)));
//                    System.out.print("'" + str.charAt(i) + "' ");
                } else {
                    result.add(str.substring(i, i+n));
//                    result[i] = str.substring(i, i + n);
//                    System.out.print("'" + result[i].trim().replaceAll(" +", " ") + "' ");
                }
            }
//            System.out.println("\n-> cardinality: " + result.length);
        }
        return result;
    }

    private static Double FuzzyWithEditDistance(String string1, String string2) {
        double result;
        // ensure we are dividing by the longest length
        String longestString = string1;
        String shortestString = string2;
        if(string1.length() < string2.length()) {
            longestString = string2;
            shortestString = string1;
        }
        if(longestString.length() == 0) {
            System.out.print("Please enter a string.");
        }
        result = ((longestString.length()-editDistance(longestString, shortestString)) / (double) longestString.length());

        System.out.println("EDIT DISTANCE: The similarity between (" + string1 + ", " + string2 + ") is: " + result + ", using edit distance of: " + editDistance(string1, string2));

        return result;
    }

    // Levenshtein Edit Distance
    public static Double editDistance(String string1, String string2) {
        // lowercase all inputs for consistency
        string1 = string1.toLowerCase();
        string2 = string2.toLowerCase();

        // normalize chain of spaces
        string1 = string1.trim().replaceAll(" +", " ");
        string2 = string2.trim().replaceAll(" +", " ");

        // perform scoring - edit distance
        // if similar (cost)
        int[] cost = new int[string2.length() +1]; // +1 for seed cell (remainder) -- just a number

        for(int i = 0; i <= string1.length(); i++) {
            int lastIndex = i;
            for(int j = 0; j <= string2.length(); j++) {
                if(i == 0) {
                    cost[j] = j; // handle out of bounds
                } else if(j > 0) {
                    // when right cell value > left cell value
                    int newVal = cost[j-1];
                    // when right cell value < left cell value
                    if(string1.charAt(i-1) != string2.charAt(j-1)) {
                        newVal = min(newVal, lastIndex, cost[j]) + 1;
                    }
                    cost[j - 1] = lastIndex;
                    lastIndex = newVal;
                }
            }
            if(i > 0) {
                cost[string2.length()] = lastIndex; // should be the result (the final pos)
            }
        }
        return (double)cost[string2.length()];
    }

    // another string similarity matching
    private static Double jaccardIndex(double t, int stringLength1, int stringLength2) {
        double J;
        J = t/(stringLength1+stringLength2);
        return J;
    }

    // find biGram of a word
    private static void biGram(String str) {
        String[] result = new String[str.length() -1];
        System.out.println("The biGram of " +str + ":");
        for(int i=0; i<=str.length() - 2; i++) {
            result[i] = str.substring(i, i+2);
            System.out.print("'"+result[i].trim().replaceAll(" +", " ")+"' ");
        }
        System.out.println("\n-> cardinality: "+result.length);

    }

    // HELPERS
    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a,b) ,c);
    }

    private static String append(String[] words, int startPos, int endPos) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = startPos; i < endPos; i++) {
            if(i > startPos) {
                stringBuilder.append(" ").append(words[i]);
            } else {
                stringBuilder.append(words[i]);
            }
        }
        return stringBuilder.toString();
    }

}
