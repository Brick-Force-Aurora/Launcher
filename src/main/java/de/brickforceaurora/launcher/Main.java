package de.brickforceaurora.launcher;

public class Main {

    public static void main(String[] args) {
        try {
            new LauncherApp();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}


// TODO: SWITCH TO INNO SETUP INSTEAD OF JPACKAGE