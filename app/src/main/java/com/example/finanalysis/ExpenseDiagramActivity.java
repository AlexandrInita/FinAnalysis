package com.example.finanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class ExpenseDiagramActivity extends AppCompatActivity {

    HashMap<String,Float> expenseAboutMap; // Категории расходов

    ImageView imageView; // Диаграмма
    ListView listView; // Список расходов

    ArrayList<Check> checks_array = new ArrayList<>(); // Чеки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_diagram);

        // Прием значений
        Bundle arguments = getIntent().getExtras();

        if(arguments!=null) {
           float[] check_ex_rev = arguments.getFloatArray("check_ex_rev");
           String[] check_date = arguments.getStringArray("check_date");
           String[] check_about = arguments.getStringArray("check_about");

            for (int i = 0; i < check_ex_rev.length; i++) {
                checks_array.add(new Check(check_about[i], check_ex_rev[i], check_date[i]));
            }
        }

        // Инициализация
        imageView = findViewById(R.id.imageView);
        listView = findViewById(R.id.listView);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //showDiagram();
    }

    // Нажатие кнопки полной диаграммы расходов
    public void onBtn_showFullDiagram(View view)
    {
        createFullList();
        showDiagram();
    }

    // Нажатие кнопки сжатой диаграммы расходов
    public void onBtn_showShortDiagram(View view)
    {
        createShortList();
        showDiagram();
    }

    // Отрисовка диаграммы расходов
    void showDiagram()
    {
        try {
            Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            BitmapShader shader;
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);

            RectF rect = new RectF(0.0f, 0.0f, imageView.getWidth(), imageView.getHeight());
            canvas.drawRoundRect(rect, 10.0f, 10.0f, paint);
            //canvas.drawColor(0XFF212121);

            // Диаграмма расходов
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(10);
            Path path = new Path();

            final RectF oval = new RectF();
            float center_x, center_y;
            center_x = canvas.getWidth()/2;
            center_y = canvas.getHeight()/2;
            float radius = 400f;

            float delta = 0; // Минимальная единица зарисовки
            for (String state: expenseAboutMap.keySet()) delta += expenseAboutMap.get(state);
            delta = 360/delta;

            float last_point = 0; // Конец последнего края отрисовоной части

            int k = 1;
            // Рисуем диаграмму расходов
            for (String state: expenseAboutMap.keySet()) {
                oval.set(center_x - radius, center_y - radius, center_x + radius,
                        center_y + radius);
                paint.setColor(0xFFB9F6CA*(k));
                k+=1;
                canvas.drawArc(oval, last_point, expenseAboutMap.get(state)*delta, true, paint);
                last_point += expenseAboutMap.get(state)*delta;
            }

            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Заполнения полного списка
    void createFullList()
    {
        expenseAboutMap = new HashMap<>(); // Категории расходов

        for (int i=0; i < checks_array.size(); i++) {
            if (checks_array.get(i).getExpense_revenue() < 0){
                try {
                    expenseAboutMap.put(checks_array.get(i).getName(), expenseAboutMap.get(checks_array.get(i).getName()) - checks_array.get(i).getExpense_revenue());
                } catch (Exception e) {
                    expenseAboutMap.put(checks_array.get(i).getName(), -checks_array.get(i).getExpense_revenue());
                }
            }
        }

        // Выводим список расходов по категориям
        listView.setAdapter(null);

        // Обьявляем массив значений-ключей и заполняем его
        ArrayList<CategoryExpense> list = new ArrayList<>();

        for (String state : expenseAboutMap.keySet()) {
            list.add(new CategoryExpense(String.format("%.18s", state),String.format("%.1f", expenseAboutMap.get(state))));
        }

        CategoryArrayAdapter listAdapter = new CategoryArrayAdapter(this, list);
        listView.setAdapter(listAdapter);
    }

    // Заполнения сжатого списка
    void createShortList()
    {
        expenseAboutMap = new HashMap<>(); // Категории расходов

        for (int i=0; i < checks_array.size(); i++) {
            if (checks_array.get(i).getExpense_revenue() < 0){
                try {
                    expenseAboutMap.put(changeCategory(checks_array.get(i).getName()), expenseAboutMap.get(changeCategory(checks_array.get(i).getName())) - checks_array.get(i).getExpense_revenue());
                } catch (Exception e) {
                    expenseAboutMap.put(changeCategory(checks_array.get(i).getName()), -checks_array.get(i).getExpense_revenue());
                }
            }
        }

        // Выводим список расходов по категориям
        listView.setAdapter(null);

        // Обьявляем массив значений-ключей и заполняем его
        ArrayList<CategoryExpense> list = new ArrayList<>();

        for (String state : expenseAboutMap.keySet()) {
            list.add(new CategoryExpense(String.format("%.18s", state),String.format("%.1f", expenseAboutMap.get(state))));
        }

        CategoryArrayAdapter listAdapter = new CategoryArrayAdapter(this, list);
        listView.setAdapter(listAdapter);
    }

    // Выбор категории
    String changeCategory(String name_check)
    {
        // Магазины, гипермаргеты
        String[] Shop_category = {"KARUSEL", "PLOVDIV", "OKEY", "SPAR", "PYATEROCHKA",
                "MAGNIT", "PEREKRESTOK", "AUCHAN", "PERVAYA POLOSA", "UN.TALLINNSKIY",
                "OVOSHHI", "BELORUSSKIJ DVORIK"};
        for (String s: Shop_category) if (name_check.contains(s)) return "Магазины";

        // Рестараны, кафе, фастфуды
        String[] Food_category = {"BURGERKING", "FUDKORT", "MCDONALDS", "SUSHI", "Sushi", "KFC", "Your Favorite Shaw"};
        for (String s: Food_category) if (name_check.contains(s)) return "Кафе, рестораны";

        // Аптеки
        String[] Apteka_category = {"APTEKA", "Apteka", "LEKA-FARM", "SPB ALTERMED"};
        for (String s: Apteka_category) if (name_check.contains(s)) return "Аптеки";

        // Одежда и обувь
        String[] Clothes_category = {"OSTIN", "CROPP", "34PLAY"};
        for (String s: Clothes_category) if (name_check.contains(s)) return "Гардероб";

        // Расходники
        String[] Ras_category = {"KANTSELYAR", "KOPIRKA"};
        for (String s: Ras_category) if (name_check.contains(s)) return "Расходники";

        // Хобби
        String[] Hobby_category = {"BUKVOED", "LEONARDO"};
        for (String s: Hobby_category) if (name_check.contains(s)) return "Хобби";

        // Интернет покупки
        String[] Net_Hobby_category = {"VKONTAKTE", "GOOGLE", "VK"};
        for (String s: Net_Hobby_category) if (name_check.contains(s)) return "Интернет покупки";

        // Бары
        if (name_check.contains("BAR")) return "Бары";

        // Метро
        if (name_check.contains("METRO")) return "Метро";

        // BROKER
        if (name_check.contains("BROKER")) return "Брокер";

        return  name_check;
    }
}
