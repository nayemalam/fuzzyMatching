import info.debatty.java.stringsimilarity.*;
import java.util.ArrayList;
import java.util.Arrays;

public class fuzzyUpdatedRepo {

    public static void main(String[] args) {
        String[] testStrings =
                {"the cat in the hat", "the hat in the cat",
                        "this is my", "this my is",
                        "nayem       alam", "AlaM NaYem",
                        "night", "nacht"
                };

        runFuzzyMatching(testStrings, 0.05, 2);

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

    private static void runFuzzyMatching(String[] testStrings, Double spaceScore, int numberOfGrams) {

        String testString1, testString2;
        // with Edit Distance
        for(int i = 0; i<testStrings.length-1; i+=2) {
            testString1 = testStrings[i].toLowerCase().trim().replaceAll(" +", " ");
            testString2 = testStrings[i+1].toLowerCase().trim().replaceAll(" +", " ");

            Double editDistance = editDistance(testString1, testString2);
            Double fuzzyEdit = FuzzyWithEditDistance(testString1, testString2);

            System.out.println("EDIT DISTANCE: the similarity between " + "(" +testString1+ ", " +testString2+ ") is: " +fuzzyEdit+ ", with edit distance of: " +editDistance);
        }
        System.out.println(" ");
        // with Dice Coefficient
        for(int i = 0; i<testStrings.length-1; i+=2) {
            testString1 = testStrings[i].toLowerCase().trim().replaceAll(" +", " ");
            testString2 = testStrings[i+1].toLowerCase().trim().replaceAll(" +", " ");
            Double fuzzyDice = FuzzyWithDiceCoefficient(testString1, testString2, spaceScore, numberOfGrams);
            ArrayList<String> ngram1 = nGram(numberOfGrams, testString1);
            ArrayList<String> ngram2 = nGram(numberOfGrams, testString2);
            double diceTerms = getCommonTerms(ngram1, ngram2).size();

            System.out.println("DICE COEFFICIENT: the similarity between " + "(" +ngram1+ ", " +ngram2+ ") is: " +fuzzyDice+ ", with common terms of: " +diceTerms);
        }

    }

    private static Double FuzzyWithDiceCoefficient(String string1, String string2, Double spaceScore, int numberOfGrams) {
        double result = 0.0, commonTerms;

        if(string1 == null || string1.length() == 0 || string2 == null || string2.length() == 0) {
            System.err.println("CANNOT LEAVE ANY OF THE STRINGS BLANK");
            return result;
        } else {
            // normalize everything
            string1 = string1.toLowerCase().trim().replaceAll(" +", " ");
            string2 = string2.toLowerCase().trim().replaceAll(" +", " ");

            // get nGram of input strings
            ArrayList<String> ngram1 = nGram(numberOfGrams, string1);
            ArrayList<String> ngram2 = nGram(numberOfGrams, string2);
//            System.out.println("First diced: " + ngram1 + "\nSecond diced: " + ngram2);

            // find common elements
            ArrayList<String> intersection = getCommonTerms(ngram1, ngram2);
//            System.out.println("Intersection: "+intersection);
            commonTerms = intersection.size();

            // account for positioning of words
            double matches = getWordMatches(ngram1, ngram2);
//            System.out.println("Number of mismatches: " +matches);
            spaceScore *= matches;

            result = diceFunction(commonTerms, spaceScore, ngram1.size(), ngram2.size());
//            System.out.println("\nDICE COEFFICIENT: The similarity between (" + string1 + ", " + string2 + ") is: " + result + ", with common terms of: " + commonTerms);
        }
        return result;
    }

    private static ArrayList<String> getCommonTerms(ArrayList<String> ngram1, ArrayList<String> ngram2) {
        ArrayList<String> intersection = new ArrayList<String>();

        int longestDist = ngram1.size();
        int shortestDist = ngram2.size();
        ArrayList<String> longestArr = ngram1;
        ArrayList<String> shortestArr = ngram2;

        if(ngram2.size() > ngram1.size()) {
            longestDist = ngram2.size();
            shortestDist = ngram1.size();
            longestArr = ngram2;
            shortestArr = ngram1;
        }
        for(int i =0; i<longestDist; i++) {
            if(shortestArr.contains(longestArr.get(i))) {
                intersection.add(longestArr.get(i));
            }
        }
        return intersection;
    }

    private static Double getWordMatches(ArrayList<String> ngram1, ArrayList<String> ngram2) {
        double pos = 0;

        int longestDist = ngram1.size();
        int shortestDist = ngram2.size();
        if(ngram2.size() > ngram1.size()) {
            longestDist = ngram2.size();
            shortestDist = ngram1.size();
        }
        for(int i =0; i<shortestDist; i++) {
            if (ngram1.indexOf(ngram2.get(i)) != -1) {
//                System.out.println(ngram1.get(i));
//                System.out.println("intersection: "+intersection);
                pos++;
                // takes order into consideration (ex: this is my is vs. this is my friend)
                if (ngram1.get(i).equals(ngram2.get(i)) && ngram1.indexOf(ngram2.get(i)) == ngram2.indexOf(ngram1.get(i))) {
                    // don't count words that have the same index and same word
                    pos--;
                    // indices are not equal
                } else if (ngram1.indexOf(ngram2.get(i)) != ngram2.indexOf(ngram1.get(i))) {
                    pos--;
                }
            }
        }
        return pos;
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
//                if (n == 1) {
//                    result.add(Character.toString(str.charAt(i)));
////                    System.out.print("'" + str.charAt(i) + "' ");
//                }
                result.add(str.substring(i, i+n));

            }
        }
        return result;
    }

    private static Double FuzzyWithEditDistance(String string1, String string2) {
        double result = 0;

        // normalize everything
        string1 = string1.toLowerCase().trim().replaceAll(" +", " ");
        string2 = string2.toLowerCase().trim().replaceAll(" +", " ");

        // ensure we are dividing by the longest length
        String longestString = string1;
        String shortestString = string2;
        if(string1.length() < string2.length()) {
            longestString = string2;
            shortestString = string1;
        }
        if(longestString.length() == 0) {
            System.out.print("Please enter a string.");
            return result;
        }
        result = ((longestString.length()-editDistance(longestString, shortestString)) / (double) longestString.length());

//        System.out.println("EDIT DISTANCE: The similarity between (" + string1 + ", " + string2 + ") is: " + result + ", using edit distance of: " + editDistance(string1, string2));

        return result;
    }

    // Levenshtein Edit Distance
    public static Double editDistance(String string1, String string2) {
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
