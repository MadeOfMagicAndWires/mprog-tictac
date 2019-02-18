package online.madeofmagicandwires.tictac;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Class to draw BoardSizePreference Dialog views
 *
 * @see "https://github.com/h6ah4i/android-numberpickerprefcompat"
 */
public class BoardSizePreferenceFragment extends PreferenceDialogFragmentCompat {

    private static final String SAVED_STATE_KEY = "savedState";

    private NumberPicker picker;

    public static BoardSizePreferenceFragment newInstance(String preferenceKey) {
        BoardSizePreferenceFragment fragment = new BoardSizePreferenceFragment();
        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, preferenceKey); // needed to get the correct preference key.

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(SAVED_STATE_KEY)) {
            getPreference().setValue(savedInstanceState.getInt(SAVED_STATE_KEY));
        }
    }




    /**
     * Used to bind data to the dialog view
     * @param root view representing the root of the dialog content
     */
    @Override
    protected void onBindDialogView(View root) throws IllegalArgumentException {
        super.onBindDialogView(root);

        picker = root.findViewById(R.id.boardSizePicker);
        if(picker != null) {
            picker.setMinValue(getPreference().getMinimumValue());
            picker.setMaxValue(getPreference().getMaximumValue());
            picker.setValue(getPreference().getValue());
            Log.d("picker Value", Integer.toString(picker.getValue()));
        } else {
            throw new IllegalArgumentException(getString(R.string.dialogfragment_missing_numberpicker_exception_msg));
        }
    }

    /**
     * Called when the dialog view is closed
     * @param positiveResult user input on whether to discard or save the value;
     *                       true for save; false for discard.
     */
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {
            if(picker != null) {
                picker.clearFocus();

                final int newValue = picker.getValue();
                if(getPreference().callChangeListener(newValue)) {
                    getPreference().setValue(newValue);
                    Log.d("new board size preference", Integer.toString(getPreference().getValue()));
                }
            }

        }
    }

    /**
     * Gets the BoardSizePreference instance
     * @return the BoardSizePreference object associated with this fragment
     */
    @NonNull
    public BoardSizePreference getPreference() {
        return (BoardSizePreference) super.getPreference();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_STATE_KEY, getPreference().getValue());
    }
}
