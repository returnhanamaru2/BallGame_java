# Ball Game (Java)

このプロジェクトは、シンプルなボールゲーム（M5Stack風GUI＋ランキング＋ホーム画面）をJava/Swingで実装したものです。

## ディレクトリ構成

```
ballgame/
├── BallGamePanel.java      # メインのゲームパネル（画面遷移・イベント処理）
├── GameState.java          # ゲームの状態管理（スコア・ボール・パドルなど）
├── GameRenderer.java       # ゲーム画面の描画処理
├── RankingManager.java     # ランキング管理
```

## クラス概要

- **BallGamePanel**
  - ゲームのメインパネル。画面遷移（ホーム・ゲーム・ランキング）やキー入力、タイマー処理を担当。
  - `main`メソッドから起動。
- **GameState**
  - ゲームの状態（スコア、ボール・パドルの位置、レベルなど）を管理。
  - 状態の初期化や更新ロジックを持つ。
- **GameRenderer**
  - `GameState`の内容をもとに、ゲーム画面（ボール・パドル・壁・スコアなど）を描画。
- **RankingManager**
  - スコアランキングの管理（配列のソートや取得）を担当。

## 実行方法

1. Javaがインストールされていることを確認してください。
2. 以下のコマンドでコンパイルします。

```
javac BallGamePanel.java GameState.java GameRenderer.java RankingManager.java
```

3. ゲームを起動します。

```
java BallGamePanel
```

## 操作方法
- ホーム画面：
  - `A`キー：ランキング表示
  - `C`キー：ゲーム開始
- ゲーム中：
  - ←/→キー：パドル移動
- ランキング画面：
  - `B`キー：ホームに戻る

---

この構成・説明に合わせて、各ファイルの役割やクラス名・ファイル名を統一してください。
