package com.example.finanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticActivity extends AppCompatActivity  {

    TextView textView1;
    TextView textView2;
    ImageView imageView; // График

    ArrayList<Check> checks_array = new ArrayList<>(); // Чеки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            float[] check_ex_rev = arguments.getFloatArray("check_ex_rev");
            String[] check_date = arguments.getStringArray("check_date");
            String[] check_about = arguments.getStringArray("check_about");

            for (int i=0; i < check_ex_rev.length; i++) {
                checks_array.add(new Check(check_about[i],check_ex_rev[i],check_date[i]));
            }

        }

        textView1 = findViewById(R.id.textView); // Текст "График"
        textView2 = findViewById(R.id.textView2); // Текст среднего чека
        imageView = findViewById(R.id.imageView); // График

        double mian_expence = 0; // Средний чек
        double n_expence = 0; // Количество расходов
        double sigma = 0; // Среднеквадротическое отклонение
        double mian = 0;

        // Подсчет среднего чека с выбросом крайни высоких расходов (свыше трех сигм)
        // Посчитываем среднее
        for (int i=0; i<checks_array.size();i++)
        {
            if (checks_array.get(i).getExpense_revenue() < 0) {
                mian += checks_array.get(i).getExpense_revenue();
                n_expence +=1;
            }
        }

        mian /= n_expence;

        //Считаем дисперсию
        for (int i=0; i < checks_array.size();i++)
        {
            if (checks_array.get(i).getExpense_revenue() < 0)
                sigma += (checks_array.get(i).getExpense_revenue() - mian) * (checks_array.get(i).getExpense_revenue() - mian);
        }

        sigma /= n_expence; // Дисперсия
        sigma = Math.sqrt(sigma); // Среднеквадратическое отклонение

        // Подсчитываем средней чек
        n_expence = 0;
        for (int i=0; i < checks_array.size(); i++)
        {
            if (checks_array.get(i).getExpense_revenue() < 0) {
                if (checks_array.get(i).getExpense_revenue() > mian-3*sigma) {
                    mian_expence += checks_array.get(i).getExpense_revenue();
                    n_expence += 1;
                }
            }
        }

        mian_expence /= n_expence;

        textView2.setText(String.format("Средний чек: %.2f", -mian_expence));
    }

    // Нажатие кнопки баланс для отрисовки графика баланса
    public void onBtn_balance(View view)
    {
        try {
            // Для отрисовки графика
            Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Чтобы сделать края графика округлыми
            BitmapShader shader;
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);
            RectF rect = new RectF(0.0f, 0.0f, imageView.getWidth(), imageView.getHeight());
            canvas.drawRoundRect(rect, 10.0f, 10.0f, paint);

            // График извенения баланса
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setColor(0xFF69F0AE);
            Path path = new Path();

            // Закругляем углы у линии отрисовки
            float radius = 50.0f;
            CornerPathEffect cornerPathEffect = new CornerPathEffect(radius);
            paint.setPathEffect(cornerPathEffect);

            float[] sum_bal = new float[1000];
            float min_bal = checks_array.get(checks_array.size()-1).getExpense_revenue();
            float max_bal = checks_array.get(checks_array.size()-1).getExpense_revenue();

            // Заполнение наоборот, т.к. список был наоборот
            sum_bal[0] = checks_array.get(checks_array.size()-1).getExpense_revenue();

            // Заполнение изменения баланса и поиск минимального и максимального баланса
            for (int i= 1; i < checks_array.size(); i++)
            {
                sum_bal[i] = sum_bal[i-1] + checks_array.get(checks_array.size()-1-i).getExpense_revenue();

                if (min_bal > sum_bal[i]) min_bal = sum_bal[i];
                if (max_bal < sum_bal[i]) max_bal = sum_bal[i];
            }

            //                                 100 - отступ от краев
            float pix_y = (canvas.getHeight()-100) / (float)(max_bal - min_bal); // Определение минимального изменения для отрисовки
            float pix_x = canvas.getWidth() / checks_array.size();

            path.moveTo(0, 50 + (float)max_bal * pix_y); // Начало отрисовки графика

            for (int i=0; i < checks_array.size(); i++)
            {
                path.lineTo((i+1)*pix_x, 50 + (float)max_bal * pix_y - (float)sum_bal[i]*pix_y);
                canvas.drawPath(path, paint);
            }

            imageView.setImageBitmap(bitmap);

            textView1.setText("Баланс");
        } catch (Exception e) { textView1.setText("Error"); }
    }

    // Гистограмма расходов
    public void onBtn_Expence(View view)
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

            // График извенения баланса
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setColor(0xFF69F0AE);
            Path path = new Path();

            float max_ex = 0;

            ArrayList<Float> expense_arr = new ArrayList<Float>();

            // Поиск максимального расхода
            // Отсчет наоборот
            for (int i=checks_array.size()-1; i > 0; i--)
            {
                if (checks_array.get(i).getExpense_revenue() < 0) {
                    expense_arr.add(checks_array.get(i).getExpense_revenue());
                    if (max_ex > checks_array.get(i).getExpense_revenue())
                        max_ex = checks_array.get(i).getExpense_revenue();
                }
            }

            //                                 100 - отступ от верхнего края
            float pix_y = (canvas.getHeight()-100) / max_ex; // Определение минимального изменения для отрисовки

            for (int i=0; i<expense_arr.size(); i++)
            {
                path.moveTo(5 + (i)*5, canvas.getHeight()); // Начало отрисовки
                path.lineTo(5 + (i)*5, canvas.getHeight() - expense_arr.get(i)*pix_y);
                canvas.drawPath(path, paint);
            }

            imageView.setImageBitmap(bitmap);

            textView1.setText("Расходы");
        } catch (Exception e) { textView1.setText("Error"); }
    }

    // Нажатие кнопки диаграмма расходов для перехода на другую активность
    public void onBtn_DigrammExpence(View view)
    {
        float[] check_ex_rev = new float[checks_array.size()];
        String[] check_date = new String[checks_array.size()];
        String[] check_about = new String[checks_array.size()];

        for (int j=0; j < checks_array.size(); j++) {
            check_ex_rev[j] = checks_array.get(j).getExpense_revenue();
            check_date[j] = checks_array.get(j).getDate();
            check_about[j] = checks_array.get(j).getName();
        }

        Intent i1 = new Intent(this,ExpenseDiagramActivity.class);
        i1.putExtra("check_ex_rev", check_ex_rev);
        i1.putExtra("check_date", check_date);
        i1.putExtra("check_about", check_about);
        startActivity(i1);
    }
}
