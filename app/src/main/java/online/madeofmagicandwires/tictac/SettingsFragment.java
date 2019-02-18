package online.madeofmagicandwires.tictac;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import java.util.Objects;

/**
 * Basic PreferenceFragment containing the preferred game board size
 * @see R.xml#tictac_prefs
 * @see PreferenceFragmentCompat
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @SuppressWarnings("WeakerAccess")
    public static final String DIALOG_FRAGMENT_TAG = "BoardSizePreferenceFragment";

    /**
     * On change listener for the 'boardSize' preference.
     */
    @SuppressWarnings("WeakerAccess")
    public static class OnBoardSizeChangeListener implements Preference.OnPreferenceChangeListener {


        private final Context context;

        @SuppressWarnings("WeakerAccess")
        public OnBoardSizeChangeListener(@NonNull Context context) {
            this.context = context;
        }

        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param newValue   The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(context.getString(R.string.prefs_board_size_key))) {
                if (newValue instanceof Integer) {
                    Log.d("OnBoardSizeChange", preference.getKey() + " changed");
                    if(context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Inflates the preference values and adds a eventlistener to the board size setting
     *
     * @param bundle Bundle containing the fragments arguments.
     * @param s the key used for {@link #setPreferencesFromResource}
     */
    @SuppressWarnings("ConstantConditions") // isAdded means context != null, don't flag this as a warning
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.tictac_prefs, s);
        if(isAdded()) {
            getPreferenceManager().findPreference(getActivity().getString(R.string.prefs_board_size_key))
                    .setOnPreferenceChangeListener(new OnBoardSizeChangeListener(getContext()));

        }
    }



    /**
     * Called when a dialog preference requests a dialog view.
     * @see "https://github.com/h6ah4i/android-numberpickerprefcompat"
     * @param preference the preference object to use when creating a dialog
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if(Objects.requireNonNull(getFragmentManager()).findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        final PreferenceDialogFragmentCompat fragment;
        if(preference instanceof BoardSizePreference) {
            fragment = BoardSizePreferenceFragment.newInstance(preference.getKey());
        } else {
            fragment = null;
        }

        if(fragment != null) {
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else  {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
