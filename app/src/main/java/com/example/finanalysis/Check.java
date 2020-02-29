package com.example.finanalysis;

public class Check {

    public String name; // Имя чека
    public float expense_revenue; // Расход или доход
    public String date; // Дата
    public String description; // Описание

    public Check() {
        this.name = "";
        this.expense_revenue = 0;
        this.date = "";
        this.description = "";
    }

    public Check(String name, float expense_revenue, String date, String description) {
        this.name = name;
        this.expense_revenue = expense_revenue;
        this.date = date;
        this.description = description;
    }

    public Check(String name, float expense_revenue, String date) {
        this.name = name;
        this.expense_revenue = expense_revenue;
        this.date = date;
        this.description = "";
    }

    // Записать чек
    public void setCheck(String name, float expense_revenue, String date, String description) {
        this.name = name;
        this.expense_revenue = expense_revenue;
        this.date = date;
        this.description = description;
    }

    // Записать чек
    public void setCheck(Check check) {
        this.setCheck(check.name,check.expense_revenue,check.date,check.description);
    }

    // Вывести чек
    public Check getCheck() {
        return this;
    }

    // Записать описание чека
    public void setName(String name)  {this.name = name;}

    // Записать дохов или расход
    public void setExpense_revenue(float expense_revenue) {this.expense_revenue = expense_revenue;}

    // Записать дату
    public void setDate(String date) {this.date = date;}

    // Записать описание
    public void setDescription(String description) {this.description = description;}

    // Получить описание чека
    public String getName()  {return name;}

    // Получить доход или расход
    public float getExpense_revenue() {return expense_revenue;}

    // Получить доход или расход
    public String getStringExpense_revenue() {return String.valueOf(expense_revenue);}

    // Получить дату
    public String getDate() {return date;}

    // Получить дату
    public String getDescription() {return description;}

    // Получить сокращеную перевернутую дату в виде строки
    public String getStringDate()
    {
        StringBuilder sss = new StringBuilder();
        sss.append(date.charAt(6));
        sss.append(date.charAt(7));
        sss.append(date.charAt(8));
        sss.append(date.charAt(9));
        sss.append(date.charAt(3));
        sss.append(date.charAt(4));
        sss.append(date.charAt(0));
        sss.append(date.charAt(1));
        return sss.toString();
    }
}
