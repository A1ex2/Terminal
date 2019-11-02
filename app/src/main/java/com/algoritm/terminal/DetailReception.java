package com.algoritm.terminal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class DetailReception extends AppCompatActivity {
    private Reception reception;
    private TextView description;
    private TextView driver;
    private TextView autoNumber;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reception);

        reception = getIntent().getParcelableExtra("Reception");

        description = findViewById(R.id.description);
        description.setText(reception.getDescription());

        driver = findViewById(R.id.driver);
        driver.setText(reception.getDriver());

        autoNumber = findViewById(R.id.autoNumber);
        autoNumber.setText(reception.getAutoNumber());

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerAdapterCarData adapter = new RecyclerAdapterCarData(this, R.layout.item_activity_detail, reception.getCarData());
        recyclerView.setAdapter(adapter);
        //adapter.setActionListener(mClick);
    }

    private RecyclerAdapterCarData.ActionListener mClick = new RecyclerAdapterCarData.ActionListener() {
        @Override
        public void onClick(CarData carData) {
            mActionListener.edit(carData);
        }
    };

    public interface ActionListener {
        public void edit(CarData carData);
    }

    private ActionListener mActionListener;

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
