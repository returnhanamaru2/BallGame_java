#include <M5Stack.h>

//=== 定数・グローバル変数定義 ===//

// ボールの色一覧（レベルごとに変化）
int color[9] = {WHITE, PINK, GREENYELLOW, GREEN, CYAN, BLUE, YELLOW, ORANGE, RED};

// ランキングスコア（上位5件）
int rank[5];

// スコア管理
int S;

// ゲームパラメータ
double speed;     // ボール速度
double step;      // 音階の上昇率
int shift, save;  // パドル位置（現在・前回）
int size;         // パドルサイズ
int stop;         // ゲームオーバー判定用フラグ

// ボール位置と移動管理
int x, y;         // 現在位置
int m_x, m_y;     // 移動方向フラグ（0:正方向, 1:負方向）
int chk_x, chk_y; // 前回の描画位置（消去に使用）
int count;        // フレーム数などに使用可

// 現在のレベル（色で表現）
int color_s;

// モード選択用（0:ランキング, 1:ゲーム）
int option;


//=== 画面表示・UI系関数 ===//

// ホーム画面表示
void home() {
    M5.update();
    M5.Lcd.setCursor(64, 100);
    M5.Lcd.setTextSize(4);
    M5.Lcd.printf("BALL GAME");

    M5.Lcd.setCursor(10, 10);
    M5.Lcd.setTextSize(1);
    M5.Lcd.printf("@2024 TEAM_I MADE IN JAPAN");

    M5.Lcd.setCursor(50, 180);
    M5.Lcd.setTextSize(2);
    M5.Lcd.printf("A_button is ranking.");

    M5.Lcd.setCursor(50, 200);
    M5.Lcd.setTextSize(2);
    M5.Lcd.printf("C_button is start.");

    // 枠線表示
    M5.Lcd.fillRect(0, 0, 320, 5, GREEN);
    M5.Lcd.fillRect(0, 0, 5, 240, GREEN);
    M5.Lcd.fillRect(0, 235, 320, 5, GREEN);
    M5.Lcd.fillRect(315, 0, 5, 240, GREEN);
}

// ゲームプレイ時の枠（下部のみオレンジ）
void flame() {
    M5.Lcd.fillRect(0, 180, 320, 5, ORANGE);
    M5.Lcd.fillRect(0, 180, 5, 60, ORANGE);
    M5.Lcd.fillRect(0, 235, 320, 5, ORANGE);
    M5.Lcd.fillRect(315, 180, 5, 60, ORANGE);
    M5.Lcd.fillRect(230, 180, 5, 60, ORANGE);
}

// パドル操作
void bar_move(int shift, int size) {
    if (M5.BtnA.isPressed() && shift >= 0) shift -= 10;
    if (M5.BtnC.isPressed() && shift + size <= 320) shift += 10;
}


//=== メインゲームロジック ===//

void game() {
    M5.update();
    flame();

    // スコア・レベル表示
    M5.Lcd.setCursor(10, 195);
    M5.Lcd.setTextSize(4);
    M5.Lcd.printf("SCORE%4d", S);

    M5.Lcd.setCursor(240, 200);
    M5.Lcd.setTextSize(3);
    M5.Lcd.printf("Lv.%1d", color_s + 1);

    // パドル操作
    bar_move(shift, size);

    // 壁・パドルへの反射と音
    if (x <= 5) { m_x = 0; x = 5; M5.Speaker.tone(392 * pow(step, color_s), 1); delay(10); }
    if (x >= 315) { m_x = 1; x = 315; M5.Speaker.tone(392 * pow(step, color_s), 1); delay(10); }
    if (y <= 5) { m_y = 0; y = 5; M5.Speaker.tone(262 * pow(step, color_s), 1); delay(10); }

    if (m_x == 0) x += speed;
    if (m_x == 1) x -= speed;
    if (m_y == 0) y += speed;
    if (m_y == 1) y -= speed;

    // パドルにヒットした場合
    if (170 <= y && shift - 5 <= x && x <= shift + size + 5) {
        m_y = 1;
        y = 170;
        S += 10;

        if (S % 50 == 0) {
            speed += 0.5;
            if (color_s <= 7) color_s++;
            if (size > 30) size -= 5;
        }
        M5.Speaker.tone(262 * pow(step, color_s), 1);
        delay(10);
    }

    // ボール描画・前フレーム消去
    M5.Lcd.fillCircle(x, y, 5, color[color_s]);
    M5.Lcd.drawLine(shift, 175, shift + size, 175, WHITE);
    delay(30);
    M5.Lcd.fillCircle(chk_x, chk_y, 5, BLACK);
    chk_x = x; chk_y = y;
    M5.Lcd.drawLine(save, 175, save + size, 175, BLACK);
    save = shift;

    M5.Speaker.end();
}


//=== ゲーム終了・スコア表示 ===//

void gameover_screen(int S, int stop) {
    if (stop != 0) {
        // 一旦昇順にソート
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (rank[j] > rank[j + 1]) std::swap(rank[j], rank[j + 1]);

        // 先頭に今回スコア
        rank[0] = S;

        // 降順に再ソート
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (rank[j] < rank[j + 1]) std::swap(rank[j], rank[j + 1]);

        M5.Lcd.setCursor(64, 50);
        M5.Lcd.setTextSize(4);
        M5.Lcd.printf("GAME OVER\n  YOUR SCORE\n   %4d", S);
        delay(3000);
        M5.Lcd.fillScreen(BLACK);
    }
}


//=== モード選択（ランキング or ゲーム開始）===//

void game_option(int option) {
    // 初期化
    srand(time(NULL));
    int r = (rand() % 300) + 10;
    S = 0; speed = 3; step = 1.059;
    shift = save = 110;
    size = 80; stop = 0;
    x = r; y = 10; m_x = m_y = 0;
    chk_x = 160; chk_y = 120; count = 0; color_s = 0;

    if (option == 0) {
        // ランキング表示
        while (1) {
            M5.update();
            M5.Lcd.setTextSize(4);
            M5.Lcd.setCursor(10, 0);
            M5.Lcd.printf(" --RANKING--\n");

            for (int i = 0; i < 5; i++) {
                M5.Lcd.setCursor(10, 37 * (i + 1));
                M5.Lcd.printf("    %d:%3d\n", i + 1, rank[i]);
            }

            M5.Lcd.setCursor(64, 220);
            M5.Lcd.setTextSize(2);
            M5.Lcd.printf("B_button is back.");

            if (M5.BtnB.isPressed()) {
                M5.Speaker.tone(800, 1); delay(30);
                M5.Speaker.end();
                M5.Lcd.fillScreen(BLACK);
                break;
            }
        }
    } else if (option == 1) {
        // ゲーム開始
        while (1) {
            game();
            if (y >= 175) {
                stop++;
                M5.Lcd.fillScreen(BLACK);
                break;
            }
        }
        gameover_screen(S, stop);
    }
}


//=== セットアップ / メインループ ===//

void setup() {
    M5.begin();
    M5.Speaker.begin();
    M5.Speaker.setVolume(4);
}

void loop() {
    option = 0;

    while (1) {
        home();

        if (M5.BtnA.isPressed()) {
            M5.Speaker.tone(800, 1); delay(30);
            M5.Speaker.end();
            M5.Lcd.fillScreen(BLACK);
            option = 0; break;
        }

        if (M5.BtnC.isPressed()) {
            M5.Speaker.tone(800, 1); delay(30);
            M5.Speaker.end();
            M5.Lcd.fillScreen(BLACK);
            option = 1; break;
        }
    }

    game_option(option);
}
