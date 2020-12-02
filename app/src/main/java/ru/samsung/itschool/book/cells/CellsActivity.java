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


import java.util.Random;

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
//        public int row; // строчка
//        public int col; // колонка
        public boolean opened=false; // открыта ли ячейка
        public boolean isFired=false; // стреляли
        public boolean isShip=false; // стоит ли корабль
        public boolean isNear=false; // стоит ли корабль рядом
        //public boolean isClickable=true; // можно ли нажать
        public int shipSize=0; // размер корабля
        public int textureNumber; // todo: текстуры?
//        void GameCell(int row, int col) {
//            this.row = row;
//            this.col = col;
//        }
    }

    private GameCell[][] playerField;// поле игрока //todo зачем это дублируется в свойствах класса для row и col
    private GameCell[][] enemyField;// поле противника

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cells);
        makeCells();
        generate();
    }

    void generate() {
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

        // рандомно выбирать вертикальное или горизонатльно направление
        //затем рандомно клетку до тех пока она не разрешить постановку
        //count_ship_for_enemy--;
        //вызывать neighbours;
    }
    void neighbours(int row, int col, GameCell[][] field, Button[][] but_cell){//установка соседей
        for (int i=row-1; i<=row+1; i++){
            for (int j=col-1; j<=col+1; j++){
                if (is_exist(i,j) && !field[i][j].isShip){
                    field[i][j].isNear=true;
                    but_cell[i][j].setBackgroundColor(Color.GREEN);
                }
            }
        }
    }
    void create_ship_enemy(int row, int col, int size, String direction_enemy_ship){
        if (direction_enemy_ship.equals("ver")){
            for (int i=row; i>row-size; i--){
                enemyField[i][col].isShip=true;
                enemyField[i][col].shipSize = size;
                cellsEnemy[i][col].setText(Integer.toString(enemyField[i][col].shipSize));
                cellsEnemy[i][col].setBackgroundColor(Color.RED);
                //todo сделать отрисовку на поле
                neighbours(i,col,enemyField,  cellsEnemy);
            }
        } else if (direction_enemy_ship.equals("hor")) {
            for (int i = col; i < col + size; i++) {
                enemyField[row][i].isShip = true;
                enemyField[row][i].shipSize = size;
                cellsEnemy[row][i].setBackgroundColor(Color.RED);
                cellsEnemy[row][i].setText(Integer.toString(enemyField[row][i].shipSize));
                neighbours(row, i, enemyField, cellsEnemy);
            }
        }
    }
    void create_ship(int row, int col, int size) {
        if (can_place(playerField,row,col,size,direction)){
            if (direction.equals("ver")){
                for (int i=row; i>row-size; i--){
                    playerField[i][col].isShip=true;
                    playerField[i][col].shipSize = size;
                    cells[i][col].setBackgroundColor(Color.RED);
                    //todo сделать отрисовку на поле
                    neighbours(i,col,playerField, cells);
                }
            } else if (direction.equals("hor")){
                for (int i=col; i<col+size; i++){
                    playerField[row][i].isShip=true;
                    playerField[row][i].shipSize = size;
                    cells[row][i].setBackgroundColor(Color.RED);
                    neighbours(row, i, playerField, cells);
                }
            }
            countShipPlace--;
            redraw_ship();
        }else{
            Stub.show(context,"Выберите другую клетку");
        }
        if (check_end_of_build()){
            //todo сделать перерисовку поля
            //todo убрать зеленые клетки, наложить текстуры
            // todo вывести фразу
            phase="yourTurn";
            Stub.show(context, "Редим игры переключен");
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
//    int[][] get_free_cells(GameCell[][] field) {
//        /*Возвращает индексы ячеек у которых рядом нет кораблей первая первая размерность - ячейки
//        * ячейка 1: row1, col1
//        * ячейка 2: row2, col2
//        * ...*/
//        return new int[0][0];
//    }
//
//    void close_cells_for_building(GameCell[][] field, int size, String direction) {
//        /*Закрывает для нажатия определённые кнопки при расстановке корабля с определённым и
//        размером и направлением*/
//    }
    void redraw_ship() {
        /*При повороте корабля он пропадает в поле и перерисовывается в интерфейсе*/
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                menu[i][j].setBackgroundColor(Color.WHITE);
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
                cellsEnemy[row][col].setBackgroundColor(Color.BLACK);
                rewrite_size_ship(enemyField, row, col);
                if (enemyField[row][col].shipSize == 0) Stub.show(context, "корабль потоплен");
                // todo сделать проверку на потопление или подьитие
            }
            else {
                enemyField[row][col].isFired = true;
                enemyField[row][col].opened = true;
                phase = "botTurn";
                cellsEnemy[row][col].setBackgroundColor(Color.GRAY);
                enemy_shot();
            }
        }

        /*Обработка выстрела игрока по полю противника
        * Информационное составляющее поля противника должно быть изменено
        * При попадании не менять ход*/
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

    void check_win() {
        /*Проверяется выигрыш одного из игроков*/
    }
    /** Начало бота*/
    boolean is_exist(int x1, int y1) {
        // Проверяет ячейку на существование
        return (x1 >= 0 && x1 <= 9 && y1 >= 0 && y1 <= 9);
    }
    boolean is_near(int x1, int y1, int x2, int y2) {
        // Проверяет ячейку на нахождение рядом по вертикали или горизонтали
        return ((x1 == x2 && y1 != y2) || (x1 != x2 && y1 == y2));
    }
    void choose_pretend(int i, int j){
        if (is_exist(i,j) && is_near(i,j, turn_i, turn_j)) {
            if (!playerField[i][j].isFired) {
                mas_for_choose_tap[sum_pretend] = (i) * 10 + j;//добавление в массив индексов элемента
                sum_pretend++;
            }
        }
    }
    void sort_mas_for_choose(){
        for (int j=0; j<3; j++){
            for (int i=0; i<3; i++){
                if (mas_for_choose_tap[i]==0){
                    int temp=mas_for_choose_tap[i+1];
                    mas_for_choose_tap[i+1]=0;
                    mas_for_choose_tap[i]=temp;
                }
            }
        }
    }
    int [] mas_for_choose_tap=new int[4];
    int pretend_turn_i=0;
    int pretend_turn_j=0;
    int sum_pretend=0;
    void first_hit_choose_pretend(){
        for (int i=turn_i-1; i<=turn_i+1; i++){
            for (int j=turn_j-1; j<=turn_j+1; j++){
                choose_pretend(i,j);
            }
        }
    }
    void delete_pretend(){
        if (pretend_turn_i==turn_i){
            for (int i=0; i<=4; i++){
                if (mas_for_choose_tap[i]/10!=turn_i){
                    mas_for_choose_tap[i]=0;
                }
            }
        }
        else{
            for (int i=0; i<=4; i++){
                if (mas_for_choose_tap[i]%10!=turn_j){
                    mas_for_choose_tap[i]=0;
                }
            }
        }
        sort_mas_for_choose();
        sum_pretend=0;
        for (int i=0; i<4; i++){
            if (mas_for_choose_tap[i]!=0){ //требует доработки, так как тут максимум может быть 1 претендент
                sum_pretend++;
            }
        }
    }
    void add_and_verify_pretend(){
        if (pretend_turn_i==turn_i) {
            if (pretend_turn_j > turn_j) {
                if (pretend_turn_j < WIDTH - 1)
                    if (!playerField[pretend_turn_i][pretend_turn_j + 1].isFired) {
                        mas_for_choose_tap[sum_pretend + 1] = pretend_turn_i * 10 + pretend_turn_j + 1;
                        sum_pretend++;
                    }
            } else if (pretend_turn_j > 0) {
                if (!playerField[pretend_turn_i][pretend_turn_j - 1].isFired) {
                    mas_for_choose_tap[sum_pretend + 1] = pretend_turn_i * 10 + pretend_turn_j - 1;
                    sum_pretend++;
                }
            }
        }else{
        if (pretend_turn_i>turn_i){
            if (pretend_turn_i<HEIGHT-1)
                if (!playerField[pretend_turn_i+1][pretend_turn_j].isFired){
                    mas_for_choose_tap[sum_pretend+1]=(pretend_turn_i+1)*10+pretend_turn_j;
                    sum_pretend++;
                }
        }
        else if (pretend_turn_i>0){
            if (!playerField[pretend_turn_i-1][pretend_turn_j].isFired){
                mas_for_choose_tap[sum_pretend+1]=(pretend_turn_i-1)*10+pretend_turn_j-1;
                sum_pretend++;
            }
        }
    }

    }
    void choose_cell_for_shoot() {
        /*Выбор ячейки для хода противника*/
        Random random = new Random();
        int rand_choose=random.nextInt(sum_pretend);
        playerField[mas_for_choose_tap[rand_choose]/10][mas_for_choose_tap[rand_choose]%10].isFired=true;
        mas_for_choose_tap[rand_choose]=0;
        sum_pretend--;
        sort_mas_for_choose();
        if (playerField[mas_for_choose_tap[rand_choose]/10][mas_for_choose_tap[rand_choose]%10].isShip){
            sum_hit++;
            pretend_turn_i=mas_for_choose_tap[rand_choose]/10;
            pretend_turn_j=mas_for_choose_tap[rand_choose]%10;
            if (sum_hit==2)
                delete_pretend();
            add_and_verify_pretend();
            if (sum_hit==playerField[mas_for_choose_tap[rand_choose]/10][mas_for_choose_tap[rand_choose]%10].shipSize){
                hit=false;
                //дописать обнуление необходимых параметров
                //пустой ли массив для выбора хода бота к этому моменту?
                //todo анимация потопления корабля
            }
            enemy_shot();//это сработает только если было попадание
            //todo отрисовка анимации попадания
        }
    }
    int sum_suitable_cell=0, turn_i, turn_j, sum_hit=0;
    boolean hit=false;
    int [] generate_mas_for_bot_turn(){ //создает массив из доступных для удара ячеек
        int [] mas_check_for_bot=new int [100];
        for (int i=0; i<HEIGHT; i++){
            for (int j=0; j<WIDTH; j++){
                if (playerField[i][j].isFired){
                    mas_check_for_bot[sum_suitable_cell]=i*10+j;
                    sum_suitable_cell++;
                }
            }
        }
        return mas_check_for_bot;
    }
    void enemy_shot() {
        /*Обработка выстрела противника по полю игрока
        * Информация меняется
        * При попадании не менять ход*/
        //todo задержка?
        if (hit) {
            choose_cell_for_shoot();
        } else{
            int [] mas_check_for_bot=generate_mas_for_bot_turn();
            Random random = new Random();
            int k = random.nextInt(sum_suitable_cell);
            int temp_turn_i=mas_check_for_bot[k]/10;
            int temp_turn_j=mas_check_for_bot[k]%10;
            playerField[temp_turn_i][temp_turn_j].isFired=true;
            if (playerField[temp_turn_i][temp_turn_j].isShip){
                hit=true;
                turn_i=temp_turn_i;
                turn_j=temp_turn_j;
                sum_hit++;
                first_hit_choose_pretend();
                enemy_shot();
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