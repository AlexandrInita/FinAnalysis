package com.example.finanalysis;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // Нажатие на кнопку финансы
    public void onAnalButtonClick(View view)
    {
        Intent i1 = new Intent(this,AnalyticActivity.class);
        startActivity(i1);
    }
}