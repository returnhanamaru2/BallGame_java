import java.awt.*;
import java.util.Random;

/**
 * ゲームの状態管理クラス。
 * スコア、ボール・パドルの位置、レベルなどを保持。
 * ballgame.cppの変数・ロジックをJavaクラス化。
 */
public class GameState {
    // 画面サイズ
    public static final int WIDTH = 320, HEIGHT = 240;
    // レベルごとのボール色
    public static final Color[] COLORS = {
        Color.WHITE, Color.PINK, new Color(173,255,47), Color.GREEN,
        Color.CYAN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.RED
    };

    // スコア
    private int score;
    // ボールの速度
    private double speed;
    // パドルの左端座標と長さ
    private int shift, size;
    // ボールの座標
    private int x, y;
    // ボールの移動方向（0:正方向, 1:逆方向）
    private int m_x, m_y;
    // 現在のレベル（色）
    private int color_s;
    // ゲームオーバーフラグ
    private boolean gameOver;

    /**
     * コンストラクタ。初期化処理を呼び出し。
     */
    public GameState() {
        init();
    }

    /**
     * ゲーム状態の初期化。
     * ballgame.cppの初期化処理に相当。
     */
    public void init() {
        Random r = new Random();
        x = r.nextInt(300) + 10; // ボールの初期X座標
        y = 10;                  // ボールの初期Y座標
        score = 0;
        speed = 3.0;
        shift = 110;             // パドルの初期位置
        size = 80;               // パドルの初期長さ
        m_x = m_y = 0;           // ボールの移動方向初期化
        color_s = 0;             // レベル初期化
        gameOver = false;
    }

    /**
     * ゲーム状態の更新処理。
     * パドル移動・ボール移動・衝突判定・スコア加算・レベルアップ・ゲームオーバー判定。
     * ballgame.cppのメインループロジックに相当。
     * @param left  左キー押下フラグ
     * @param right 右キー押下フラグ
     */
    public void update(int left, int right) {
        // パドル移動
        if (left > 0 && shift >= 0) shift -= 10;
        if (right > 0 && shift + size <= WIDTH) shift += 10;

        // ボールの壁衝突判定
        if (x <= 5) { m_x = 0; x = 5; }
        if (x >= 315) { m_x = 1; x = 315; }
        if (y <= 5) { m_y = 0; y = 5; }

        // ボール移動
        x += (m_x == 0 ? speed : -speed);
        y += (m_y == 0 ? speed : -speed);

        // パドルとの衝突判定
        if (y >= 170 && shift - 5 <= x && x <= shift + size + 5) {
            m_y = 1;
            y = 170;
            score += 10;
            // レベルアップ処理
            if (score % 50 == 0) {
                speed += 0.5;
                if (color_s < 7) color_s++;
                if (size > 30) size -= 5;
            }
        }

        // 下端に到達したらゲームオーバー
        if (y >= 175) {
            gameOver = true;
        }
    }

    // --- 各種ゲッター ---
    /** スコア取得 */
    public int getScore() { return score; }
    /** レベル取得 */
    public int getLevel() { return color_s; }
    /** ボールX座標取得 */
    public int getX() { return x; }
    /** ボールY座標取得 */
    public int getY() { return y; }
    /** パドル左端座標取得 */
    public int getShift() { return shift; }
    /** パドル長さ取得 */
    public int getSize() { return size; }
    /** ゲームオーバーフラグ取得 */
    public boolean isGameOver() { return gameOver; }
    /** ゲームオーバーフラグ設定 */
    public void setGameOver(boolean b) { gameOver = b; }
}
