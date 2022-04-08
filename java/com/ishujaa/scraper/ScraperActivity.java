package com.ishujaa.scraper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScraperActivity extends AppCompatActivity {

    ListView targetListView;
    SQLiteOpenHelper openHelper;
    TextView serviceStatus;
    TextView lastUpdateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scraper);

        lastUpdateView = findViewById(R.id.last_update_view);
        targetListView = findViewById(R.id.target_list_view);
        openHelper = new DBHelper(this);

        targetListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(view.getContext(), AddNewTarget.class);
            intent.putExtra("targetId", (int) l);
            intent.putExtra("update", true);
            startActivity(intent);

        });

        Button addNewTargetButton = findViewById(R.id.btn_add_new_target);
        addNewTargetButton.setOnClickListener(view -> startActivity(new Intent(view.getContext(),
                AddNewTarget.class)));

        Button buttonStartService = findViewById(R.id.btn_start_service);
        buttonStartService.setOnClickListener(view -> {
            startService(new Intent(view.getContext(), ScraperService.class));
            setServiceLabel();
        });


    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ScraperService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setServiceLabel(){
        serviceStatus = findViewById(R.id.service_status);

        if(isMyServiceRunning()){
            serviceStatus.setText("Service is Running Good.");
            serviceStatus.setTextColor(Color.GREEN);
        }else{
            serviceStatus.setText("Service not Running.");
            serviceStatus.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            SQLiteDatabase database = openHelper.getReadableDatabase();
            Cursor cursor = database.query("target_table",
                    new String[]{"_id, name"},
                    null, null, null, null, null);
            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
            targetListView.setAdapter(cursorAdapter);

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        setServiceLabel();

        String lastUpdate = "Last Updated: ";
        try {
            lastUpdate += new SPHelper(this).getLastUpdateTime();;
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        lastUpdateView.setText(lastUpdate);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}