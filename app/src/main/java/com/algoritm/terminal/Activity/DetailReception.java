package com.algoritm.terminal.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.algoritm.terminal.DataBase.SharedData;
import com.algoritm.terminal.Objects.CarData;
import com.algoritm.terminal.R;
import com.algoritm.terminal.Objects.Reception;
import com.algoritm.terminal.Adapters.RecyclerAdapterCarData;

public class DetailReception extends AppCompatActivity {
    private Reception reception;
    private TextView description;
    private TextView driver;
    private TextView autoNumber;
    private RecyclerView recyclerView;
    private RecyclerAdapterCarData adapter;

    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reception);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        reception = getIntent().getParcelableExtra("Reception");
        String id = getIntent().getStringExtra("Reception");
        reception = SharedData.getReception(id);

        description = findViewById(R.id.description);
        description.setText(reception.getDescription());

        driver = findViewById(R.id.driver);
        driver.setText(reception.getDriver());

        autoNumber = findViewById(R.id.autoNumber);
        autoNumber.setText(reception.getAutoNumber());

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapterCarData(this, R.layout.item_activity_detail, reception.getCarData());
        recyclerView.setAdapter(adapter);
        adapter.setActionListener(new RecyclerAdapterCarData.ActionListener() {
            @Override
            public void onClick(CarData carData) {
                Intent intent = new Intent(DetailReception.this, CarActivity.class);
                intent.putExtra("CarData", carData);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLists();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //updateLists();
            }
        }

    }

    private void updateLists() {
        if (adapter == null) {
            adapter = new RecyclerAdapterCarData(this, R.layout.item_activity_detail, reception.getCarData());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
