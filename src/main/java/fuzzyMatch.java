import java.util.ArrayList;

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

    private static ArrayList<String> nGram(int n, String str) {
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

    // Levenshtein Edit Distance
    private static Double editDistance(String string1, String string2) {
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

    // another string similarity matching
    private static Double jaccardIndex(double t, int stringLength1, int stringLength2) {
        double J;
        J = t/(stringLength1+stringLength2);
        return J;
    }

    // find biGram of a word

    /**
     * something
     * @param str
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
