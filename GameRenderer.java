import java.awt.*;

public class GameRenderer {
    // ゲームの状態を保持する参照
    private final GameState state;

    // コンストラクタ：GameStateの参照を受け取る
    public GameRenderer(GameState state) {
        this.state = state;
    }

    // ゲーム画面の描画処理
    public void draw(Graphics g) {
        // --- フレーム（オレンジ色の下部エリア） ---
        g.setColor(Color.ORANGE);
        // 下部の横線
        g.fillRect(0, 180, GameState.WIDTH, 5);
        // 左下の縦線
        g.fillRect(0, 180, 5, 60);
        // 一番下の横線
        g.fillRect(0, 235, GameState.WIDTH, 5);
        // 右下の縦線
        g.fillRect(315, 180, 5, 60);
        // 右側の追加縦線（障害物やデザイン用）
        g.fillRect(230, 180, 5, 60);

        // --- スコアとレベル表示 ---
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        // 左下にスコア表示
        g.drawString("SCORE: " + state.getScore(), 10, 210);
        // 右下にレベル表示
        g.drawString("Lv. " + (state.getLevel() + 1), 240, 210);

        // --- ボールの描画 ---
        // レベルに応じた色でボールを描画
        g.setColor(GameState.COLORS[state.getLevel()]);
        // ボールの中心座標(x, y)から半径5pxの円
        g.fillOval(state.getX() - 5, state.getY() - 5, 10, 10);

        // --- パドル（バー）の描画 ---
        g.setColor(Color.WHITE);
        // パドルの左端(state.getShift())から右端(state.getShift() + state.getSize())までの水平線
        g.drawLine(state.getShift(), 175, state.getShift() + state.getSize(), 175);
    }
}
