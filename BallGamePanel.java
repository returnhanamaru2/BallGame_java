// Java変換：M5Stackゲーム（GUI＋ランキング＋ホーム画面）


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 新規分割クラスのインポート
import java.util.Arrays;


/**
 * ゲームのメインパネル。
 * 画面遷移（ホーム・ゲーム・ランキング）、キー入力、タイマー処理を担当。
 * ballgame.cppのメインループや画面制御に相当。
 */
public class BallGamePanel extends JPanel implements ActionListener, KeyListener {
    // ゲーム状態管理クラス
    private final GameState gameState = new GameState();
    // 描画処理クラス
    private final GameRenderer renderer = new GameRenderer(gameState);
    // ランキング管理クラス
    private final RankingManager rankingManager = new RankingManager();

    // ゲーム進行用タイマー
    private Timer timer;
    // ホーム画面表示フラグ
    private boolean showHome = true;
    // ランキング画面表示フラグ
    private boolean showRanking = false;
    // メニュー選択肢（未使用）
    private int option;
    // パドル操作用フラグ
    private boolean pressedLeft = false;
    private boolean pressedRight = false;

    /**
     * パネル初期化。画面サイズ・背景色・キーリスナー・タイマー設定。
     */
    public BallGamePanel() {
        setPreferredSize(new Dimension(GameState.WIDTH, GameState.HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        gameState.init();
        timer = new Timer(30, this);
        timer.start();
    }

    /**
     * ホーム画面の描画処理。
     * タイトルや操作説明を表示。
     */
    private void drawHome(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 320, 5);
        g.fillRect(0, 0, 5, 240);
        g.fillRect(0, 235, 320, 5);
        g.fillRect(315, 0, 5, 240);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.drawString("BALL GAME", 64, 100);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("@2024 TEAM_I MADE IN JAPAN", 10, 20);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.drawString("A = Show Ranking", 50, 180);
        g.drawString("C = Start Game", 50, 200);
    }

    /**
     * 下部フレーム（オレンジ色エリア）の描画。
     * ※現在はGameRendererで描画されているため未使用
     */
    private void drawFrame(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(0, 180, 320, 5);
        g.fillRect(0, 180, 5, 60);
        g.fillRect(0, 235, 320, 5);
        g.fillRect(315, 180, 5, 60);
        g.fillRect(230, 180, 5, 60);
    }

    /**
     * パドル移動処理（現在はGameStateで一括管理のため未使用）
     */
    private void movePaddle() {
        // GameStateのupdateで移動処理を行うため、ここでは何もしない
    }

    /**
     * ゲーム進行・状態更新処理。
     * ballgame.cppのゲームループに相当。
     */
    private void updateGame() {
        int left = pressedLeft ? 1 : 0;
        int right = pressedRight ? 1 : 0;
        gameState.update(left, right);
        if (gameState.isGameOver()) {
            showRanking = false;
            showHome = false;
            timer.stop();
            showGameOver();
        }
    }

    /**
     * ゲームオーバー時の処理。
     * スコアをランキングに反映し、ダイアログ表示後リセット。
     */
    private void showGameOver() {
        rankingManager.updateRanking(gameState.getScore());
        JOptionPane.showMessageDialog(this, "GAME OVER\nYOUR SCORE: " + gameState.getScore());
        gameState.init();
        showRanking = true;
        timer.start();
    }

    /**
     * ランキング画面の描画処理。
     * 上位5件のスコアを表示。
     */
    private void drawRanking(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        g.drawString("-- RANKING --", 60, 40);
        int[] rank = rankingManager.getRanking();
        for (int i = 0; i < 5; i++) {
            g.drawString((i + 1) + ": " + rank[i], 100, 80 + i * 30);
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.drawString("Press B to return", 70, 230);
    }

    /**
     * ゲーム画面の描画処理。
     * GameRendererに委譲。
     */
    private void drawGame(Graphics g) {
        renderer.draw(g);
    }

    // pressedAは不要になったので削除

    /**
     * 画面描画の分岐処理。
     * ホーム・ランキング・ゲーム画面を切り替え描画。
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showHome) drawHome(g);
        else if (showRanking) drawRanking(g);
        else drawGame(g);
    }

    /**
     * タイマーイベント処理。
     * ゲーム進行中のみ状態更新→再描画。
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!showHome && !showRanking) {
            updateGame();
        }
        repaint();
    }

    /**
     * キー押下イベント処理。
     * ホーム・ランキング・ゲーム画面でのキー操作を分岐。
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (showHome) {
            // ホーム画面：A=ランキング、C=ゲーム開始
            if (e.getKeyCode() == KeyEvent.VK_A) {
                option = 0; showHome = false; showRanking = true;
            } else if (e.getKeyCode() == KeyEvent.VK_C) {
                option = 1; showHome = false;
            }
            return;
        }

        if (showRanking) {
            // ランキング画面：B=ホームに戻る
            if (e.getKeyCode() == KeyEvent.VK_B) {
                showRanking = false;
                showHome = true;
            }
            return;
        }

        // ゲーム中：←/→でパドル操作
        if (e.getKeyCode() == KeyEvent.VK_LEFT) pressedLeft = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) pressedRight = true;
    }

    /**
     * キー離上イベント処理。
     * パドル操作用フラグをリセット。
     */
    @Override public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) pressedLeft = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) pressedRight = false;
    }
    /**
     * 未使用（文字入力イベント）
     */
    @Override public void keyTyped(KeyEvent e) {}

    /**
     * エントリポイント。ウィンドウ生成・パネル追加。
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ball Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new BallGamePanel());
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
