package ru.samsung.itschool.book.cells;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.GridLayout;


import task.Stub;
import task.Task;

public class CellsActivity extends Activity implements OnClickListener,
        OnLongClickListener {

    private int WIDTH = 11;
    private int HEIGHT = 11;
    int countShips;
    int countDestructedShips;
    int countDestructedShipsEnemy;

    private boolean enemyTurn;
    private boolean SoloGameMode;
    private boolean firstPlayerTurn;

    private boolean[][] status;
    private boolean[][] statusEnemy;
    private boolean[][] openStatus;
    private boolean[][] openStatusEnemy;
    private Button[][] cellsEnemy;
    private Button[][] cells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cells);
        makeCells();

        generate();

    }

    void generate() {

        //Эту строку нужно удалить
        Task.showMessage(this, "Добавьте код в функцию активности generate() для генерации клеточного поля");


        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                //ADD YOUR CODE HERE
                //....

            }
    }

    @Override
    public boolean onLongClick(View v) {
        //Эту строку нужно удалить
        Stub.show(this, "Добавьте код в функцию активности onLongClick() - реакцию на долгое нажатие на клетку");
        return false;
    }

    @Override
    public void onClick(View v) {
        //Эту строку нужно удалить
        Stub.show(this, "Добавьте код в функцию активности onClick() - реакцию на нажатие на клетку");

        Button tappedCell = (Button) v;

        //Получаем координтаты нажатой клетки
        int tappedX = getX(tappedCell);
        int tappedY = getY(tappedCell);
        //ADD YOUR CODE HERE
        //....

    }

    protected int getX(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[1]);
    }

    protected int getY(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[0]);
    }

    protected void placeShips(int length4, int length3, int length2, int length1) {
        if (SoloGameMode) {
            for (int i = 0; i < length4; i++) placeShip(4);
            for (int i = 0; i < length3; i++) placeShip(3);
            for (int i = 0; i < length2; i++) placeShip(2);
            for (int i = 0; i < length1; i++) placeShip(1);
        }
        else {
            for (int i = 0; i < length4; i++) placeShip(4, 1);
            for (int i = 0; i < length3; i++) placeShip(3, 1);
            for (int i = 0; i < length2; i++) placeShip(2, 1);
            for (int i = 0; i < length1; i++) placeShip(1, 1);

            for (int i = 0; i < length4; i++) placeShip(4, 2);
            for (int i = 0; i < length3; i++) placeShip(3, 2);
            for (int i = 0; i < length2; i++) placeShip(2, 2);
            for (int i = 0; i < length1; i++) placeShip(1, 2);
        }
    }

    protected void placeShip(int length) {}

    protected void placeShip(int length, int player) {}

    protected boolean checkWin() { return false;}

    protected void botTurn() {}

    protected boolean playerTurn() { return false;}

    protected boolean playerTurn(int player) { return false;}//для мультиплеера

    protected void changeGameMode() {}

    protected void refresh() {}

    protected void changePlayer() {}

    void makeCells() {
        cells = new Button[HEIGHT][WIDTH];
        cellsEnemy = new Button[HEIGHT][WIDTH];
        GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        GridLayout cellsLayoutEnemy = (GridLayout) findViewById(R.id.CellsLayoutEnemy);
        cellsLayout.removeAllViews();
        cellsLayoutEnemy.removeAllViews();
        cellsLayout.setColumnCount(WIDTH);
        cellsLayoutEnemy.setColumnCount(WIDTH);
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cells[i][j] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                cells[i][j].setOnClickListener(this);
                cells[i][j].setOnLongClickListener(this);
                cells[i][j].setTag(i + "," + j);
                cellsLayout.addView(cells[i][j]);

                LayoutInflater inflaterEnemy = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cellsEnemy[i][j] = (Button) inflaterEnemy.inflate(R.layout.cell, cellsLayoutEnemy, false);
                cellsEnemy[i][j].setOnClickListener(this);
                cellsEnemy[i][j].setOnLongClickListener(this);
                cellsEnemy[i][j].setTag(i + "," + j);
                cellsLayoutEnemy.addView(cellsEnemy[i][j]);
            }
    }
}