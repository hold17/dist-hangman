package dk.localghost.hold17.ui;

public class GallowDrawer {
    public static void drawGallow(int wrongAnswers) {
        if (wrongAnswers < 0 || wrongAnswers > 6) return;

        String[] steps = {"" +
                "       |========",
                "       || //   |",
                "       ||//   ",
                "       ||/    ",
                "       ||    ",
                "       ||      ",
                "       ||     ",
                "       ||    ",
                "       ||",
                "    --------",
                "   /         \\",
                "__/            \\__"};

        String head = "( )";
        String leftArm1 = "/";
        String leftArm2 = "/ ";
        String body1 = "|";
        String body2 = "|";
        String body3 = "|";
        String rightArm1 = "\\";
        String rightArm2 = " \\";
        String leftLeg1 = "/";
        String leftLeg2 = "/ ";
        String rightLeg1 = " \\";
        String rightLeg2 = "  \\";

        switch (wrongAnswers) {
            default: break;
            case 6: leftLeg1 += rightLeg1; leftLeg2 += rightLeg2;
            case 5: steps[6] += leftLeg1; steps[7] += leftLeg2;
            case 4: body1 += rightArm1; body2 += rightArm2;
            case 3: leftArm1 += body1; leftArm2 += body2; steps[5] += body3;
            case 2: steps[3] += leftArm1; steps[4] += leftArm2;
            case 1: steps[2] += head; break;
        }


        for (String s : steps) {
            System.out.println(s);
        }
    }
}
