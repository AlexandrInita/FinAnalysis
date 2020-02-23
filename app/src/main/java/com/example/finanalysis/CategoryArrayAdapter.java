package com.example.finanalysis;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryArrayAdapter extends ArrayAdapter<CategoryExpense> {
    private Context context;
    private ArrayList<CategoryExpense> stringValues;

    public CategoryArrayAdapter (Context context, ArrayList<CategoryExpense> stringValues)
    {
        super(context, R.layout.list_item_expense_category, stringValues);
        this.context = context;
        this.stringValues = stringValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_expense_category, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.text1);
        TextView textView2 = (TextView) view.findViewById(R.id.text2);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.lin);

        textView.setText(stringValues.get(position).getName());
        textView2.setText(stringValues.get(position).getExpense());

        linearLayout.setBackgroundColor(0xFFB9F6CA*(position+1));
        return view;
    }
}
