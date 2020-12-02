package ru.samsung.itschool.book.cells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.GridLayout;


import task.Stub;
import task.Task;

public class CellsActivity extends Activity{
    private final Context context = this;
    private int WIDTH = 10;
    private int HEIGHT = 10;
    private int countShipPlace = 9; // 1 - 4x, 2 - 3x, 3 - 2x, 3 - 1x
    private String phase; // build, yourTurn, botTurn
    private String direction = "hor";

    private Button[][] cellsEnemy;
    private Button[][] cells;
    private Button[] indexHorizontal = new Button[11];
    private Button[] indexVertical = new Button[11];
    private Button[] indexHorizontalEnemy = new Button[11];
    private Button[] indexVerticalEnemy = new Button[11];
    private Button[][] menu;

    // todo: Создать кнопку для поворота кораблей при расстановке
    // TODO: 01.12.2020 Интерфейс говно - надо доработатть
    // todo: Дописать методы класса
    protected class GameCell {
        public int row; // строчка
        public int col; // колонка
        public boolean opened; // открыта ли ячейка
        public boolean isFired; // стреляли
        public boolean isShip; // стоит ли корабль
        public boolean isNear; // стоит ли корабль рядом
        public boolean isClickable; // можно ли нажать
        public int shipSize; // размер корабля
        public int textureNumber; // todo: текстуры?

        void GameCell(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private GameCell[][] playerField = new GameCell[HEIGHT][WIDTH]; // поле игрока
    private GameCell[][] enemyField = new GameCell[HEIGHT][WIDTH]; // поле противника

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cells);
        makeCells();
        generate();
    }

    void generate() {
        phase = "build";
    }

    /*
    onCreate() -> makeCells()
    onCreate() -> generate()
    generate() -> create_enemy_field()
    create_enemy_field() -> get_free_cells()
    create_enemy_field() -> can_place()
    create_enemy_field() -> create_ship()
    generate() -> close_cells_for_building()

    phase: build
    click on_player_field -> create_ship()
    create_ship -> close_cells_for_building()
    click on_rotate(button) -> redraw_ship()
    click on_rotate(button) -> close_cells_for_building()
    create_ship() ->  check_end_of_build()
    click on_rotate(button) -> redraw_field()
    create_ship() -> redraw()

    phase: player
    click on_enemy_field -> player_shot() (при условии правильности)
    click on_enemy_field -> redraw_field()
    player_shot -> check_win()
    player_shot() -> choose_cells_for_shoot() (при условии промаха)
    player_shot() -> phase="enemy"

    phase: enemy
    choose_cell_for_shoot() -> enemy_shot()
    enemy_shot() -> redraw_field()
    enemy_shot() -> check_win()
    enemy_shot() -> phase="player" (при условии промаха)
    * */

    void show_field() {
        /*Функция идёт по массиву данных и отрисовывает их на кнопках*/
        /* Нарисовать открытые поля с текстурой(?) выделить подбитые, потопленные
        * Как выделять - придумать*/
    }

    void create_enemy_field() {
        /*Создает поле противника*/
    }

    void create_ship(GameCell[][] field, int row, int col, int size) {
        /* Функция создаёт корабль в нужных координатах и поле
        (row,col) - верхний левый угол корабля
        direction: "hor", "ver" - читается из параметра
        * работает и для игрока, и для компьютера
        После каждой постановки корабля вы получаете свободные ячейки,
        в какую-то из них надо попытать поставить корабль*/
    }

    boolean can_place(GameCell[][] field, int row, int col, int size, String direction) {
        /*Возращает, можно ли поставить корабль*/
        return true;
    }

    int[][] get_free_cells(GameCell[][] field) {
        /*Возвращает индексы ячеек у которых рядом нет кораблей первая первая размерность - ячейки
        * ячейка 1: row1, col1
        * ячейка 2: row2, col2
        * ...*/
        return new int[0][0];
    }

    void close_cells_for_building(GameCell[][] field, int size, String direction) {
        /*Закрывает для нажатия определённые кнопки при расстановке корабля с определённым и
        размером и направлением*/
    }

    void redraw_ship() {
        /*При повороте корабля он пропадает в поле и перерисовывается в интерфейсе*/
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                menu[i][j].setBackgroundColor(Color.WHITE);
                if (countShipPlace < 9) {
                    if ((i == 0 && j == 0) || (i == 3 && j == 3)) continue;
                }
                if (countShipPlace < 7) {
                    if ((i == 1 && j == 0) || (i == 3 && j == 2)) continue;
                }
                if (countShipPlace < 4) {
                    if ((i == 2 && j == 0) || (i == 3 && j == 1)) continue;
                }
                if (countShipPlace < 1) {
                    break;
                }
                if (direction == "hor") {
                    if (j == 0) {
                        menu[i][j].setBackgroundColor(Color.RED);
                    }
                } else if (direction == "ver") {
                    if (i == 3) {
                        menu[i][j].setBackgroundColor(Color.RED);
                    }
                }
            }
        }
    }

    boolean check_end_of_build() {
        /*Вызывается после постановки каждого корабля*/
        /*После постановки последнего корабля*/
        return true;
    }

    void player_shot(int row, int col) {
        /*Обработка выстрела игрока по полю противника
        * Информационное составляющее поля противника должно быть изменено
        * При попадании не менять ход*/
    }

    void check_win() {
        /*Проверяется выигрыш одного из игроков*/
    }

    void choose_cell_for_shoot() {
        /*Выбор ячейки для хода противника*/
    }

    void enemy_shot() {
        /*Обработка выстрела противника по полю игрока
        * Информация меняется
        * При попадании не менять ход*/
    }

    boolean is_exist(int x1, int y1) {
        // Проверяет ячейку на существование
        return (x1 >= 0 && x1 <= 9 && y1 >= 0 && y1 <= 9);
    }

    boolean is_near(int x1, int y1, int x2, int y2) {
        // Проверяет ячейку на нахождение рядом по вертикали или горизонтали
        return ((x1 == x2 && y1 != y2) || (x1 != x2 && y1 == y2));
    }

    protected int getCol(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[1]) ;
    }

    protected int getRow(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[0]);
    }

    void makeCells() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menu = new Button[4][4];
        GridLayout cellsMenu = (GridLayout) findViewById(R.id.MenuLayout);
        cellsMenu.removeAllViews();
        cellsMenu.setColumnCount(4);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                menu[i][j] = (Button) inflater.inflate(R.layout.cell, cellsMenu, false);
                if (i != 3 && j != 0) {
                    menu[i][j].setText("R");
                    OnClickListener clickListenerForReverse = new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (phase == "build") {
                                direction = (direction == "hor" ? "ver" : "hor");
                                //вставить функцию изменения
                            }
                            else {
                                Stub.show(context,"Сейчас не фаза подготовки");
                            }
                            redraw_ship();
                        }
                    };
                    menu[i][j].setOnClickListener(clickListenerForReverse);
                }
                menu[i][j].setTag(i + "," + j);
                cellsMenu.addView(menu[i][j]);
            }
        }
        redraw_ship();

        cells = new Button[HEIGHT][WIDTH];
        cellsEnemy = new Button[HEIGHT][WIDTH];
        GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        GridLayout cellsLayoutEnemy = (GridLayout) findViewById(R.id.CellsLayoutEnemy);
        cellsLayout.removeAllViews();
        cellsLayoutEnemy.removeAllViews();
        cellsLayout.setColumnCount(WIDTH+1);
        cellsLayoutEnemy.setColumnCount(WIDTH+1);
        for (int i = -1; i < HEIGHT; i++)
            for (int j = -1; j < WIDTH; j++) {
                if (i == -1) {
                    indexHorizontal[j + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexHorizontal[j+1].setText(Integer.toString(j+1));
                    indexHorizontal[j+1].setTag(i + "," + j);
                    cellsLayout.addView(indexHorizontal[j + 1]);
                    indexHorizontalEnemy[j + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexHorizontalEnemy[j+1].setText(Integer.toString(j+1));
                    indexHorizontalEnemy[j+1].setTag(i + "," + j);
                    cellsLayoutEnemy.addView(indexHorizontalEnemy[j + 1]);
                    continue;
                }
                if (j == -1 && i > -1) {
                    indexVertical[i + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexVertical[i+1].setText(Integer.toString(i+1));
                    indexVertical[i+1].setTag(i + "," + j);
                    cellsLayout.addView(indexVertical[i + 1]);
                    indexVerticalEnemy[i + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexVerticalEnemy[i+1].setText(Integer.toString(i+1));
                    indexVerticalEnemy[i+1].setTag(i + "," + j);
                    cellsLayoutEnemy.addView(indexVerticalEnemy[i + 1]);
                    continue;
                }
                cells[i][j] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                //------обрабочик нажатий во время рассновки корбалей
                OnClickListener clickListenerForPlacing = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (phase == "build") {
                            Stub.show(context,"inside onClick()");
                            int tappedRow = getRow(view);
                            int tappedCol = getCol(view);
                        }
                        else {
                            Stub.show(context,"Сейчас не фаза подготовки");
                        }
                        show_field();
                    }
                };
                //---------------------------------------------------
                cells[i][j].setOnClickListener(clickListenerForPlacing);
                cells[i][j].setTag(i + "," + j);
                cellsLayout.addView(cells[i][j]);
                LayoutInflater inflaterEnemy = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //------обрабочик нажатий во время хода игрока
                OnClickListener clickListenerForYourTurn = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (phase == "yourTurn") {
                            Stub.show(context,"inside onClick()");
                            int tappedRow = getRow(view);
                            int tappedCol = getCol(view);
                            // TODO: 01.12.2020 обработка нажатия на уже обстрелянную клетку
                        }
                        else {
                            // ?
                            Stub.show(context,"сейчас не ваш ход");
                        }
                        show_field();
                    }
                };
                // TODO: 01.12.2020 добавить обработчики для кнопки поворота, и для кнопки постановки
                cellsEnemy[i][j] = (Button) inflaterEnemy.inflate(R.layout.cell, cellsLayoutEnemy, false);
                cellsEnemy[i][j].setOnClickListener(clickListenerForYourTurn);
                cellsEnemy[i][j].setTag(i + "," + j);
                cellsLayoutEnemy.addView(cellsEnemy[i][j]);
            }
    }

    class MyTimer extends CountDownTimer
    {
        MyTimer()
        {
            super(100000, 100);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            // TODO: 01.12.2020 Анимация хода противника делать в последнюю очередь
        }
        @Override
        public void onFinish() {
        }
    }
}