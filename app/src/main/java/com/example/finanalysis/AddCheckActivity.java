package com.example.finanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddCheckActivity extends AppCompatActivity {

    // База данных
    private SQLiteOpenHelper finDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private static final String TABLE_CHECK = "CHECKTABLE";

    String a_str = ""; // Строка для числа a
    int state = 0; // Состояние операций 1,2,3,4 = +,-,*,/
    int zz = 0; // Знак +- в зависимости доход или расход

    Button btn_str_ex_rev;
    TextView text_date;
    TextView text_value;
    EditText editText_CheckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null) {
            zz = arguments.getInt("zz");
        }

        if (zz < 0) setTitle("Добавление расхода");
        else setTitle("Добавление дохода");

        btn_str_ex_rev = findViewById(R.id.button_check_text);
        text_date = findViewById(R.id.text_date);
        text_value = findViewById(R.id.text_value);
        editText_CheckName = findViewById(R.id.editText_CheckName);

        btn_str_ex_rev.setText("0");
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        text_date.setText(dateFormat.format(currentDate));
    }

    // Нажатие на кнопку 1
    public void on1ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("1");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "1");
            }
        }
    }

    // Нажатие на кнопку 2
    public void on2ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("2");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "2");
            }
        }
    }

    // Нажатие на кнопку 3
    public void on3ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("3");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "3");
            }
        }
    }

    // Нажатие на кнопку 4
    public void on4ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("4");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "4");
            }
        }
    }

    // Нажатие на кнопку 5
    public void on5ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("5");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "5");
            }
        }
    }

    // Нажатие на кнопку 6
    public void on6ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("6");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "6");
            }
        }
    }

    // Нажатие на кнопку 7
    public void on7ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("7");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "7");
            }
        }
    }

    // Нажатие на кнопку 8
    public void on8ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("8");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "8");
            }
        }
    }

    // Нажатие на кнопку 9
    public void on9ButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 11)
        {
            if (btn_str_ex_rev.getText() == "0")
            {
                btn_str_ex_rev.setText("9");
            } else
            {
                btn_str_ex_rev.setText(btn_str_ex_rev.getText() + "9");
            }
        }
    }

    // Нажатие на кнопку 0
    public void on0ButtonClick(View view) {
        if ((btn_str_ex_rev.getText().length() < 11) && (btn_str_ex_rev.getText() != "0") )
            btn_str_ex_rev.setText(btn_str_ex_rev.getText()+"0");
    }

    // Нажатие на кнопку точка
    public void onPointButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() < 9)
            if (!btn_str_ex_rev.getText().toString().contains(".")) btn_str_ex_rev.setText(btn_str_ex_rev.getText()+".");
    }

    // Нажатие на кнопку удалить символ
    public void onDeleteSimbolButtonClick(View view) {
        if (btn_str_ex_rev.getText().length() > 1)
            btn_str_ex_rev.setText(btn_str_ex_rev.getText().toString().substring(0,btn_str_ex_rev.getText().length()-1));
        else if (btn_str_ex_rev.getText().length() == 1)
        {
            btn_str_ex_rev.setText("0");
        }
    }

    // Нажатие на кнопку очистить
    public void onAllDeleteButtonClick(View view) {
        btn_str_ex_rev.setText("0");
    }

    // Нажатие на кнопку плюс
    public void onPlusButtonClick(View view) {
        if (a_str == "") {
            a_str = btn_str_ex_rev.getText().toString();
            text_value.setText(a_str);
            btn_str_ex_rev.setText("0");
            state = 1;
        }
        else {
            btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) * Float.parseFloat(btn_str_ex_rev.getText().toString())));
            text_value.setText("");
            a_str = "";
            state = 0;
        }
    }

    // Нажатие на кнопку минус
    public void onMinusButtonClick(View view) {
        if (a_str == "") {
            a_str = btn_str_ex_rev.getText().toString();
            text_value.setText(a_str);
            btn_str_ex_rev.setText("0");
            state = 2;
        }
        else {
            btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) - Float.parseFloat(btn_str_ex_rev.getText().toString())));
            text_value.setText("");
            a_str = "";
            state = 0;
        }
    }

    // Нажатие на кнопку умножить
    public void onMultiplyButtonClick(View view) {
        if (a_str == "") {
            a_str = btn_str_ex_rev.getText().toString();
            text_value.setText(a_str);
            btn_str_ex_rev.setText("0");
            state = 3;
        }
        else {
            btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) * Float.parseFloat(btn_str_ex_rev.getText().toString())));
            text_value.setText("");
            a_str = "";
            state = 0;
        }
    }

    // Нажатие на кнопку делить
    public void onDivideButtonClick(View view) {
        try {
            if (a_str == "") {
                a_str = btn_str_ex_rev.getText().toString();
                text_value.setText(a_str);
                btn_str_ex_rev.setText("0");
                state = 4;
            } else {
                if (Float.parseFloat(btn_str_ex_rev.getText().toString()) != 0) {
                    btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) / Float.parseFloat(btn_str_ex_rev.getText().toString())));
                    text_value.setText("");
                    a_str = "";
                    state = 0;
                }
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Нажатие на кнопку равно
    public void onEyuallyButtonClick(View view) {
        try {
            if (a_str == "") {
            }
            else {
                switch (state) {
                    case (1) : {
                        btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) + Float.parseFloat(btn_str_ex_rev.getText().toString())));
                        break;
                       }
                    case (2) : {
                        btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) - Float.parseFloat(btn_str_ex_rev.getText().toString())));
                        break;
                    }
                    case (3) : {
                        btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) * Float.parseFloat(btn_str_ex_rev.getText().toString())));
                        break;
                    }
                    case (4) : {
                        btn_str_ex_rev.setText(String.valueOf(Float.parseFloat(a_str) / Float.parseFloat(btn_str_ex_rev.getText().toString())));
                        break;
                    }
                    default: break;
                }
                text_value.setText("");
                a_str = "";
                state = 0;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Нажатие на кнопку выбор категории
    public void onCategorySelectionButtonClick(View view) {
        try {
            finDatabaseHelper = new FinDatabaseHelper(this);
            db = finDatabaseHelper.getWritableDatabase();

            // Заполнение базы данных
            ContentValues values = new ContentValues();
            values.put("NAME", editText_CheckName.getText().toString());
            values.put("DESCRIPTION", "");
            values.put("CATEGORY", "");
            values.put("DATE", text_date.getText().toString());
            values.put("EXPENSEREVENUE", zz * Float.parseFloat(btn_str_ex_rev.getText().toString()));
            db.insert(TABLE_CHECK, null, values);

            finDatabaseHelper.close();
            db.close();

            Toast toast = Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT);
            toast.show();

            finish();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "База данных недоступна", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
