package online.madeofmagicandwires.tictac;

import android.content.Context;

import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.DialogPreference;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @see "https://github.com/h6ah4i/android-numberpickerprefcompat"
 */
@SuppressWarnings("unused,WeakerAccess")
public class BoardSizePreference extends DialogPreference {

    private static final @LayoutRes int DEFAULT_LAYOUT = R.layout.boardsizepreference;

    private int minValue;
    private int maxValue;
    private int defaultValue;
    private int currentValue;

    private boolean firstRun;


    /**
     * Most specific constructor
     * @param context the application context
     * @param attrs   attribute set containing the Views attributes
     * @param defStyleAttr the default View attributes
     * @param defStyleRes the resource id of default the View attributes
     *
     */
    public BoardSizePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);

        // Retrieve custom values
        TypedArray customAttrs = context.obtainStyledAttributes(
                attrs,
                R.styleable.BoardSizePreference,
                defStyleAttr,
                defStyleRes);
        minValue = customAttrs.getInt(R.styleable.BoardSizePreference_minValue, 0);
        maxValue = customAttrs.getInt(R.styleable.BoardSizePreference_maxValue, 10);

        Log.d("custom view", "minValue: " + minValue + " | " + "maxValue: " + maxValue);

        // if layout file is not provided, use DEFAULT_LAYOUT;
        Log.d("BoardSizePreference", "dialogLayout is " + getDialogLayoutResource());
        if(getDialogLayoutResource() == -1) {
            super.setDialogLayoutResource(DEFAULT_LAYOUT);
        }

        // free customAttributes
        customAttrs.recycle();

        if(super.getDialogLayoutResource() == 0) {
            super.setDialogLayoutResource(DEFAULT_LAYOUT);
        }

    }

    /**
     * Constructor forgoing the default style attribute
     * @param c application context
     * @param attrs attribute set containing the Views attributes
     * @param defStyleAttr the default View attributes
     */
    public BoardSizePreference(Context c, AttributeSet attrs,  int defStyleAttr) {
        this(c, attrs, defStyleAttr, 0);
    }


    /**
     * Constructor forgoing both the default style attributes and the default styleable attributes
     * @param c Context
     * @param attrs attribute set containing the Views attributes
     */
    public BoardSizePreference(Context c, AttributeSet attrs) {
        this(c, attrs, 0, 0);
    }




    /**
     * Called when the default value is retrieved
     * @param a typedarray containing this view's custom attributes
     * @param index the index of the default value attribute
     * @return int containing the board size's default value.
     */
    @NonNull
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        int persistentDefaultValue = a.getInt(index, 0);
        this.defaultValue = persistentDefaultValue;
        return persistentDefaultValue;

    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public  int getMinimumValue() {
        return this.minValue;
    }

    public int getMaximumValue() {
        return this.maxValue;
    }

    public int getValue() {
        return this.currentValue;
    }


    /**
     * Sets the current value of the preference
     * @param value the new value for this preference to assume.
     */
    public void setValue(int value) {
        final boolean changed = (currentValue != value);
        if(changed || firstRun) {
            currentValue = value;
            firstRun = false;
            persistInt(value);
            if(changed) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }

    /**
     * Sets the  default value of this preference
     * @param value the new value to be set;
     *              must be larger than minValue and smaller than maxValue.
     * @return whether this preference's defaultValue assumed the new value
     */
    public boolean setDefaultValue(int value) {
        if(defaultValue != value && minValue < value && value > maxValue) {
            this.defaultValue = value;
            return true;
        }
        return false;
    }

    /**
     * Sets the minimum possible value of this preference
     * @param value new minimum value; must be smaller than the maximum value.
     * @return whether this preference's minimum value assumed value.
     */
    public boolean setMinValue(int value) {
        if(value < maxValue) {
            this.minValue = value;
            return true;
        }
        return false;
    }

    /**
     * Sets the maximum possible value of this preference
     * @param value new minimum value; must be larger than the minimum value.
     * @return whether this preference's maximum value assumed value.
     */
    public boolean setMaxValue(int value) {
        if(minValue < value) {
            this.maxValue = value;
            return true;
        }
        return false;
    }

    /**
     *
     * @param defaultValue the default value of this preference
     */
    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        int initialValue = (defaultValue != null) ? (int) defaultValue : 0;
        this.defaultValue = initialValue;
        this.setValue(initialValue);

    }


    /**
     * Class to be used to write and read the state  of a BoardSizePreference instance.
     *
     */
    @SuppressWarnings("WeakerAccess")
    private static class BoardSizeSavedState extends View.BaseSavedState {

        private int value;

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source parcel to read from
         */

        public BoardSizeSavedState(Parcel source) {
            super(source);
            this.value = source.readInt();
        }

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        public BoardSizeSavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }

        public static final Parcelable.Creator<BoardSizeSavedState> CREATOR =
                new Parcelable.Creator<BoardSizeSavedState>() {
                    public BoardSizeSavedState createFromParcel(Parcel in) {
                        return new BoardSizeSavedState(in);
                    }

                    public BoardSizeSavedState[] newArray(int size) {
                        return new BoardSizeSavedState[size];
                    }
                };



    }

    /**
     * Saves the state to be read at a later date
     * returns a parceable containing the state of this instance
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState =  super.onSaveInstanceState();
        if(isPersistent()) {
            return superState;
        }
        final BoardSizeSavedState state = new BoardSizeSavedState(superState);
        state.value = currentValue;
        return state;
    }

    /**
     * Retrieves the state from which the value was saved.
     * @param state the saved state of this instance
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof BoardSizeSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        ((BoardSizeSavedState) state).getSuperState();
        setValue(((BoardSizeSavedState) state).value);
    }
}
