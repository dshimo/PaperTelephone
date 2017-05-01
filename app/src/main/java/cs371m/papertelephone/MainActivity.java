package cs371m.papertelephone;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int SETTINGS_REQUEST = 1;

    public static String difficulty;
    public static int rounds;
    public static int drawCountdown;
    public static int guessCountdown;
    public static boolean colorOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("colorOn", colorOn);
        editor.putString("drawCountdown", String.valueOf(drawCountdown));
        editor.putInt("rounds", rounds);
        editor.putString("guessCountdown", String.valueOf(guessCountdown));
        editor.putString("difficulty", String.valueOf(difficulty));

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        difficulty = sharedPref.getString("difficulty", "Easy Words");
        colorOn = sharedPref.getBoolean("colorOn", true);
        drawCountdown = Integer.parseInt(sharedPref.getString("drawCountdown", "60"));
        rounds = sharedPref.getInt("rounds", 1);
//        rounds = Integer.parseInt((sharedPref.getString("rounds", "1")));
        guessCountdown = Integer.parseInt(sharedPref.getString("guessCountdown", "30"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        if (requestCode == SETTINGS_REQUEST) {
            colorOn = sharedPref.getBoolean("colorOn", true);
            Log.d(TAG, "colorOn: " + colorOn);
            drawCountdown = Integer.parseInt(sharedPref.getString("drawCountdown", "60"));
            rounds = sharedPref.getInt("rounds", 1);
            guessCountdown = Integer.parseInt(sharedPref.getString("guessCountdown", "15"));
            difficulty = sharedPref.getString("difficulty", "Easy Words");
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.action_bar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        FragmentManager fm = getFragmentManager();
//        switch (item.getItemId()) {
//            case R.id.settings:
//                Intent intent = new Intent(this, PreferencesActivity.class);
//                startActivityForResult(intent, SETTINGS_REQUEST);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void startSettings(View view) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, SETTINGS_REQUEST);
    }

    public void startHowTo(View view) {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }

    public void startNewGame(View view) {
        final Intent intent = new Intent(this, DrawingActivity.class);
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        if (sharedPref.getString("difficulty", "Easy").equals("Choose your own!")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Write your prompt");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.requestFocus();
            //the snippet doesn't automatically bring up keyboard unfortunately
/*            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);*/
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DrawingActivity.word = input.getText().toString();
                    if (!DrawingActivity.word.equals("")) {
                        ((TelephoneCounter) getApplicationContext()).counter = 1;
                        ((TelephoneCounter) getApplicationContext()).guesses.clear();
                        ((TelephoneCounter) getApplicationContext()).pictures.clear();
                        startActivity(intent);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "You didn't write anything!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            ((TelephoneCounter) getApplicationContext()).counter = 1;
            ((TelephoneCounter) getApplicationContext()).guesses.clear();
            ((TelephoneCounter) getApplicationContext()).pictures.clear();
            startActivity(intent);
        }
    }
}
