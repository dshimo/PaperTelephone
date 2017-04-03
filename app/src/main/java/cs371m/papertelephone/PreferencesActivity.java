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
    }

}

