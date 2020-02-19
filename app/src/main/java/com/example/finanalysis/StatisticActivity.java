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

    double[] balance_arr = new double[1000]; // Массив доходов и расходов
    String[] balance_data = new String[1000]; // Массив дат доходов и расходов
    String[] about_arr = new String[1000]; // Массив описания расходов
    int balance_i; // Длина массива баланса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            balance_arr = arguments.getDoubleArray("balance_arr");
            balance_data = arguments.getStringArray("balance_data");
            about_arr = arguments.getStringArray("about_arr");
            balance_i = arguments.getInt("balance_i");
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
        for (int i=0; i<balance_i;i++)
        {
            if (balance_arr[i]<0) {
                mian += balance_arr[i];
                n_expence +=1;
            }
        }

        mian /= n_expence;

        //Считаем дисперсию
        for (int i=0; i<balance_i;i++)
        {
            if (balance_arr[i]<0) sigma += (balance_arr[i] - mian) * (balance_arr[i] - mian);
        }

        sigma /= n_expence; // Дисперсия
        sigma = Math.sqrt(sigma); // Среднеквадратическое отклонение

        // Подсчитываем средней чек
        n_expence = 0;
        for (int i=0; i<balance_i; i++)
        {
            if (balance_arr[i] < 0) {
                if (balance_arr[i] > mian-3*sigma) {
                    mian_expence += balance_arr[i];
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

            // Закругляем углы у линии отрисовки
            float radius = 50.0f;
            CornerPathEffect cornerPathEffect = new CornerPathEffect(radius);
            paint.setPathEffect(cornerPathEffect);

            double[] balance_arr_0 = new double[balance_i];
            String[] balance_data_0 = new String[balance_i];

            // Меняем местами данные, т.к. в начальном время идет в обратную сторону
            for (int i=balance_i; i>0; i--)
            {
                balance_arr_0[balance_i-i] = balance_arr[i];
                balance_data_0[balance_i-i] = balance_data[i];
            }

            double[] sum_bal = new double[1000];
            double min_bal = balance_arr_0[0];
            double max_bal = balance_arr_0[0];

            sum_bal[0] = balance_arr_0[0];

            // Заполнение изменения баланса и поиск минимального и максимального баланса
            for (int i=1; i<balance_i; i++)
            {
                sum_bal[i] = sum_bal[i-1] + balance_arr_0[i];

                if (min_bal > sum_bal[i]) min_bal = sum_bal[i];
                if (max_bal < sum_bal[i]) max_bal = sum_bal[i];
            }

            //                                 100 - отступ от краев
            float pix_y = (canvas.getHeight()-100) / (float)(max_bal - min_bal); // Определение минимального изменения для отрисовки
            float pix_x = canvas.getWidth() / (float)(balance_i);

            path.moveTo(0, 50 + (float)max_bal * pix_y); // Начало отрисовки графика

            for (int i=0; i<balance_i; i++)
            {
                path.lineTo((i+1)*pix_x, 50 + (float)max_bal * pix_y - (float)sum_bal[i]*pix_y);
                canvas.drawPath(path, paint);
            }

            //Paint paintText = new Paint();
            //paintText.setColor(Color.BLACK);
            //paintText.setTextSize(40);
            //canvas_s.drawText(String.valueOf(pix_y),200,200, paintText);

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

            float[] balance_arr_0 = new float[balance_i];
            String[] balance_data_0 = new String[balance_i];

            // Меняем местами данные, т.к. в начальном время идет в обратную сторону
            for (int i=balance_i; i>0; i--)
            {
                balance_arr_0[balance_i-i] = (float) balance_arr[i];
                balance_data_0[balance_i-i] = balance_data[i];
            }

            float max_ex = 0;

            ArrayList<Float> expense_arr = new ArrayList<Float>();

            // Поиск максимального расхода
            for (int i=0; i<balance_i; i++)
            {
                if (balance_arr_0[i]<0) {
                    expense_arr.add(balance_arr_0[i]);
                    if (max_ex > balance_arr_0[i]) max_ex = balance_arr_0[i];
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

    // Диаграмма расходов
    public void onBtn_DigrammExpence(View view)
    {
        Intent i1 = new Intent(this,ExpenseDiagramActivity.class);
        i1.putExtra("balance_arr", balance_arr);
        i1.putExtra("balance_data", balance_data);
        i1.putExtra("about_arr", about_arr);
        i1.putExtra("balance_i", balance_i);
        startActivity(i1);
    }
}
