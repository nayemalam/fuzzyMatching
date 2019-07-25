import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class fuzzyMatch {

    public static void main(String[] args) {
        // test strings
        String s1 = "Donald     Trump".toLowerCase().trim().replaceAll(" +", " ");
        String s2 = "    Truman      Donald".toLowerCase().trim().replaceAll(" +", " ");

        // with Edit Distance
        if(s1.length() > 25 || s2.length() >25) {
            System.out.println("The similarity is: " +FuzzyWithEditDistance(s1, s2) + ", using edit distance of: " +editDistance(s1,s2));
        } else {
            System.out.println("The similarity between (" + s1 + ", " + s2 + ") is: " + FuzzyWithEditDistance(s1, s2) + ", using edit distance of: " + editDistance(s1, s2));
        }
        // the similarity between, with common dice terms of:

        // with Dice coefficient
        FuzzyWithDiceCoefficient(s1,s2,1.0,2);
    }

    public static Double FuzzyWithDiceCoefficient(String string1, String string2, Double spaceScore, int numberOfGram) {
        String name1 = "Hello the cat jumped over";
        String name2 = "The, quick brown fo.x jumped! Yes? Indeed( )";
        String name3 = "Hello the dog jumped over";
        String name4 = "   Donald    Trump    ";
        String name5 = "Trump Donald";

        // normalize everything
        name1 = name1.toLowerCase().trim().replaceAll(" +", " ");
        name2 = name2.toLowerCase().trim().replaceAll(" +", " ");
        name3 = name3.toLowerCase().trim().replaceAll(" +", " ");

        // implements the Sørensen Dice Coefficient
        ArrayList<String> ngram1 = nGram(2, name1);
        ArrayList<String> ngram2 = nGram(2, name3);

        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        for(String token : ngram1) {
            set1.addAll(Collections.singletonList(token));
        }
        for(String token : ngram2) {
            set2.addAll(Collections.singletonList(token));
        }
        System.out.println("First diced: "+set1 + "\nSecond diced: " +set2);

        Set<String> intersection = new HashSet<String>(set1);
        intersection.retainAll(set2);
        double t = intersection.size();
        System.out.println("Common terms: " +t);

        System.out.println("The Dice Coefficient is: " +diceFunction(t, 0.0, set1.size(), set2.size()));

        return 0.0;
    }

    private static Double diceFunction(double t, double spaceScore, int stringLength1, int stringLength2) {
        double result;
        // users are able to initialize how much they'd like to score each space
        result = (2*t)/(double)(stringLength1 + stringLength2) + spaceScore;
        return result;
    }

    private static ArrayList<String> nGram(int n, String str) {
        ArrayList<String> ngrams = new ArrayList<String>();
        int numberOfSpaces = 0;
        for(char c : str.toCharArray()) {
            if(c == ' ') {
                numberOfSpaces++;
            }
        }
        System.out.println("The number of spaces: " +numberOfSpaces);
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
            System.out.println("nGrams chosen is larger than the word length. Hint: word length = " +split.length);
        }
        for(int i=0; i<split.length-n +1; i++) {
            words.add(concat(split, i,i+n));
        }
        return words;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i < end; i++) {
            if(i > start) {
                sb.append(" ").append(words[i]);
            } else {
                sb.append(words[i]);
            }
        }
        return sb.toString();
    }

    private static ArrayList<String> nGramOfOneWord(int n, String str) {
//        String[] result = new String[str.length() -1];
        ArrayList<String> result = new ArrayList<String>();
        if (n>str.length()) {
            System.out.println("nGrams chosen is larger than the word length. Hint: word length = " +str.length());
        } else {
//            System.out.println("\nThe " + n + "Gram of: '" + str + "':");
            for (int i = 0; i <= str.length() - n; i++) {
                if (n == 1) {
                    result.add(Character.toString(str.charAt(i)));
//                    System.out.print("'" + str.charAt(i) + "' ");
                } else {
                    result.add(str.substring(i,i+n));
//                    result[i] = str.substring(i, i + n);
//                    System.out.print("'" + result[i].trim().replaceAll(" +", " ") + "' ");
                }
            }
//            System.out.println("\n-> cardinality: " + result.length);
        }
        return result;
    }

    // another string similarity matching
    private static Double jaccardIndex(double t, int stringLength1, int stringLength2) {
        double J;
        J = t/(stringLength1+stringLength2);
        return J;
    }

    private static void biGram(String str) {
        String[] result = new String[str.length() -1];
        System.out.println("The biGram of " +str + ":");
        for(int i=0; i<=str.length() - 2; i++) {
            result[i] = str.substring(i, i+2);
            System.out.print("'"+result[i].trim().replaceAll(" +", " ")+"' ");
        }
        System.out.println("\n-> cardinality: "+result.length);

    }


    private static Double FuzzyWithEditDistance(String string1, String string2) {
        double result = 0.0;
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
        return result;
    }

    // Levenshtein Edit Distance
    private static Double editDistance(String string1, String string2) {
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

    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a,b) ,c);
    }

}
