package cognitiva.dyslexreader;


import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * Classe que gera os fragmentos de UI baseado nos dados do xml de preferencias
 */
public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_dyslex);

        findPreference(getString(R.string.themeCustomBackgroundKey)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                showDialog2();
                return true;
            }
        });

    }



    private void showDialog2() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.colorsForCustomTheme)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(2)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_2");
    }

}
