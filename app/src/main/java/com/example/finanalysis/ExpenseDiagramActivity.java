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

public class ExpenseDiagramActivity extends AppCompatActivity {

    double[] balance_arr = new double[1000]; // Массив доходов и расходов
    String[] balance_data = new String[1000]; // Массив дат доходов и расходов
    String[] about_arr = new String[1000]; // Массив описания расходов
    int balance_i; // Длина массива баланса

    ImageView imageView; // Диаграмма
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_diagram);

        // Прием значений
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            balance_arr = arguments.getDoubleArray("balance_arr");
            balance_data = arguments.getStringArray("balance_data");
            about_arr = arguments.getStringArray("about_arr");
            balance_i = arguments.getInt("balance_i");
        }

        // Инициализация
        imageView = findViewById(R.id.imageView);
        btn = findViewById(R.id.button2);
        showDiagram();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        showDiagram();
    }

    // Нажатие кнопки диаграммы расходов
    public void onBtn_showDiagram(View view)
    {
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

            float[] aaa = {1,1,2,3};

            final RectF oval = new RectF();
            float center_x, center_y;
            center_x = canvas.getWidth()/2;
            center_y = canvas.getHeight()/2;
            float radius = 300f;

            float delta = 0; // Минимальная единица зарисовки
            for (int i=0; i<4;i++) delta +=aaa[i];
            delta = 360/delta;

            float last_point = 0; // Конец последнего края отрисовоной части

            // Рисуем диаграмму расходов
            for (int i=0; i<4;i++) {
                oval.set(center_x - radius, center_y - radius, center_x + radius,
                        center_y + radius);
                paint.setColor(0xFFB9F6CA*(i+1));
                canvas.drawArc(oval, last_point, aaa[i]*delta, true, paint);
                last_point += aaa[i]*delta;
            }

            imageView.setImageBitmap(bitmap);
            btn.setText("Диаграмма");

        } catch (Exception e) {btn.setText("Жми");}
    }
}
