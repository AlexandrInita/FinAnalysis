package com.example.finanalysis;

public class CategoryExpense {
    public final String name;
    public final String expense;

    public CategoryExpense(String name, String expense) {
        this.name = name;
        this.expense = expense;
    }

    public String getName()  {return name;}
    public String getExpense() {return expense;}
}
