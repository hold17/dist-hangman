package dk.localghost.hold17.client;

import java.util.Scanner;

public class UserInteraction {
    private static Scanner sc = new Scanner(System.in);
    public static String getString(String message) {
        System.out.print(message + ": ");

        String line = sc.nextLine();
        return line;
    }

    public static String getLetter() {
        String letter = "";

        while(letter.length() != 1) {
            letter = getString("Guess a letter");

            // cheat
            if (letter.equals("isuckathangman"))
                break;
        }

        System.out.println("\n\n\n\n\n\n");
        return letter;
    }
}
