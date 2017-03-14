package cs371m.papertelephone;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int SETTINGS_REQUEST = 1;

    private static int rounds;
    private static int drawCountdown;
    private static int guessCountdown;
    private static boolean colorOn;

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
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        colorOn = sharedPref.getBoolean("colorOn", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        if (requestCode == SETTINGS_REQUEST) {
            colorOn = sharedPref.getBoolean("colorOn", true);
            Log.d(TAG, "colorOn: " + colorOn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startHowTo(View view) {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }
}
