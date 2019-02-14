package online.madeofmagicandwires.tictac;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Basic PreferenceFragment containing the preferred game board size
 * @see R.xml.tictac_prefs
 * @see PreferenceFragmentCompat
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * Inflates the preference values
     * @param bundle Bundle containing the fragments arguments.
     * @param s the key used for {@link #setPreferencesFromResource}
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.tictac_prefs, s);

    }
}
