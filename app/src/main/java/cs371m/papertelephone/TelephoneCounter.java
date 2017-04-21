package cs371m.papertelephone;


import android.app.Application;

import java.util.ArrayList;

public class TelephoneCounter extends Application {
    public int counter = 1;
    public int secondsRemaining;
    public ArrayList<String> guesses = new ArrayList<>();
    public ArrayList<byte[]> pictures = new ArrayList<>();
}
