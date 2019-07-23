public class fuzzyMatch {

    public static void main(String[] args) {
        // test strings
        String s1 = "sitting";
        String s2 = "kitten";
        System.out.println("FuzzyMatching using edit distance: "+FuzzyWithEditDistance(s1, s2) + ", with edit distance of: " +editDistance(s1,s2));
    }

    public static Double diceCoefficient(String string1, String string2, Double spaceScore, int numberOfGram) {
        String name1 = "the    bat     man";
        String name2 = "the man";
        String name3 = "   Donald    Trump    ";
        String name4 = "Trump Donald";
        String name5 = "Truman Donald";

        // need to have spaceScore > 0 to then do the matching
        // add a weight to a spacing -> 1 space could be like 5-10%
        // assign a position value
        if (spaceScore > 0.0) {

        }

        return 0.0;
    }

    private static Double FuzzyWithEditDistance(String string1, String string2) {
        // ensure we are dividing by the longest length
        String longestString = string1;
        String shortestString = string2;
        if(string1.length() < string2.length()) {
            longestString = string2;
            shortestString = string1;
        }
        int longestLength = string1.length();
        if(longestLength == 0) {
            System.out.print("Please enter a string.");
        }
        double result = ((longestLength-editDistance(longestString, shortestString)) / (double) longestLength);
        return result;
    }

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
