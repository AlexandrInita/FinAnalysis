package com.example.finanalysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AnalyticActivity extends AppCompatActivity {

    // Массивы для считывания строк
    String[] Inbox_name=new String[1000],
            Inbox_number=new String[1000],
            Inbox_date=new String[1000],
            Inbox_type=new String[1000],
            Inbox_msg=new String[1000];

    int pos=0;

    double[] balance_arr = new double[1000]; // Массив доходов и расходов
    String[] balance_arr_str = new String[1000]; // Массив строчных доходов и расходов
    String[] balance_data = new String[1000]; // Массив дат доходов и расходов
    int balance_i = 0; // Длина массива баланса

    TextView textView; // Текст для вывода информации
    ListView listView; // Лист для вывода доходов и расходов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        // Инициализируем компоненты
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

            // Если дано разрешение, то показать смс
            Inbox_Read();

        } else {
            // Иначе установить разрешение
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(AnalyticActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    //Чтение смс и Анализ
    void Inbox_Read()
    {
        final String SMS_URI_INBOX = "content://sms/inbox";
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
        Cursor cur = getContentResolver().query(uri, projection, null, null, null);

        String[] columns = new String[] { "address", "person", "date", "body", "type" };

        pos = 0;

        String[] phoneNumber = new String[] { "900" };
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor1 = contentResolver.query(Uri.parse("content://sms/inbox"), new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, "address=?", phoneNumber, null);
        StringBuffer msgData = new StringBuffer();
        if (cursor1.moveToFirst())
        {
            do
            {
                String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));

                Inbox_number[pos] = number;
                Inbox_name[pos] = name;

                if(date!=null) {
                    long l = Long.parseLong(date);
                    Date d = new Date(l);
                    Inbox_date[pos] = DateFormat.getDateInstance(DateFormat.LONG).format(d);
                    Inbox_type[pos] = DateFormat.getTimeInstance().format(d);
                }
                else {
                        Inbox_date[pos]=date;
                        Inbox_type[pos]=type;
                }

                Inbox_msg[pos] = msg;

                pos+=1;
            }
            while (cursor1.moveToNext());
        }
        else
        {
            textView.setText("no message from this contact"+phoneNumber);
        }

        String str = "ggg";
        int i = 0;

        while (Inbox_msg[i]!=null) {

            // Расходы
            if (Inbox_msg[i].contains("Покупка ")) {
                str = Inbox_msg[i].split("Покупка ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = -Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch (NumberFormatException e) {}
            }

            if (Inbox_msg[i].contains("Оплата ")) {
                str = Inbox_msg[i].split("Оплата ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = -Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("перевод ")) {
                str = Inbox_msg[i].split("перевод ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = -Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("Выдача ")) {
                str = Inbox_msg[i].split("Выдача ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = -Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("мобильный банк за ")) {
                str = Inbox_msg[i].split("мобильный банк за ")[1].split("р")[0].split(" ")[1];
                try {
                    balance_arr[balance_i] = -Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            // Доходы
            if (Inbox_msg[i].contains("Перевод ")) {
                str = Inbox_msg[i].split("Перевод ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("зачисление ")) {
                str = Inbox_msg[i].split("зачисление ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("Зачисление ")) {
                str = Inbox_msg[i].split("Зачисление ")[1].split("р")[0];
                try {
                    balance_arr[balance_i] = Double.parseDouble(str);
                    balance_data[balance_i] = Inbox_date[i];
                    balance_i += 1;
                } catch(NumberFormatException e){}
            }

            i += 1;
        }

        // Подсчитываем баланс
        double balance = 0;
        for (int j=0; j < balance_i; j++) {
            balance = balance + balance_arr[j];
            balance_arr_str[j] = String.valueOf(balance_arr[j]);
        }

        textView.setText("Баланс: ..." );//+ String.format("%.2f", balance));

        // Выводим список доходов и расходов
        // используем адаптер данных
        // сначала чистим список
        listView.setAdapter(null);

        // Обьявляем массив значений-ключей и заполняем его
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map;

        for (int j=0; j < balance_i; j++) {
            map = new HashMap<>();
            map.put("text1",String.valueOf(balance_arr[j]));
            map.put("text2",balance_data[j]);
            list.add(map);
        }

        SimpleAdapter m= new SimpleAdapter(getApplicationContext(), list,
                                            R.layout.list_item,
                                            new String[] {"text1", "text2"},
                                            new int[] {R.id.text1,R.id.text2});
        listView.setAdapter(m);
    }

    // Нажатие на кнопку анализа
    public void onStatButtonClick(View view)
    {
        Intent i1 = new Intent(this,StatisticActivity.class);
        i1.putExtra("balance_arr", balance_arr);
        i1.putExtra("balance_data", balance_data);
        i1.putExtra("balance_i", balance_i);
        startActivity(i1);
    }
}