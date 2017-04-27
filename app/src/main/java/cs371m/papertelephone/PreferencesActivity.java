package cs371m.papertelephone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class PreferencesActivity extends AppCompatActivity {
    static final String TAG = "PreferencesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        static SharedPreferences sharedPrefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sharedPrefs = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE);

            // set up shared preferences file
            getPreferenceManager().setSharedPreferencesName("MainActivity");
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            // handle difficulty listener and summary
            difficultyListener();
            difficultySummary();

            // handle rounds listener and summary
            roundsListener();
            updateRoundsSummary();

            // handle timer listeners and summaries
            drawTimeListener();
            updateDrawTimeSummary();
            guessTimeListener();
            updateGuessTimeSummary();

            // handle color listener and summary
            colorTimeListener();
            updateColorSummary();
        }

        private void difficultyListener() {
            final Preference diffPref = findPreference("difficulty");
            diffPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String diffSummary = "Difficulty is currently set to " + newValue;
                            diffPref.setSummary(diffSummary);
                            return true;
                        }
                    }
            );
        }

        private void difficultySummary() {
            String diffSummary = "Difficulty is currently set to "
                    + sharedPrefs.getString("difficulty", "Easy");
            Preference diffPref = findPreference("difficulty");
            diffPref.setSummary(diffSummary);
        }

        private void roundsListener() {
            final Preference roundsPref = findPreference("rounds");
            roundsPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String roundsSummary = "Currently set to " + newValue;
                            roundsPref.setSummary(roundsSummary);

                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString("rounds", newValue.toString());
                            editor.apply();
                            return true;
                        }
                    }
            );
        }

        private void updateRoundsSummary() {
            String roundsSummary = "Currently set to " + sharedPrefs.getString("rounds", "3");
            Preference roundsPref = findPreference("rounds");
            roundsPref.setSummary(roundsSummary);
        }

        private void drawTimeListener() {
            final Preference drawTimePref = findPreference("drawCountdown");
            drawTimePref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String drawTimeSummary = "Time to Draw | Currently " + newValue + " seconds";
                            drawTimePref.setSummary(drawTimeSummary);

                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString("drawCountdown", newValue.toString());
                            editor.apply();
                            return true;
                        }
                    }
            );
        }

        private void updateDrawTimeSummary() {
            String drawTimeSummary = "Time to Draw | Currently " + sharedPrefs.getString("drawCountdown", "60") + " seconds";
            Preference drawTimePref = findPreference("drawCountdown");
            drawTimePref.setSummary(drawTimeSummary);
        }

        private void guessTimeListener() {
            final Preference guessTimePref = findPreference("guessCountdown");
            guessTimePref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String guessTimeSummary = "Time to Guess | Currently " + newValue + " seconds";
                            guessTimePref.setSummary(guessTimeSummary);

                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString("guessCountdown", newValue.toString());
                            editor.apply();
                            return true;
                        }
                    }
            );
        }

        private void updateGuessTimeSummary() {
            String guessTimeSummary = "Time to Guess | Currently " + sharedPrefs.getString("guessCountdown", "15") + " seconds";
            Preference guessTimePref = findPreference("guessCountdown");
            guessTimePref.setSummary(guessTimeSummary);
        }

        private void colorTimeListener() {
            final Preference colorPref = findPreference("colorOn");
            colorPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.d(TAG, "New value is " + newValue.toString());
                            String toggle = ((boolean) newValue) ? "on" : "off";
                            String colorSummary = "Color is now " + toggle;
                            colorPref.setSummary(colorSummary);
                            return true;
                        }
                    }
            );
        }

        private void updateColorSummary() {
            String toggle = (sharedPrefs.getBoolean("colorOn", true)) ? "on" : "off";
            String colorSummary = "Color is now " + toggle;
            Preference colorPref = findPreference("colorOn");
            colorPref.setSummary(colorSummary);
        }
    }

}

