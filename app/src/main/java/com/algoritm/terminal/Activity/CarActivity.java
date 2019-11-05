package com.algoritm.terminal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.algoritm.terminal.DataBase.SharedData;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.R;
import com.algoritm.terminal.Objects.Sector;

import java.util.ArrayList;
import java.util.Calendar;

public class CarActivity extends AppCompatActivity {
    private TextView car;
    private TextView barCode;

    private EditText editDate;
    private Spinner editSector;
    private EditText editRow;
    private ImageView imageOk;
    private ImageView imageCancel;

    private CarData carData;
    private Calendar dateAndTime = Calendar.getInstance();

    private ArrayList<Sector> mSectors = SharedData.SECTORS;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        car = findViewById(R.id.car);
        barCode = findViewById(R.id.barCode);
        editDate = findViewById(R.id.editDate);
        editSector = findViewById(R.id.editSector);
        editRow = findViewById(R.id.editRow);
        imageOk = findViewById(R.id.imageOk);
        imageCancel = findViewById(R.id.imageCancel);

        imageOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sector mSector = mSectors.get(editSector.getSelectedItemPosition());

                carData.setProductionDate(dateAndTime.getTime());
                carData.setSectorID(mSector.getID());
                carData.setSector(mSector.getName());
                carData.setRow(editRow.getText().toString());
                SharedData.updateReception(carData);

                Toast.makeText(v.getContext(), "Запись данных...", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        carData = intent.getParcelableExtra("CarData");
        car.setText(carData.getCar());
        barCode.setText(carData.getBarCode());

        editDate.setText(carData.getProductionDateString());

        ArrayAdapter<Sector> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSectors);
        editSector.setAdapter(adapter);
//        editSector.setText(carData.getSector());
        editSector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //editRow.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editRow.setText(carData.getRow());

        dateAndTime.setTime(carData.getProductionDate());

        editDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editDate.getRight() - editDate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        new DatePickerDialog(CarActivity.this, d,
                                dateAndTime.get(Calendar.YEAR),
                                dateAndTime.get(Calendar.MONTH),
                                dateAndTime.get(Calendar.DAY_OF_MONTH))
                                .show();

                        return true;
                    }
                }
                return false;
            }
        });

        editDate.addTextChangedListener(getTextWatcher());
        editDate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                editSector.performClick();
//                return true;
                return false;
            }
        });
    }

    private TextWatcher getTextWatcher() {
        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s.%s.%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editDate.setText(current);
                    editDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        return tw;
    }


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();

        }
    };

    private void setInitialDateTime() {
        carData.setProductionDate(dateAndTime.getTime());
        editDate.setText(carData.getProductionDateString());
//        editSector.performClick();
    }
}
