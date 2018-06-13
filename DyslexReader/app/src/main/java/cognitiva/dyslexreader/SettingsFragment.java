package cognitiva.dyslexreader;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Classe que gera os fragmentos de UI baseado nos dados do xml de preferencias
 */
public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_dyslex);
    }
}
