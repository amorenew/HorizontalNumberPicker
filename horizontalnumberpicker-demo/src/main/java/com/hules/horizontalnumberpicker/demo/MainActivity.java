package com.hules.horizontalnumberpicker.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;
import com.hules.horizontalnumberpicker.demo.commons.DebugLog;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.graphics.Color.parseColor;

public class MainActivity extends AppCompatActivity implements HorizontalNumberPickerListener {
    @Bind(R.id.horizontal_number_picker1)
    HorizontalNumberPicker horizontalNumberPicker1;
    @Bind(R.id.horizontal_number_picker2)
    HorizontalNumberPicker horizontalNumberPicker2;
    @Bind(R.id.horizontal_number_picker3)
    HorizontalNumberPicker horizontalNumberPicker3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        horizontalNumberPicker1.setMaxValue(5);

        horizontalNumberPicker2.setMinuText("<");
        horizontalNumberPicker2.setPlusText(">");
        horizontalNumberPicker2.setShowLeadingZeros(true);
        horizontalNumberPicker2.setValue(23);
        horizontalNumberPicker3.setValueDescription("Kid");
        horizontalNumberPicker3.setBackgroundColor(parseColor("#1565C0"));
        horizontalNumberPicker3.setNumberTextColor(Color.WHITE);
        horizontalNumberPicker3.setNumberTextBold(true);
        horizontalNumberPicker3.setNumberTextSize(18);
        horizontalNumberPicker3.setButtonTextColor(Color.WHITE);
        horizontalNumberPicker3.setButtonTextSize(20);
        horizontalNumberPicker3.setButtonBold(true);
        horizontalNumberPicker1.setListener(this);
        horizontalNumberPicker2.setListener(this);
        horizontalNumberPicker3.setListener(this);
    }

    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker,
                                                int value) {
        switch (horizontalNumberPicker.getId()) {
            case R.id.horizontal_number_picker1:
                DebugLog.d("horizontal_number_picker1 current value:" + value);
                break;

            case R.id.horizontal_number_picker2:
                DebugLog.d("horizontal_number_picker2 current value: " + value);
                break;

            case R.id.horizontal_number_picker3:
                DebugLog.d("horizontal_number_picker3 current value: " + value);
                break;
        }
    }
}
