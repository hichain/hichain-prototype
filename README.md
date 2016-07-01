# HIchain Prototype版

## オープンβリリース！
![ゲームイメージ](https://github.com/hichain/HIchain-Prototype/blob/master/images/r1.0.0%20Game%20Image.png)

## HIchain とは
  + 同人ボードゲームサークル｢[HIchain Project](http://hichain.sokon.jp)｣が制作した配列ゲームです
  + ルールは[こちら](http://hichain.sokon.jp/rule.html)

## ダウンロード
  + [r1.0.0](https://github.com/hichain/HIchain-Prototype/releases/tag/r1.0.0)
    + [Windows 32bit版](https://github.com/hichain/HIchain-Prototype/releases/download/r1.0.0/HIchain_Prototype_r1_0_0_winx86.zip)
    + [Windows 64bit版](https://github.com/hichain/HIchain-Prototype/releases/download/r1.0.0/HIchain_Prototype_r1_0_0_winx64.zip)
    + [Linux 32bit版](https://github.com/hichain/HIchain-Prototype/releases/download/r1.0.0/HIchain_Prototype_r1_0_0_linux32.zip)
    + [Linux 64bit版](https://github.com/hichain/HIchain-Prototype/releases/download/r1.0.0/HIchain_Prototype_r1_0_0_linux64.zip)
    + **現在ネットワーク対戦は実装しておりません**

## 用語説明
![用語説明](https://raw.githubusercontent.com/hichain/HIchain-Prototype/master/images/terms%20discription.png)

## 操作方法
- 場内
  - **左クリック**: カードを持つ(ホールド)
- 盤内
  - **ドラッグ**: 盤を移動 (ホールド中でも可能)

- ホールド中
  - **左クリック**: カードを置く: 左クリック
  - **右クリック**: キャンセル
  - **ホイールクリック/スペースキー**: カードの回転
  - **Cキー**: 盤を中心に戻す

- **DELETEキー**: リセット

## 制作者
  + [ときわ](https://github.com/TokiwaTools)
  + [うんの](https://github.com/funi)
  + ルキ

## リンク
  + [HP](http://hichain.sokon.jp/)
  + [Twitter](https://twitter.com/HIchain_game)

## リリースノート
### r1.0.0
  基本ルール､最低限のUIを実装
+ ルール処理部分
  + 置ける条件判定
  + 加点文字列探索
    + 連番･ぞろ目･勝利確定文字列
  + ポイント計算
  + ターン
+ データベース部分
  + A-Z, * のデータ追加
+ UI部分
  + 指定したカードが置ける座標をハイライト
  + 加点文字列のハイライト
  + ステータス表示
  + ホールド機能
  + マウスポインタによる盤の移動
  + リセット機能
  + 盤の中心に移動
