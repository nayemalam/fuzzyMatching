import java.util.ArrayList;

/**
 * <a href="https://en.wikipedia.org/wiki/Approximate_string_matching">Fuzzy Matching</a> implemented to compare the similarity between two given strings
 * <br>Both strings can be of any length and may contain multiple words
 * <br>Objective is to return a score between [0.0, 1.0] based on how similar the two input strings are
 * @author Nayem Alam
 */
public class fuzzyMatch {

    public static void main(String[] args) {
        String[] testStrings =
                {"the cat in the hat", "the hat in the cat",
                 "this is my", "this my is",
                 "nayem       alam", "AlaM NaYem",
                 "night", "nacht"
                };

        runFuzzyMatching(testStrings, 0.0, 1);
    }

    /**
     * This method performs two Fuzzy Matching algorithms and outputs on Print Stream
     * <br>test method to run FuzzyMatching with both
     * <i><a href="#FuzzyWithDiceCoefficient-java.lang.String-java.lang.String-java.lang.Double-int-">FuzzyWithDiceCoefficient</a></i> and
     * <i><a href="#FuzzyWithEditDistance-java.lang.String-java.lang.String-">FuzzyWithEditDistance</a></i>
     * @param testStrings list of string pairs
     * @param spaceScore score between [0.0, 1.0]
     * @param numberOfGrams number of grams
     */
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

            System.out.println("Jaccard Coefficient: " +jaccardIndex(diceTerms, ngram1.size(), ngram2.size()));

            System.out.println("DICE COEFFICIENT: the similarity between " + "(" +ngram1+ ", " +ngram2+ ") is: " +fuzzyDice+ ", with common terms of: " +diceTerms);
        }

    }

    /**
     * This method performs the fuzzy similarity matching using the dice coefficient
     * @param string1 first input string (one or many words)
     * @param string2 second input string (one or many words)
     * @param spaceScore score between [0.0, 1.0]
     * @param numberOfGrams number of grams
     * @return a value between [0.0,1.0] which gives the similarity between two words based on the dice coefficient - values closer to <i>1.0</i>
     * have higher similarity
     */
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

            // find common elements
            ArrayList<String> intersection = getCommonTerms(ngram1, ngram2);
            commonTerms = intersection.size();

            // account for positioning of words
            double matches = getWordMatches(ngram1, ngram2);
            spaceScore *= matches;

            result = diceFunction(commonTerms, spaceScore, ngram1.size(), ngram2.size());
        }
        return result;
    }

    /**
     * This method finds all the common elements between two array lists
     * <br>alternate to <a href="https://www.geeksforgeeks.org/arraylist-retainall-method-in-java/">retainAll() method</a>
     * @param ngram1 list of word(s) diced (i.e. with its corresponding ngrams)
     * @param ngram2 list of word(s) diced (i.e. with its corresponding ngrams)
     * @return the common terms between two lists
     */
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

    /**
     * This method identifies the positioning of similar words between two lists (i.e. different index but same word)
     * @param ngram1 list of word(s) diced (i.e. with its corresponding ngrams)
     * @param ngram2 list of word(s) diced (i.e. with its corresponding ngrams)
     * @return the number of similar words that are in different positions
     */
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
                pos++;
                // takes order into consideration
                if (ngram1.get(i).equals(ngram2.get(i)) && ngram1.indexOf(ngram2.get(i)) == ngram2.indexOf(ngram1.get(i))) {
                    // don't count words that have the same position and same word
                    pos--;
                    // words that match but do not have the same index
                } else if (ngram1.indexOf(ngram2.get(i)) != ngram2.indexOf(ngram1.get(i))) {
                    pos--;
                }
            }
        }
        return pos;
    }

    /**
     * This method performs the <a href="https://en.wikipedia.org/wiki/S%C3%B8rensen%E2%80%93Dice_coefficient">Sørensen–Dice coefficient</a>
     * @param t number of common elements between the two input strings
     * @param spaceScore score that accounts for similar words but positions are mismatched
     * @param stringLength1 length of string 1
     * @param stringLength2 length of string 2
     * @return the dice coefficient between two strings
     */
    private static Double diceFunction(double t, double spaceScore, int stringLength1, int stringLength2) {
        double result = 0;
        // users are able to initialize how much they'd like to score for every mismatch in position of words
        if(spaceScore < 0) {
            System.err.println("CANNOT ASSIGN A NEGATIVE SPACESCORE");
        } else {
            result = (2 * t) / (double) (stringLength1 + stringLength2) - spaceScore;
        }
        return result;
    }

    /**
     * This method takes care of both one word and multiple words and removes punctuations from any word
     * <br>uses both <i><a href="#nGramofManyWords-int-java.lang.String-">nGramofManyWords</a></i>
     * and <i><a href="#nGramOfOneWord-int-java.lang.String-">nGramOfOneWord</a></i>
     * @param n number of grams
     * @param str any string
     * @return the unigram, bigram, trigram, four-gram, etc... (depending of chosen <i>n</i>) of input <i>str</i>
     */
    public static ArrayList<String> nGram(int n, String str) {
        ArrayList<String> ngrams = new ArrayList<String>();
        // get spaces to distinguish between 1 word vs. many words
        int numberOfSpaces = 0;
        for(char space : str.toCharArray()) {
            if(space == ' ') {
                numberOfSpaces++;
            }
        }
        if(numberOfSpaces > 0) {
            // handle any other use cases
            if (str.contains(",") || str.contains("!") || str.contains("?") || str.contains("(") || str.contains(")") || str.contains(".")) {
                str = str.trim().replaceAll("[^a-zA-Z ]", "");
            }
            if (str.contains(" ")) {
                ngrams = nGramofManyWords(n, str);
            }
        } else {
            ngrams = nGramOfOneWord(n, str);
        }
        return ngrams;
    }

    /**
     * This method breaks multiple words into <a href="https://en.wikipedia.org/wiki/N-gram">ngrams</a>
     * @param n number of grams
     * @param str multiple words
     * @return the unigram, bigram, trigram, four-gram, etc... (depending of chosen <i>n</i>) of input <i>str</i>
     */
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

    /**
     * This method breaks one word into <a href="https://en.wikipedia.org/wiki/N-gram">ngrams</a>
     * <br>superior method to <i><a href="#biGram-java.lang.String-">biGram</a></i>
     * @param n number of grams
     * @param str one word
     * @return the unigram, bigram, trigram, four-gram, etc... (depending of chosen <i>n</i>) of input <i>str</i>
     */
    private static ArrayList<String> nGramOfOneWord(int n, String str) {
        ArrayList<String> result = new ArrayList<String>();
        if (n>str.length()) {
            System.err.println("One or both of the nGrams chosen is larger than the word length. Hint: word length = " +str.length());
        } else {
            for (int i = 0; i <= str.length() - n; i++) {
                result.add(str.substring(i, i+n));
            }
        }
        return result;
    }

    /**
     * This method performs the fuzzy similarity matching using the edit distance
     * @param string1 word or sentence
     * @param string2 word or sentence
     * @return a value between [0.0,1.0] which gives the similarity between two words based on their edit distance - values closer to <i>1.0</i> have higher similarity
     */
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

        return result;
    }

    /**
     * This method performs the <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein Edit Distance</a>
     * @param string1 word or sentence
     * @param string2 word or sentence
     * @return the edit distance between two strings
     */
    // Levenshtein Edit Distance
    public static Double editDistance(String string1, String string2) {
        // perform scoring - edit distance
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
                cost[string2.length()] = lastIndex;
            }
        }
        return (double)cost[string2.length()];
    }

    /**
     * This method performs the <a href="https://en.wikipedia.org/wiki/Jaccard_index">Jaccard Coefficient</a>
     * @param t number of common elements in both sets
     * @param stringLength1 number of elements in set 1
     * @param stringLength2 number of elements in set 2
     * @return the jaccard index
     */
    private static Double jaccardIndex(double t, int stringLength1, int stringLength2) {
        // formula from: https://en.wikipedia.org/wiki/S%C3%B8rensen%E2%80%93Dice_coefficient#Difference_from_Jaccard
        Double J;
        Double S = diceFunction(t,0.0, stringLength1, stringLength2);
        J = S/(2-S);
        return J;
    }

    /**
     * This method breaks one word into bigram(s) (2 adjacent elements from a string of tokens)
     * @param str one word input
     * <br>Print Streams the bigram(s) of word and the corresponding cardinality
     */
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

    /**
     * This method first returns the smaller value between a and b, and then returns the smaller value between (a,b) and c
     * @param a some value
     * @param b some value
     * @param c some value
     * @return the smaller of (a,b) and c
     */
    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a,b) ,c);
    }

    /**
     * This method appends a string of tokens
     * @param words sentence in an array
     * @param startPos first element in the array
     * @param endPos last element in the array
     * @return the appended words
     */
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
