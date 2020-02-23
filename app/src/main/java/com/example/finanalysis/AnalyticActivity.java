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

        // Сбербанк
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
                    Inbox_date[pos] = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
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
            textView.setText("нет сообщений по этому контакту"+phoneNumber);
        }

        String str = "ggg";
        String str_about_txt = "ggg";
        int i = 0;

        while (Inbox_msg[i]!=null) {

            // Расходы
            if (Inbox_msg[i].contains("Покупка ")) {
                str = Inbox_msg[i].split("Покупка ")[1].split("р")[0];
                str_about_txt = Inbox_msg[i].split("Покупка ")[1].split("р ")[1].split(" Баланс")[0];
                try {
                    checks_array.add(new Check(str_about_txt,-Float.parseFloat(str),Inbox_date[i]));
                } catch (NumberFormatException e) {}
            }

            if (Inbox_msg[i].contains("Оплата ")) {
                str = Inbox_msg[i].split("Оплата ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Оплата в интернете",-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("перевод ")) {
                str = Inbox_msg[i].split("перевод ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Перевод с карты",-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("Выдача ")) {
                str = Inbox_msg[i].split("Выдача ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Выдача наличных",-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("мобильный банк за ")) {
                str = Inbox_msg[i].split("мобильный банк за ")[1].split("р")[0].split(" ")[1];
                try {
                    checks_array.add(new Check("Мобильный банк",-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            // Доходы
            if (Inbox_msg[i].contains("Перевод ")) {
                str = Inbox_msg[i].split("Перевод ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Получен перевод",Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("зачисление ")) {
                str = Inbox_msg[i].split("зачисление ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Зачисление",Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("Зачисление ")) {
                str = Inbox_msg[i].split("Зачисление ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Зачисление",Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            i += 1;
        }

        // ВТБ
        phoneNumber = new String[] { "VTB" };
        contentResolver = getContentResolver();
        cursor1 = contentResolver.query(Uri.parse("content://sms/inbox"), new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, "address=?", phoneNumber, null);
        msgData = new StringBuffer();
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
                    Inbox_date[pos] = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
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
            textView.setText("Нет сообщение от "+phoneNumber);
        }

        str = "ggg";
        str_about_txt = "ggg";
        i = 0;

        while (Inbox_msg[i]!=null) {

            // Расходы
            if (Inbox_msg[i].contains("Oplata ")) {
                str = Inbox_msg[i].split("Oplata ")[1].split(" RUB")[0];
                str_about_txt = Inbox_msg[i].split("Oplata ")[1].split("RUB;")[1].split(";")[0];
                try {
                    checks_array.add(new Check(str_about_txt,-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("spisanie ")) {
                str = Inbox_msg[i].split("spisanie ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Перевод",-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("snyatie ")) {
                str = Inbox_msg[i].split("snyatie ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Выдача наличных",-Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            // Доходы
            if (Inbox_msg[i].contains("postuplenie zarabotnoy plati ")) {
                str = Inbox_msg[i].split("postuplenie zarabotnoy plati ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Зарплата",Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            if (Inbox_msg[i].contains("postuplenie ")) {
                str = Inbox_msg[i].split("postuplenie ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Получен перевод",Float.parseFloat(str),Inbox_date[i]));
                } catch(NumberFormatException e){}
            }

            i += 1;
        }

        // Сортировка по дате /////////////////////////////////////////////////////////////////////
        int maxData = 0;
        int max_kData = 0;
        int sortData = 0;
        long l;
        Date d;

        Check exchange_check = new Check(); // Чек для алгоритма обмена

        for (int j=0; j < checks_array.size(); j++) {
            maxData = Integer.parseInt(checks_array.get(j).getStringDate());
            max_kData = j;

            for (int k = j; k < checks_array.size(); k++) {
                sortData = Integer.parseInt(checks_array.get(k).getStringDate());

                if (sortData>maxData)
                {
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
        for (int j=0; j < checks_array.size(); j++) {
            balance = balance + checks_array.get(j).getExpense_revenue();
        }

        textView.setText("Список" );//+ String.format("%.2f", balance));

        // Выводим список доходов и расходов
        // используем адаптер данных
        // сначала чистим список
        listView.setAdapter(null);

        // Обьявляем массив значений-ключей и заполняем его
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> map;

        for (int j=0; j < checks_array.size(); j++) {
            map = new HashMap<>();
            map.put("text1",checks_array.get(j).getStringExpense_revenue());
            map.put("text2",checks_array.get(j).getDate());
            map.put("text3",checks_array.get(j).getName());
            list.add(map);
        }

        SimpleAdapter m= new SimpleAdapter(getApplicationContext(), list,
                                            R.layout.list_item,
                                            new String[] {"text1", "text2","text3"},
                                            new int[] {R.id.text1,R.id.text2,R.id.text3});
        listView.setAdapter(m);
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
}