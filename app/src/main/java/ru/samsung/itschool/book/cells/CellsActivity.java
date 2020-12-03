package ru.samsung.itschool.book.cells;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import task.Stub;
import task.Task;

public class CellsActivity extends Activity{
    private final Context context = this;
    private int WIDTH = 10;
    private int HEIGHT = 10;
    private int countShipPlace = 10; // 1 - 4x, 2 - 3x, 3 - 2x, 4 - 1x
    private String phase; // build, yourTurn, botTurn
    private String direction = "ver";
    private int count_ship_for_enemy=10;
    private long frame = 0;
    private long enemyStartFrame = -61;
    private long enemyFramePeriod = 30;
    private int playerShips = 10;
    private int enemyShips = 10;

    private Button[][] cellsEnemy;
    private Button[][] cells;
    private Button[] indexHorizontal = new Button[11];
    private Button[] indexVertical = new Button[11];
    private Button[] indexHorizontalEnemy = new Button[11];
    private Button[] indexVerticalEnemy = new Button[11];
    private Button[][] menu;

    // todo: разобраться с кнопкой
    // TODO: 01.12.2020 Интерфейс говно - надо доработатть
    protected class GameCell {
        public boolean opened = false; // открыта ли ячейка
        public boolean isShown = false;
        public boolean isFired = false; // стреляли
        public boolean isShip = false; // стоит ли корабль
        public boolean isNear = false; // стоит ли корабль рядом
        //public boolean isClickable=true; // можно ли нажать
        public int shipSize = 0; // размер корабля
        public int textureNumber; // todo: текстуры?
    }

    private GameCell[][] playerField;// поле игрока
    private GameCell[][] enemyField;// поле противника

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cells);
        makeCells();
        generate();
        MyTimer timer = new MyTimer();
        System.out.println(timer);
        timer.start();
        playerShips = 10;
        enemyShips = 10;
    }

    void generate() {
        System.out.println("AAAA");
        phase = "build";
        playerField = new GameCell[HEIGHT][WIDTH];
        enemyField = new GameCell[HEIGHT][WIDTH];
        for (int i=0; i<HEIGHT; i++){
            for (int j=0; j<WIDTH; j++){
                playerField[i][j]=new GameCell();
                enemyField[i][j]=new GameCell();
            }
        }
        create_enemy_field();
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
        Random random = new Random();
        String choose_direction_for_enemy=(random.nextInt(2)==1? "ver":"hor");
        int rand_row=random.nextInt(10);
        int rand_col=random.nextInt(10);
        int size_enemy_ship=0;
        for (int i=count_ship_for_enemy; i>0; i--){
            switch(i){
                case(10):
                    size_enemy_ship=4;
                    break;
                case(9):
                case(8):
                    size_enemy_ship=3;
                    break;
                case(7):
                case(6):
                case(5):
                    size_enemy_ship=2;
                    break;
                case(4):
                case(3):
                case(2):
                case(1):
                    size_enemy_ship=1;
                    break;
            }
            while (!can_place(enemyField, rand_row, rand_col, size_enemy_ship,choose_direction_for_enemy)){
                choose_direction_for_enemy=(random.nextInt(2)==1? "ver":"hor");
                rand_row=random.nextInt(10);
                rand_col=random.nextInt(10);
            }
            create_ship_enemy(rand_row, rand_col,size_enemy_ship, choose_direction_for_enemy);
        }
    }
    void neighbours(int row, int col, GameCell[][] field, Button[][] but_cell){//установка соседей
        for (int i=row-1; i<=row+1; i++){
            for (int j=col-1; j<=col+1; j++){
                if (is_exist(i,j) && !field[i][j].isShip){
                    field[i][j].isNear=true;
                    if (field==playerField){
                        but_cell[i][j].setBackgroundColor(Color.WHITE);
                    }
                }
            }
        }
    }
    void create_ship_enemy(int row, int col, int size, String direction_enemy_ship){
        if (direction_enemy_ship.equals("ver")){
            for (int i=row; i>row-size; i--){
                enemyField[i][col].isShip=true;
                enemyField[i][col].shipSize = size;
                //todo сделать отрисовку на поле
                neighbours(i,col,enemyField,  cellsEnemy);
            }
        } else if (direction_enemy_ship.equals("hor")) {
            for (int i = col; i < col + size; i++) {
                enemyField[row][i].isShip = true;
                enemyField[row][i].shipSize = size;
                neighbours(row, i, enemyField, cellsEnemy);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    void create_ship(int row, int col, int size) {
        if (can_place(playerField,row,col,size,direction)){
            if (direction.equals("ver")){
                for (int i=row; i>row-size; i--){
                    playerField[i][col].isShip=true;
                    playerField[i][col].shipSize = size;
                    if (size > 1 && i == row - size + 1)  cells[i][col].setBackgroundResource(R.drawable.ship_nose_vertical);
                    if (size >= 1 && (i > row - size + 1 && i < row)) cells[i][col].setBackgroundResource(R.drawable.ship_center_vertical);
                    if (size > 1 && i == row)  cells[i][col].setBackgroundResource(R.drawable.ship_back_vertical);
                    if (size == 1) cells[i][col].setBackgroundResource(R.drawable.small_ver);
                    //todo сделать отрисовку на поле
                    neighbours(i,col,playerField, cells);
                }
            } else if (direction.equals("hor")){
                for (int i=col; i<col+size; i++){
                    playerField[row][i].isShip=true;
                    playerField[row][i].shipSize = size;
                    if (size > 1 && i == col + size - 1)  cells[row][i].setBackgroundResource(R.drawable.ship_nose_horizontal);
                    if (size >= 1 && (i < col + size - 1 && i > col)) cells[row][i].setBackgroundResource(R.drawable.ship_center_horizontal);
                    if (size > 1 && i == col)  cells[row][i].setBackgroundResource(R.drawable.ship_back_horizontal);
                    if (size == 1) cells[row][i].setBackgroundResource(R.drawable.small_hor);
                    neighbours(row, i, playerField, cells);
                }
            }
            countShipPlace--;
            redraw_ship();
        }else{
            Stub.show(context,"Выберите другую клетку");
        }
        if (check_end_of_build()){
            //todo наложить текстуры
            // todo вывести фразу
            for (int i=0; i<HEIGHT; i++){
                for (int j=0; j<WIDTH; j++){
                    if (playerField[i][j].isNear && !playerField[i][j].isShip) {
                        cells[i][j].setBackgroundColor(Color.WHITE);
                        cells[i][j].setBackgroundColor(getColor(R.color.SEA));
                    }
                }
            }
            phase="yourTurn";
            Stub.show(context, "Пора начать сражение");
        }
        // todo надо перерисовать поле заново
    }

    boolean can_place(GameCell[][] field, int row, int col, int size, String dir) {
        /*Возращает, можно ли поставить корабль*/
        if (dir.equals("ver")){
            if (row-size<-1){
                return false;
            }
            boolean check=true;
            for (int i=row; i>row-size; i--){
                if (field[i][col].isShip || field[i][col].isNear){
                    check=false;
                }
            }
            return check;
        }else{
            if (col+size>WIDTH){
                return false;
            }
            boolean check=true;
            for (int i=col; i<col+size; i++){
                if (field[row][i].isShip || field[row][i].isNear){
                    check=false;
                }
            }
            return check;
        }
    }
    void redraw_ship() {
        /*При повороте корабля он пропадает в поле и перерисовывается в интерфейсе*/
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 3 || j == 0)menu[i][j].setBackgroundColor(Color.WHITE);
                if (countShipPlace < 10) {
                    if ((i == 0 && j == 0) || (i == 3 && j == 3)) continue;
                }
                if (countShipPlace < 8) {
                    if ((i == 1 && j == 0) || (i == 3 && j == 2)) continue;
                }
                if (countShipPlace < 5) {
                    if ((i == 2 && j == 0) || (i == 3 && j == 1)) continue;
                }
                if (countShipPlace < 1) {
                    break;
                }
                if (direction.equals("ver")) {
                    if (j == 0) {
                        menu[i][j].setBackgroundColor(Color.RED);
                    }
                } else if (direction.equals("hor")) {
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
        return countShipPlace == 0;
    }

    void player_shot(int row, int col) {
        if (!enemyField[row][col].isFired) {
            if (enemyField[row][col].isShip) {
                enemyField[row][col].isFired = true;
                enemyField[row][col].opened = true;
                cellsEnemy[row][col].setBackgroundResource(R.drawable.popadanie);
                rewrite_size_ship(enemyField, row, col);
                if (enemyField[row][col].shipSize == 0) {
                    Stub.show(context, "корабль потоплен");
                    enemyShips--;
                }
                // todo сделать проверку на потопление или подьитие
            }
            else {
                enemyField[row][col].isFired = true;
                enemyField[row][col].opened = true;
                phase = "botTurn";
                cellsEnemy[row][col].setBackgroundColor(Color.GRAY);
                enemyStartFrame = frame;
            }
            check_win(enemyField);
        }

        /*Обработка выстрела игрока по полю противника
        * Информационное составляющее поля противника должно быть изменено
        * При попадании не менять ход*/
    }

    void showDistructedShip(int row, int col) {
        cellsEnemy[row][col].setBackgroundResource(R.drawable.oblomki);
        for (int i = row - 1; i <= row+1; i++) {
            for (int j= col -1; j <=col+1; j++) {
                if (is_exist(i, j) && enemyField[i][j].isShip && !enemyField[i][j].isShown) {
                    enemyField[i][j].isShown = true;
                    cellsEnemy[i][j].setBackgroundResource(R.drawable.oblomki);
                    showDistructedShip(i, j);
                }
            }
        }
    }

    void rewrite_size_ship(GameCell[][] field, int row, int col) {
        if (field[row][col].isShip) {
            field[row][col].shipSize--;
            if (is_exist(row-1, col) && field[row-1][col].shipSize > field[row][col].shipSize) rewrite_size_ship(field, row - 1, col);
            if (is_exist(row, col + 1) && field[row][col+1].shipSize > field[row][col].shipSize) rewrite_size_ship(field, row, col +1);
            if (is_exist(row+1, col) && field[row+1][col].shipSize > field[row][col].shipSize) rewrite_size_ship(field, row + 1, col);
            if (is_exist(row, col - 1) && field[row][col-1].shipSize > field[row][col].shipSize) rewrite_size_ship(field, row , col -1);
        }
    }
    void check_win(GameCell[][] field) {
        /*Проверяется выигрыш одного из игроков*/
        if (enemyShips == 0) {
            Stub.show(context, "Вы выиграли");
            phase = "end";
        }
        else if (playerShips == 0) {
            Stub.show(context, "Вы проиграли");
            phase = "end";
        }

    }
    /** Начало бота*/
    void reset_n_1(int temp_i, int temp_j){
        for (int i=temp_i-1; i<=temp_i+1; i++){
            for (int j=temp_j-1; j<=temp_j+1; j++){
                if (is_exist(i,j)){
                    playerField[i][j].isFired=true;
                    if (playerField[i][j].shipSize>0 && i!=pretend_turn_i && j!=pretend_turn_j){
                        pretend_turn_i=i;
                        pretend_turn_j=j;
                    }
                }
            }
        }
    }
    void reset_neighbours(int size_died_ship){
        int temp_i;
        int temp_j;
        for (int k=size_died_ship; k>0; k--){
            temp_i=pretend_turn_i;
            temp_j=pretend_turn_j;
            boolean flag;
            for (int i=temp_i-1; i<=temp_i+1; i++){
                for (int j=temp_j-1; j<=temp_j+1; j++){
                    if (temp_i==i && temp_j==j)
                        flag=false;
                    else
                        flag=true;
                    if (is_exist(i,j)){
                        playerField[i][j].isFired=true;
                        if (playerField[i][j].isShip && flag){
                            pretend_turn_i=i;
                            pretend_turn_j=j;
                        }
                    }
                }
            }
        }
    }
    boolean is_exist(int x1, int y1) {
        // Проверяет ячейку на существование
        return (x1 >= 0 && x1 <= 9 && y1 >= 0 && y1 <= 9);
    }
    boolean is_near(int x1, int y1, int x2, int y2) {
        // Проверяет ячейку на нахождение рядом по вертикали или горизонтали
        return ((x1 == x2 && y1 != y2) || (x1 != x2 && y1 == y2));
    }
    void choose_pretend(int i, int j){
        if (is_exist(i,j) && is_near(i,j, turn_i, turn_j) && !playerField[i][j].isFired) {
            sum_pretend++;
            mas_for_choose_tap[sum_pretend] = (i) * 10 + j;//добавление в массив индексов элемента
        }
    }
    void sort_mas_for_choose(){
        for (int j=0; j<4; j++){
            for (int i=0; i<3; i++){
                if (mas_for_choose_tap[i]==-1){
                    int temp=mas_for_choose_tap[i+1];
                    mas_for_choose_tap[i+1]=-1;
                    mas_for_choose_tap[i]=temp;
                }
            }
        }
        sum_pretend=-1;
        for (int i=0; i<4; i++){
            if (mas_for_choose_tap[i]!=-1){
                sum_pretend++;
            }
        }
    }
    int [] mas_for_choose_tap=new int[4];
    int pretend_turn_i=0;
    int pretend_turn_j=0;
    int sum_pretend=-1;

    void first_hit_choose_pretend(){
        for (int i=turn_i-1; i<=turn_i+1; i++){
            for (int j=turn_j-1; j<=turn_j+1; j++){
                choose_pretend(i,j);
            }
        }
    }
    void delete_pretend(){
        if (pretend_turn_i==turn_i){
            for (int i=0; i<4; i++){
                if (mas_for_choose_tap[i]/10!=turn_i){
                    mas_for_choose_tap[i]=-1;
                }
            }
        }
        else{
            for (int i=0; i<4; i++){
                if (mas_for_choose_tap[i]%10!=turn_j){
                    mas_for_choose_tap[i]=-1;
                }
            }
        }
        sort_mas_for_choose();
    }
    void add_and_verify_pretend(){
        if (pretend_turn_i==turn_i) {
            if (pretend_turn_j > turn_j) {
                if (pretend_turn_j+1 < WIDTH)
                    if (!playerField[pretend_turn_i][pretend_turn_j + 1].isFired) {
                        sum_pretend++;
                        mas_for_choose_tap[sum_pretend] = pretend_turn_i * 10 + pretend_turn_j + 1;
                    }
            } else if (pretend_turn_j-1 >= 0) {
                if (!playerField[pretend_turn_i][pretend_turn_j - 1].isFired) {
                    sum_pretend++;
                    mas_for_choose_tap[sum_pretend] = pretend_turn_i * 10 + pretend_turn_j - 1;
                }
            }
        }else{
            if (pretend_turn_i>turn_i){
                if (pretend_turn_i+1<HEIGHT)
                    if (!playerField[pretend_turn_i+1][pretend_turn_j].isFired){
                        sum_pretend++;
                        mas_for_choose_tap[sum_pretend]=(pretend_turn_i+1)*10+pretend_turn_j;
                    }
            }
            else if (pretend_turn_i-1>=0){
                if (!playerField[pretend_turn_i-1][pretend_turn_j].isFired){
                    sum_pretend++;
                    mas_for_choose_tap[sum_pretend]=(pretend_turn_i-1)*10+pretend_turn_j;
                }
            }
        }
    }
    void choose_cell_for_shoot() {
        /*Выбор ячейки для хода противника*/
        Random random = new Random();
        sort_mas_for_choose();
        int rand_choose = random.nextInt(sum_pretend + 1);
        int temp_turn_i = mas_for_choose_tap[rand_choose] / 10;
        int temp_turn_j = mas_for_choose_tap[rand_choose] % 10;


        playerField[temp_turn_i][temp_turn_j].isFired = true;
        mas_for_choose_tap[rand_choose] = -1;
        cells[temp_turn_i][temp_turn_j].setBackgroundColor(Color.GRAY);
        if (playerField[temp_turn_i][temp_turn_j].isShip) {
            cells[temp_turn_i][temp_turn_j].setBackgroundColor(getColor(R.color.hit));
            pretend_turn_i = temp_turn_i;
            pretend_turn_j = temp_turn_j;
            sum_hit++;
            if (sum_hit == playerField[temp_turn_i][temp_turn_j].shipSize) {
                reset_neighbours(sum_hit); //функция, которая делает всех соседей обстрелянными
                hit = false;
                playerShips--;
                //todo анимация потопления корабля
            } else {
                if (sum_hit == 2)
                    delete_pretend();
                add_and_verify_pretend();

                //todo отрисовка анимации попадания
            }
            enemyStartFrame = frame;
        } else {
            phase = "yourTurn";
        }
    }
    int sum_suitable_cell=-1, turn_i, turn_j, sum_hit=0;
    boolean hit=false;
    void generate_mas_for_bot_turn(int []mas_check_for_bot){ //создает массив из доступных для удара ячеек
        sum_suitable_cell=-1;
        for (int i=0; i<HEIGHT; i++){
            for (int j=0; j<WIDTH; j++){
                if (!playerField[i][j].isFired){
                    sum_suitable_cell++;
                    mas_check_for_bot[sum_suitable_cell]=i*10+j;
                }
            }
        }
    }
    void reset_mas(){
        for (int i=0; i<4; i++){
            mas_for_choose_tap[i]=-1;
        }
    }
    void enemy_shot() {
        //todo задержка?
        check_win(playerField);
        if (hit) {
            choose_cell_for_shoot();
        } else{
            int [] mas_check_for_bot=new int[100];
            generate_mas_for_bot_turn(mas_check_for_bot);
            Random random = new Random();
            if (sum_suitable_cell<0){
                Stub.show(context, "suitable"+Integer.toString(sum_suitable_cell));
            } else{
                int k = random.nextInt(sum_suitable_cell+1);
                int temp_turn_i=mas_check_for_bot[k]/10;
                int temp_turn_j=mas_check_for_bot[k]%10;
                playerField[temp_turn_i][temp_turn_j].isFired=true;
                cells[temp_turn_i][temp_turn_j].setBackgroundColor(Color.GRAY);
                if (playerField[temp_turn_i][temp_turn_j].isShip){
                    cells[temp_turn_i][temp_turn_j].setBackgroundColor(getColor(R.color.hit));
                    if (playerField[temp_turn_i][temp_turn_j].shipSize == 1){
                        reset_n_1(temp_turn_i, temp_turn_j);
                        hit=false;
                        playerShips--;
                    } else {
                        hit=true;
                        turn_i=temp_turn_i;
                        turn_j=temp_turn_j;
                        sum_hit=1;
                        sum_pretend=-1;
                        reset_mas();
                        first_hit_choose_pretend();
                    }
                    enemyStartFrame = frame;
                }
                else{
                    phase = "yourTurn";
                }
            }
        }
    }
    /**Конец бота*/


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
                    if (i == 0 && j == 1) menu[i][j].setBackgroundResource(R.drawable.rotate_button_11);
                    if (i == 0 && j == 2) menu[i][j].setBackgroundResource(R.drawable.rotate_button_12);
                    if (i == 0 && j == 3) menu[i][j].setBackgroundResource(R.drawable.rotate_button_13);
                    if (i == 1 && j == 1) menu[i][j].setBackgroundResource(R.drawable.rotate_button_21);
                    if (i == 1 && j == 2) menu[i][j].setBackgroundResource(R.drawable.rotate_button_22);
                    if (i == 1 && j == 3) menu[i][j].setBackgroundResource(R.drawable.rotate_button_23);
                    if (i == 2 && j == 1) menu[i][j].setBackgroundResource(R.drawable.rotate_button_31);
                    if (i == 2 && j == 2) menu[i][j].setBackgroundResource(R.drawable.rotate_button_32);
                    if (i == 2 && j == 3) menu[i][j].setBackgroundResource(R.drawable.rotate_button_33);
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
                    indexHorizontal[j+1].setTag(i + "," + j);
                    indexHorizontal[j+1].setBackgroundColor(Color.WHITE);
                    cellsLayout.addView(indexHorizontal[j + 1]);
                    indexHorizontalEnemy[j + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexHorizontalEnemy[j+1].setTag(i + "," + j);
                    indexHorizontalEnemy[j+1].setBackgroundColor(Color.WHITE);
                    cellsLayoutEnemy.addView(indexHorizontalEnemy[j + 1]);
                    continue;
                }
                if (j == -1 && i > -1) {
                    indexVertical[i + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexVertical[i+1].setTag(i + "," + j);
                    indexVertical[i+1].setBackgroundColor(Color.WHITE);
                    cellsLayout.addView(indexVertical[i + 1]);
                    indexVerticalEnemy[i + 1] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                    indexVerticalEnemy[i+1].setTag(i + "," + j);
                    indexVerticalEnemy[i+1].setBackgroundColor(Color.WHITE);
                    cellsLayoutEnemy.addView(indexVerticalEnemy[i + 1]);
                    continue;
                }
                cells[i][j] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                //------обрабочик нажатий во время рассновки корбалей
                OnClickListener clickListenerForPlacing = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (phase.equals("build")) {
                            int tappedRow = getRow(view);
                            int tappedCol = getCol(view);
                            int size_ship=0;
                            switch(countShipPlace){
                                case(10):
                                    size_ship=4;
                                    break;
                                case(9):
                                case(8):
                                    size_ship=3;
                                    break;
                                case(7):
                                case(6):
                                case(5):
                                    size_ship=2;
                                    break;
                                case(4):
                                case(3):
                                case(2):
                                case(1):
                                    size_ship=1;
                                    break;
                            }
                            create_ship(tappedRow,tappedCol,size_ship);
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
                        if (phase.equals("yourTurn")) {
                            //Stub.show(context,"inside onClick()");
                            int tappedRow = getRow(view);
                            int tappedCol = getCol(view);
                            player_shot(tappedRow, tappedCol);
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

        indexHorizontal[1].setBackgroundResource(R.drawable.playera);
        indexHorizontal[2].setBackgroundResource(R.drawable.playerb);
        indexHorizontal[3].setBackgroundResource(R.drawable.playerc);
        indexHorizontal[4].setBackgroundResource(R.drawable.playerd);
        indexHorizontal[5].setBackgroundResource(R.drawable.playere);
        indexHorizontal[6].setBackgroundResource(R.drawable.playerf);
        indexHorizontal[7].setBackgroundResource(R.drawable.playerg);
        indexHorizontal[8].setBackgroundResource(R.drawable.playerh);
        indexHorizontal[9].setBackgroundResource(R.drawable.playeri);
        indexHorizontal[10].setBackgroundResource(R.drawable.playerj);

        indexVertical[1].setBackgroundResource(R.drawable.player1);
        indexVertical[2].setBackgroundResource(R.drawable.player2);
        indexVertical[3].setBackgroundResource(R.drawable.player3);
        indexVertical[4].setBackgroundResource(R.drawable.player4);
        indexVertical[5].setBackgroundResource(R.drawable.player5);
        indexVertical[6].setBackgroundResource(R.drawable.player6);
        indexVertical[7].setBackgroundResource(R.drawable.player7);
        indexVertical[8].setBackgroundResource(R.drawable.player8);
        indexVertical[9].setBackgroundResource(R.drawable.player9);
        indexVertical[10].setBackgroundResource(R.drawable.player10);

        indexHorizontalEnemy[1].setBackgroundResource(R.drawable.enemya);
        indexHorizontalEnemy[2].setBackgroundResource(R.drawable.enemyb);
        indexHorizontalEnemy[3].setBackgroundResource(R.drawable.enemyc);
        indexHorizontalEnemy[4].setBackgroundResource(R.drawable.enemyd);
        indexHorizontalEnemy[5].setBackgroundResource(R.drawable.enemye);
        indexHorizontalEnemy[6].setBackgroundResource(R.drawable.enemyf);
        indexHorizontalEnemy[7].setBackgroundResource(R.drawable.enemyg);
        indexHorizontalEnemy[8].setBackgroundResource(R.drawable.enemyh);
        indexHorizontalEnemy[9].setBackgroundResource(R.drawable.enemyi);
        indexHorizontalEnemy[10].setBackgroundResource(R.drawable.enemyj);

        indexVerticalEnemy[1].setBackgroundResource(R.drawable.enemy1);
        indexVerticalEnemy[2].setBackgroundResource(R.drawable.enemy2);
        indexVerticalEnemy[3].setBackgroundResource(R.drawable.enemy3);
        indexVerticalEnemy[4].setBackgroundResource(R.drawable.enemy4);
        indexVerticalEnemy[5].setBackgroundResource(R.drawable.enemy5);
        indexVerticalEnemy[6].setBackgroundResource(R.drawable.enemy6);
        indexVerticalEnemy[7].setBackgroundResource(R.drawable.enemy7);
        indexVerticalEnemy[8].setBackgroundResource(R.drawable.enemy8);
        indexVerticalEnemy[9].setBackgroundResource(R.drawable.enemy9);
        indexVerticalEnemy[10].setBackgroundResource(R.drawable.enemy10);
    }

    void update() {
        frame++;
        System.out.println(frame);
        if (frame == enemyStartFrame + enemyFramePeriod) {
            enemy_shot();
        }
    }

    class MyTimer extends CountDownTimer
    {
        MyTimer()
        {
            super(1000000000, 20);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            // TODO: 01.12.2020 Анимация хода противника делать в последнюю очередь
            update();
        }
        @Override
        public void onFinish() {
        }
    }
}