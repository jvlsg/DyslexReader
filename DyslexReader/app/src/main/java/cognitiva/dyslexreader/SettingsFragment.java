package cognitiva.dyslexreader;


import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;
import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * Classe que gera os fragmentos de UI baseado nos dados do xml de preferencias
 */
public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_dyslex);

    }

    @Override public void onDisplayPreferenceDialog(Preference preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
