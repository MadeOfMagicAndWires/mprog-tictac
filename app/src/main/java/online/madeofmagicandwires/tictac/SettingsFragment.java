package online.madeofmagicandwires.tictac;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;


/**
 * Basic PreferenceFragment containing the preferred game board size
 * @see R.xml#tictac_prefs
 * @see PreferenceFragmentCompat
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    /**
     * On change listener for the 'boardSize' preference.
     */
    public static class OnBoardSizeChangeListener implements Preference.OnPreferenceChangeListener {


        private Context context;

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
            if(preference.getKey().equals("boardSize")) {
                Log.d("OnBoardSizeChangeListener", "changes made to boardSize");
                if((newValue instanceof String) && ((String) newValue).matches("^\\d*") && Integer.valueOf((String)newValue) >= Game.DEFAULT_BOARD_SIZE) {
                    Log.d("onBoardSizeChangeListener", "boardSize will now be " + ((String) newValue));
                    if(context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                    return true;
                } else {
                    Toast tst = Toast.makeText(
                            context,
                            context.getText(R.string.board_size_setting_not_valid_error_msg),
                            Toast.LENGTH_LONG);
                    tst.show();
                }
            }
            return false;
        }

    }


    /**
     * Inflates the preference values and adds a changelistener to the board size setting
     *
     * @param bundle Bundle containing the fragments arguments.
     * @param s the key used for {@link #setPreferencesFromResource}
     */
    @SuppressWarnings("ConstantConditions") // isAdded means context != null, don't flag this as a warning
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.tictac_prefs, s);
        if(isAdded()) {
            getPreferenceManager().findPreference("boardSize")
                    .setOnPreferenceChangeListener(new OnBoardSizeChangeListener(getContext()));

        }
    }


}
