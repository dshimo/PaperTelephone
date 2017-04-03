package cs371m.papertelephone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class PreferencesActivity extends AppCompatActivity {

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

            // handle rounds summary and listener
            roundsListener();
            updateRoundsSummary();

            // handle timer summaries and listeners
            drawTimeListener();
            updateDrawTimeSummary();
            guessTimeListener();
            updateGuessTimeSummary();
        }

        private void roundsListener() {
            final Preference roundsPref = findPreference("rounds");
            roundsPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            String roundsSummary = "Number of Rounds: " + newValue;
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
            String roundsSummary = "Number of Rounds: " + sharedPrefs.getString("rounds", "3");
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
    }

}

