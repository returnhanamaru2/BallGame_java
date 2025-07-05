import java.util.Arrays;

/**
 * ランキング管理クラス。
 * 上位5件のスコアを保持・更新。
 * ballgame.cppのランキング配列・ソート処理に相当。
 */
public class RankingManager {
    // ランキング配列（上位5件）
    private int[] rank = new int[5];

    /**
     * コンストラクタ。ランキング初期化。
     */
    public RankingManager() {
        Arrays.fill(rank, 0);
    }

    /**
     * スコアをランキングに反映。
     * 配列に追加→降順ソート。
     * ballgame.cppのランキング更新ロジック。
     */
    public void updateRanking(int score) {
        Arrays.sort(rank);
        rank[0] = score;
        Arrays.sort(rank);
        for (int i = 0; i < rank.length / 2; i++) {
            int temp = rank[i];
            rank[i] = rank[rank.length - 1 - i];
            rank[rank.length - 1 - i] = temp;
        }
    }

    /**
     * ランキング配列のコピーを返す。
     */
    public int[] getRanking() {
        return rank.clone();
    }
}
