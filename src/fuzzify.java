import java.util.ArrayList;

public class fuzzify {

    public static void main(String[] args) {

//        System.out.println(compareScore("hello", "hallo"));
        System.out.println(fuzzyMatch("Hello my. name. is U.S.A", "You know. what they. call me"));
    }

    public static String fuzzyMatch(String str1, String str2) {
        // TODO: get user input

        // lowercase each string
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        // tokenize each string (displayed in characters)
        String[] firstString = str1.split("//.");
        String[] secondString = str2.split("");
        for(String token : firstString) {
            System.out.print(token + " / ");
        }
        System.out.print("\n");
        for(String token : secondString) {
            System.out.print(token + " / ");
        }

        // convert string to bi-gram (2 ngram)



        return "\ntesting";
    }














    // PROBLEM with this one:
    // - only compares with first string's length
    // - no user input
    // - can go out of bounds
    // - semi hard-coded
    public static String compareScore(String s1, String s2) {
        // lowercase each string
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        // tokenize string into characters and compare each character
        int counter = 0;
        for(int i=0; i<s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if(c1 == c2) {
                counter++;
            }
        }
        double result = ((double) counter / (double) s1.length()) * 100;
        String msg = "The score between both string is: "+ counter + "/" +s1.length() +" = " + result+"%";
        return msg;
    }
}
