package com.example.finanalysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AnalyticActivity extends AppCompatActivity {

    // База данных
    private SQLiteOpenHelper finDatabaseHelper;
    private SQLiteDatabase db;
    private  Cursor cursor;
    private static final String TABLE_CHECK = "CHECKTABLE";

    ArrayList<Check> checks_array =  new ArrayList<>(); // Массив чеков

    TextView textView; // Текст для вывода информации
    ListView listView; // Лист для вывода доходов и расходов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        // Инициализируем компоненты
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);

        showList(); // Показать лист записей
    }

    //Выгрузка из базы данных и показ записей
    void showList() {
        int n = 0;
        Toast toast;

        try {
            finDatabaseHelper = new FinDatabaseHelper(this);
            db = finDatabaseHelper.getWritableDatabase();

            // Чтение базы данных
            checks_array = new ArrayList<>();

            cursor = db.query(TABLE_CHECK, null, null,
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    checks_array.add(new Check(cursor.getString(1), cursor.getFloat(5), cursor.getString(4)));
                } while (cursor.moveToNext());
            }

            try {
                db.close();
                cursor.close();
            } catch (Exception e) {}

        // Сортировка по дате /////////////////////////////////////////////////////////////////////
        int maxData = 0;
        int max_kData = 0;
        int sortData = 0;
        long l;
        Date d;

        Check exchange_check = new Check(); // Чек для алгоритма обмена

        for (int j = 0; j < checks_array.size(); j++) {
            maxData = Integer.parseInt(checks_array.get(j).getStringDate());
            max_kData = j;

            for (int k = j; k < checks_array.size(); k++) {
                sortData = Integer.parseInt(checks_array.get(k).getStringDate());

                if (sortData > maxData) {
                    maxData = sortData;
                    max_kData = k;
                }
            }

            exchange_check.setCheck(checks_array.get(j));
            checks_array.get(j).setCheck(checks_array.get(max_kData));
            checks_array.get(max_kData).setCheck(exchange_check);
        }

        // Подсчитываем баланс ////////////////////////////////////////////////////////////////////
        double balance = 0;
        for (int j = 0; j < checks_array.size(); j++) {
            balance = balance + checks_array.get(j).getExpense_revenue();
        }

        textView.setText("Список");

        // Выводим список доходов и расходов
        // используем адаптер данных
        // сначала чистим список
        listView.setAdapter(null);

        // Обьявляем массив значений-ключей и заполняем его
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map;

        for (int j = 0; j < checks_array.size(); j++) {
            map = new HashMap<>();
            map.put("text1", checks_array.get(j).getStringExpense_revenue());
            map.put("text2", checks_array.get(j).getDate());
            map.put("text3", String.format("%.18s" ,checks_array.get(j).getName()));
            list.add(map);
        }

        SimpleAdapter m = new SimpleAdapter(getApplicationContext(), list,
                R.layout.list_item,
                new String[]{"text1", "text2", "text3"},
                new int[]{R.id.text1, R.id.text2, R.id.text3});
        listView.setAdapter(m);

    }catch (SQLiteException e){
        toast =Toast.makeText(this,"База данных недоступна", Toast.LENGTH_SHORT);
        toast.show();
        }
    }

    // Нажатие на кнопку анализа
    public void onStatButtonClick(View view)
    {
        float[] check_ex_rev = new float[checks_array.size()];
        String[] check_date = new String[checks_array.size()];
        String[] check_about = new String[checks_array.size()];

        for (int j=0; j < checks_array.size(); j++) {
            check_ex_rev[j] = checks_array.get(j).getExpense_revenue();
            check_date[j] = checks_array.get(j).getDate();
            check_about[j] = checks_array.get(j).getName();
        }

        Intent i1 = new Intent(this,StatisticActivity.class);
        i1.putExtra("check_ex_rev", check_ex_rev);
        i1.putExtra("check_date", check_date);
        i1.putExtra("check_about", check_about);
        startActivity(i1);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try {
            db.close();
            cursor.close();
        } catch (Exception e) {}
    }
}