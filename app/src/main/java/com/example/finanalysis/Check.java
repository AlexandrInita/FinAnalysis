package com.example.finanalysis;

public class Check {

    public String name;
    public float expense_revenue;
    public String date;

    public Check() {
        this.name = "";
        this.expense_revenue = 0;
        this.date = "";
    }

    public Check(String name, float expense_revenue, String date) {
        this.name = name;
        this.expense_revenue = expense_revenue;
        this.date = date;
    }

    // Записать чек
    public void setCheck(String name, float expense_revenue, String date) {
        this.name = name;
        this.expense_revenue = expense_revenue;
        this.date = date;
    }

    // Записать чек
    public void setCheck(Check check) {
        this.setCheck(check.name,check.expense_revenue,check.date);
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

    // Получить описание чека
    public String getName()  {return name;}

    // Получить доход или расход
    public float getExpense_revenue() {return expense_revenue;}

    // Получить доход или расход
    public String getStringExpense_revenue() {return String.valueOf(expense_revenue);}

    // Получить дату
    public String getDate() {return date;}

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
