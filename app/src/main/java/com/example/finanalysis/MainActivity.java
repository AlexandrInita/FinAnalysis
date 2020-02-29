package com.example.finanalysis;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    // База данных
    private SQLiteOpenHelper finDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private static final String TABLE_CHECK = "CHECKTABLE";

    // Массивы для считывания строк
    String[] Inbox_name=new String[1000],
            Inbox_number=new String[1000],
            Inbox_date=new String[1000],
            Inbox_type=new String[1000],
            Inbox_msg=new String[1000];

    int pos=0;

    ArrayList<Check> checks_array =  new ArrayList<>(); // Массив чеков

    Dialog dialog; // Диалоговое окно

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        addSMS();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    // Меню
    public void selectDrawerItem(MenuItem menuItem) {
        Toast toast;
        switch(menuItem.getItemId()) {
            case R.id.nav_first:
                toast = Toast.makeText(this, "1", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.nav_second:
                toast = Toast.makeText(this, "2", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.nav_third: // Очистить таблицу
                deleteTable();
                break;
            default:
                toast = Toast.makeText(this, "4", Toast.LENGTH_SHORT);
                toast.show();
        }

        menuItem.setChecked(true);
        mDrawer.closeDrawers();
    }

///////////////////////////////////////////////////////////////////////////////////////////////

    // Нажатие на кнопку финансы
    public void onAnalButtonClick(View view) {
        Intent i1 = new Intent(this, AnalyticActivity.class);
        startActivity(i1);
    }

    // Добавитьобавить с смс
    public void deleteTable() {

        dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.dialog_view_ok);

        TextView text = (TextView) dialog.findViewById(R.id.textView);
        text.setText("Уверены, что хотите очистить базу данных?");

        Button button_cancle =  (Button) dialog.findViewById(R.id.btn_cancle);
        Button button_ok =  (Button) dialog.findViewById(R.id.btn_ok);

        finDatabaseHelper = new FinDatabaseHelper(this);

        button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finDatabaseHelper.close();
                dialog.dismiss();
            }
        });

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Очистка базы данных
                db = finDatabaseHelper.getWritableDatabase();
                db.delete(TABLE_CHECK, null, null);
                finDatabaseHelper.close();
                db.close();
                Toast toast = Toast.makeText(MainActivity.this, "Очищено", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

        // Добавитьобавить с смс
    public void addSMS() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            // Если дано разрешение
            checks_array =  new ArrayList<>(); // Очистить массив чеков
            Inbox_Read();
        } else {
            // Иначе установить разрешение
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        try {
            finDatabaseHelper = new FinDatabaseHelper(this);
            db = finDatabaseHelper.getWritableDatabase();

            // Заполнение базы данных
            for (int j = 0; j < checks_array.size(); j++) {
                ContentValues values = new ContentValues();
                values.put("NAME", checks_array.get(j).getName());
                values.put("DESCRIPTION", checks_array.get(j).getDescription());
                values.put("CATEGORY", "");
                values.put("DATE", checks_array.get(j).getDate());
                values.put("EXPENSEREVENUE", checks_array.get(j).getExpense_revenue());
                db.insert(TABLE_CHECK, null, values);
            }

            finDatabaseHelper.close();
            db.close();

            Toast toast = Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT);
            toast.show();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "База данных недоступна", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Нажатие на кнопку расход
    public void onExpenseButtonClick (View view)
    {
        Intent i1 = new Intent(this, AddCheckActivity.class);
        i1.putExtra("zz", -1);
        startActivity(i1);
    }


    // Нажатие на кнопку доход
    public void onRevenueButtonClick (View view)
    {
        Intent i1 = new Intent(this, AddCheckActivity.class);
        i1.putExtra("zz", 1);
        startActivity(i1);
    }

    //Чтение смс и Анализ
    void Inbox_Read ()
    {
        final String SMS_URI_INBOX = "content://sms/inbox";
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = getContentResolver().query(uri, projection, null, null, null);

        String[] columns = new String[]{"address", "person", "date", "body", "type"};

        pos = 0;

        // Сбербанк
        String[] phoneNumber = new String[]{"900"};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor1 = contentResolver.query(Uri.parse("content://sms/inbox"), new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"}, "address=?", phoneNumber, null);
        StringBuffer msgData = new StringBuffer();
        if (cursor1.moveToFirst()) {
            do {
                String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));

                Inbox_number[pos] = number;
                Inbox_name[pos] = name;

                if (date != null) {
                    long l = Long.parseLong(date);
                    Date d = new Date(l);
                    Inbox_date[pos] = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
                    Inbox_type[pos] = DateFormat.getTimeInstance().format(d);
                } else {
                    Inbox_date[pos] = date;
                    Inbox_type[pos] = type;
                }

                Inbox_msg[pos] = msg;

                pos += 1;
            }
            while (cursor1.moveToNext());
        } else {
            Toast toast = Toast.makeText(this, "нет сообщений по этому контакту" + phoneNumber, Toast.LENGTH_SHORT);
            toast.show();
        }

        String str = "";
        String str_about_txt = "";
        int i = 0;

        while (Inbox_msg[i] != null) {

            // Расходы
            if (Inbox_msg[i].contains("Покупка ")) {
                str = Inbox_msg[i].split("Покупка ")[1].split("р")[0];
                str_about_txt = Inbox_msg[i].split("Покупка ")[1].split("р ")[1].split(" Баланс")[0];
                try {
                    checks_array.add(new Check(str_about_txt, -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("Оплата ")) {
                str = Inbox_msg[i].split("Оплата ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Оплата в интернете", -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("перевод ")) {
                str = Inbox_msg[i].split("перевод ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Перевод с карты", -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("Выдача ")) {
                str = Inbox_msg[i].split("Выдача ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Выдача наличных", -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("мобильный банк за ")) {
                str = Inbox_msg[i].split("мобильный банк за ")[1].split("р")[0].split(" ")[1];
                try {
                    checks_array.add(new Check("Мобильный банк", -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            // Доходы
            if (Inbox_msg[i].contains("Перевод ")) {
                str = Inbox_msg[i].split("Перевод ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Получен перевод", Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("зачисление ")) {
                str = Inbox_msg[i].split("зачисление ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Зачисление", Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("Зачисление ")) {
                str = Inbox_msg[i].split("Зачисление ")[1].split("р")[0];
                try {
                    checks_array.add(new Check("Зачисление", Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            i += 1;
        }

        // ВТБ
        phoneNumber = new String[]{"VTB"};
        contentResolver = getContentResolver();
        cursor1 = contentResolver.query(Uri.parse("content://sms/inbox"), new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"}, "address=?", phoneNumber, null);
        msgData = new StringBuffer();
        if (cursor1.moveToFirst()) {
            do {
                String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));

                Inbox_number[pos] = number;
                Inbox_name[pos] = name;

                if (date != null) {
                    long l = Long.parseLong(date);
                    Date d = new Date(l);
                    Inbox_date[pos] = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
                    Inbox_type[pos] = DateFormat.getTimeInstance().format(d);
                } else {
                    Inbox_date[pos] = date;
                    Inbox_type[pos] = type;
                }

                Inbox_msg[pos] = msg;

                pos += 1;
            }
            while (cursor1.moveToNext());
        } else {
            Toast toast = Toast.makeText(this, "нет сообщений по этому контакту" + phoneNumber, Toast.LENGTH_SHORT);
            toast.show();
        }

        str = "";
        str_about_txt = "";
        i = 0;

        while (Inbox_msg[i] != null) {

            // Расходы
            if (Inbox_msg[i].contains("Oplata ")) {
                str = Inbox_msg[i].split("Oplata ")[1].split(" RUB")[0];
                str_about_txt = Inbox_msg[i].split("Oplata ")[1].split("RUB;")[1].split(";")[0];
                try {
                    checks_array.add(new Check(str_about_txt, -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("spisanie ")) {
                str = Inbox_msg[i].split("spisanie ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Перевод", -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("snyatie ")) {
                str = Inbox_msg[i].split("snyatie ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Выдача наличных", -Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            // Доходы
            if (Inbox_msg[i].contains("postuplenie zarabotnoy plati ")) {
                str = Inbox_msg[i].split("postuplenie zarabotnoy plati ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Зарплата", Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            if (Inbox_msg[i].contains("postuplenie ")) {
                str = Inbox_msg[i].split("postuplenie ")[1].split(" RUB")[0];
                try {
                    checks_array.add(new Check("Получен перевод", Float.parseFloat(str), Inbox_date[i],Inbox_msg[i]));
                } catch (NumberFormatException e) {
                }
            }

            i += 1;
        }
    }
}