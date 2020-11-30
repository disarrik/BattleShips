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


import java.io.FileReader;

import task.Stub;
import task.Task;

public class CellsActivity extends Activity implements OnClickListener,
        OnLongClickListener {

    private int WIDTH = 11;
    private int HEIGHT = 11;
    private int countShips;
    private int countDestructedShips;
    private int countDestructedShipsEnemy;
    private int length1;
    private int length2;
    private int length3;
    private int length4;

    private boolean enemyTurn;
    private boolean SoloGameMode;
    private boolean firstPlayerTurn;
    private boolean start;

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
        length1 = 3;
        length2 = 3;
        length3 = 2;
        length4 = 1;
        countShips = 4*length4 + 3*length3 + 2*length2 + length1; //1 - 4x палубная, 2 - 3х палубная, 3 - 2х палубные, 3 - 1 палубная
        countDestructedShips = 0;
        countDestructedShipsEnemy = 0;
        enemyTurn = false;
        SoloGameMode = true;
        firstPlayerTurn = true;
        start = true;
        placeShips(length4, length2, length3, length3);
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
                if(i != 0 && j != 0) {
                    cells[i][j].setOnClickListener(this);
                }
                else if (j == 0 && i != 0){
                    cells[i][j].setText(Integer.toString(i));
                }
                else if (i == 0 && j != 0) {
                    cells[i][j].setText(Integer.toString(j));
                }
                cells[i][j].setTag(i + "," + j);
                cellsLayout.addView(cells[i][j]);


                LayoutInflater inflaterEnemy = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cellsEnemy[i][j] = (Button) inflaterEnemy.inflate(R.layout.cell, cellsLayoutEnemy, false);
                if (!SoloGameMode && i != 0 && j != 0) {
                    cellsEnemy[i][j].setOnClickListener(this);
                }
                else if (j == 0 && i != 0){
                    cellsEnemy[i][j].setText(Integer.toString(i));
                }
                else if (i == 0 && j != 0) {
                    cellsEnemy[i][j].setText(Integer.toString(j));
                }
                cellsEnemy[i][j].setTag(i + "," + j);
                cellsLayoutEnemy.addView(cellsEnemy[i][j]);
            }
    }
}