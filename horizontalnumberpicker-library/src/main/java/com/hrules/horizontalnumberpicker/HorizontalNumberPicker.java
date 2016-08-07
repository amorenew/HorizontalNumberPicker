package com.hrules.horizontalnumberpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HorizontalNumberPicker extends LinearLayout {
    private final static int MIN_UPDATE_INTERVAL = 50;

    private int value;
    private int maxValue;
    private int minValue;
    private int stepSize;
    private boolean showLeadingZeros;

    private Button buttonMinus;
    private Button buttonPlus;
    private TextView textValue, textValueDescription;
    private CardView mainView;
    private LinearLayout mainView2;
    private boolean autoIncrement;
    private boolean autoDecrement;

    private int updateInterval;

    private Handler updateIntervalHandler;

    private HorizontalNumberPickerListener listener;
    private boolean isTitleUp;

    public HorizontalNumberPicker(Context context) {
        this(context, null);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //if (isInEditMode()) {
        //    return;
        //}
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.NumberPicker);
        isTitleUp = typedArray.getBoolean(R.styleable.NumberPicker_titleUp, false);

        if (isTitleUp) {
            layoutInflater.inflate(R.layout.horizontal_number_picker_title_up, this);
            mainView2 = (LinearLayout) findViewById(R.id.mainView);
        } else {
            layoutInflater.inflate(R.layout.horizontal_number_picker, this);
            mainView = (CardView) findViewById(R.id.mainView);
        }
        Resources res = getResources();

        String buttonPlusText = typedArray.getString(R.styleable.NumberPicker_plusButtonText);
        initButtonPlus(
                buttonPlusText != null ? buttonPlusText : res.getString(R.string.defaultButtonPlus));

        String buttonMinusText =
                typedArray.getString(R.styleable.NumberPicker_minusButtonText);
        initButtonMinus(
                buttonMinusText != null ? buttonMinusText : res.getString(R.string.defaultButtonMinus));

        minValue = typedArray.getInt(R.styleable.NumberPicker_minValue,
                res.getInteger(R.integer.default_minValue));
        maxValue = typedArray.getInt(R.styleable.NumberPicker_maxValue,
                res.getInteger(R.integer.default_maxValue));

        updateInterval = typedArray.getInt(R.styleable.NumberPicker_repeatDelay,
                res.getInteger(R.integer.default_updateInterval));
        stepSize = typedArray.getInt(R.styleable.NumberPicker_stepSize,
                res.getInteger(R.integer.default_stepSize));
        showLeadingZeros = typedArray.getBoolean(R.styleable.NumberPicker_showLeadingZeros,
                res.getBoolean(R.bool.default_showLeadingZeros));

        initTextValue();
        String valueDescriptionText = typedArray.getString(R.styleable.NumberPicker_valueDescription);
        textValueDescription.setText(valueDescriptionText);
        value = typedArray.getInt(R.styleable.NumberPicker_value,
                res.getInteger(R.integer.default_value));
        int pickerBackgroundColor = typedArray.getColor(R.styleable.NumberPicker_pickerBackgroundColor, Color.WHITE);
        setBackgroundColor(pickerBackgroundColor);
        int numberTextColor = typedArray.getColor(R.styleable.NumberPicker_numberTextColor, Color.BLACK);
        int descriptionTextColor = typedArray.getColor(R.styleable.NumberPicker_descriptionTextColor, Color.BLACK);
        setNumberTextColor(numberTextColor);
        setDescriptionTextColor(descriptionTextColor);
        int buttonTextColor = typedArray.getColor(R.styleable.NumberPicker_buttonTextColor, Color.BLACK);
        setButtonTextColor(buttonTextColor);
        int numberBackgroundColor = typedArray.getColor(R.styleable.NumberPicker_numberBackgroundColor, -1);
        int descriptionBackgroundColor = typedArray.getColor(R.styleable.NumberPicker_descriptionBackgroundColor, -1);
        setNumberBackgroundColor(numberBackgroundColor);
        setDescriptionBackgroundColor(descriptionBackgroundColor);
        int buttonBackgroundColor = typedArray.getColor(R.styleable.NumberPicker_buttonBackgroundColor, -1);
        setButtonBackgroundColor(buttonBackgroundColor);
        boolean numberTextBold = typedArray.getBoolean(R.styleable.NumberPicker_numberTextBold, true);
        boolean descriptionTextBold = typedArray.getBoolean(R.styleable.NumberPicker_descriptionTextBold, true);
        setNumberTextBold(numberTextBold);
        setDescriptionTextBold(descriptionTextBold);
        boolean buttonTextBold = typedArray.getBoolean(R.styleable.NumberPicker_buttonTextBold, true);
        setButtonBold(buttonTextBold);
        int numberTextSize = typedArray.getInteger(R.styleable.NumberPicker_numberTextSize, 14);
        int descriptionTextSize = typedArray.getInteger(R.styleable.NumberPicker_descriptionTextSize, 14);
        setNumberTextSize(numberTextSize);
        setDescriptionTextSize(descriptionTextSize);
        int buttonTextSize = typedArray.getInteger(R.styleable.NumberPicker_buttonTextSize, 14);
        setButtonTextSize(buttonTextSize);
        this.setValue();
        typedArray.recycle();
        autoIncrement = false;
        autoDecrement = false;

        updateIntervalHandler = new Handler();
    }

    private void setNumberBackgroundColor(int numberBackgroundColor) {
        if (numberBackgroundColor > 0)
            textValue.setBackgroundColor(numberBackgroundColor);
    }

    private void setDescriptionBackgroundColor(int descriptionBackgroundColor) {
        if (descriptionBackgroundColor > 0)
            textValueDescription.setBackgroundColor(descriptionBackgroundColor);
    }

    @Override
    public void setBackgroundColor(int color) {
        if (isTitleUp) {
            mainView2.setBackgroundColor(color);
        } else {
            mainView.setCardBackgroundColor(color);
        }
    }

    private void initTextValue() {
        textValue = (TextView) findViewById(R.id.text_value);
        textValueDescription = (TextView) findViewById(R.id.text_description);
    }

    public void setButtonTextColor(int color) {
        buttonMinus.setTextColor(color);
        buttonPlus.setTextColor(color);
    }

    public void setButtonBackgroundColor(int color) {
/*        if (color == 0 && isTitleUp) {
        } else*/
        if (color > 0) {
            buttonMinus.setBackgroundColor(color);
            buttonPlus.setBackgroundColor(color);
        }
    }

    public void setNumberTextSize(int size) {
        textValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setDescriptionTextSize(int size) {
        textValueDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size - 2);
    }

    public void setButtonTextSize(int size) {
        buttonMinus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        buttonPlus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setButtonBold(boolean makeBold) {
        if (makeBold) {
            buttonMinus.setTypeface(null, Typeface.BOLD);
            buttonPlus.setTypeface(null, Typeface.BOLD);
        } else {
            buttonMinus.setTypeface(null, Typeface.NORMAL);
            buttonPlus.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void initButtonPlus(String text) {
        buttonPlus = (Button) findViewById(R.id.button_plus);
        buttonPlus.setText(text);

        buttonPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                increment();
            }
        });

        buttonPlus.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                autoIncrement = true;
                updateIntervalHandler.post(new repeat());
                return false;
            }
        });

        buttonPlus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                    autoIncrement = false;
                }
                return false;
            }
        });
    }

    private void initButtonMinus(String text) {
        buttonMinus = (Button) findViewById(R.id.button_minus);
        buttonMinus.setText(text);

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decrement();
            }
        });

        buttonMinus.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                autoDecrement = true;
                updateIntervalHandler.post(new repeat());
                return false;
            }
        });

        buttonMinus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
                    autoDecrement = false;
                }
                return false;
            }
        });
    }

    public Button getButtonMinusView() {
        return buttonMinus;
    }

    public void setButtonMinusText(String text) {
        buttonMinus.setText(text);
    }

    public void setButtonPlusText(String text) {
        buttonPlus.setText(text);
    }

    public Button getButtonPlusView() {
        return buttonPlus;
    }

    public TextView getTextValueView() {
        return textValue;
    }

    public void setNumberTextColor(int color) {
        textValue.setTextColor(color);
    }

    public void setDescriptionTextColor(int color) {
        textValueDescription.setTextColor(color);
    }

    public void setNumberTextBold(boolean makeBold) {
        if (makeBold) {
            textValue.setTypeface(null, Typeface.BOLD);
        } else {
            textValue.setTypeface(null, Typeface.NORMAL);
        }
    }

    public void setDescriptionTextBold(boolean makeBold) {
        if (makeBold) {
            textValueDescription.setTypeface(null, Typeface.BOLD);
        } else {
            textValueDescription.setTypeface(null, Typeface.NORMAL);
        }
    }

    public void increment() {
        if (value < maxValue) {
            this.setValue(value + stepSize);
        }
    }

    public void decrement() {
        if (value > minValue) {
            this.setValue(value - stepSize);
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value > maxValue) {
            value = maxValue;
        }
        if (value < minValue) {
            value = minValue;
        }

        this.value = value;
        this.setValue();
    }

    private void setValue() {
        String formatter = "%0" + String.valueOf(maxValue).length() + "d";
        textValue.setText(showLeadingZeros ? String.format(formatter, value) : String.valueOf(value));
        if (listener != null) {
            listener.onHorizontalNumberPickerChanged(this, value);
        }
    }

    public void setValueDescription(String text) {
        textValueDescription.setText(text);
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (maxValue < value) {
            value = maxValue;
            this.setValue();
        }
    }

    public void reset() {
        value = minValue;
        this.setValue();
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        if (minValue > value) {
            value = minValue;
            this.setValue();
        }
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public void setShowLeadingZeros(boolean showLeadingZeros) {
        this.showLeadingZeros = showLeadingZeros;

        String formatter = "%0" + String.valueOf(maxValue).length() + "d";
        textValue.setText(showLeadingZeros ? String.format(formatter, value) : String.valueOf(value));
    }

    public long getOnLongPressUpdateInterval() {
        return updateInterval;
    }

    public void setOnLongPressUpdateInterval(int intervalMillis) {
        if (intervalMillis < MIN_UPDATE_INTERVAL) {
            intervalMillis = MIN_UPDATE_INTERVAL;
        }
        this.updateInterval = intervalMillis;
    }

    public void setListener(HorizontalNumberPickerListener listener) {
        this.listener = listener;
    }


    private class repeat implements Runnable {
        public void run() {
            if (autoIncrement) {
                increment();
                updateIntervalHandler.postDelayed(new repeat(), updateInterval);
            } else if (autoDecrement) {
                decrement();
                updateIntervalHandler.postDelayed(new repeat(), updateInterval);
            }
        }
    }
}
