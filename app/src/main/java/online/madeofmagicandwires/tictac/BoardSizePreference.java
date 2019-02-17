package online.madeofmagicandwires.tictac;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class BoardSizePreference extends DialogPreference implements NumberPicker.OnValueChangeListener {


    public BoardSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Called upon a change of the current value.
     *
     * @param picker The NumberPicker associated with this listener.
     * @param oldVal The previous value.
     * @param newVal The new value.
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}
