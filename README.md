# 時計アプリ（School Clock Game）

小学生が、日付と時刻の関係を実際に動かしながら学べる Android アプリです。

デジタル表示とアナログ表示を同じ画面に並べ、年・月・日・時・分・秒を個別に操作できます。時計の読み方だけでなく、「針が一周すると次の単位が進む」「月によって日数が異なる」といった時間と暦の仕組みを視覚的に確認できます。

## 主な機能

- 現在の日付・時刻をリアルタイム表示
- 表示を一時停止し、任意の日付・時刻を操作
- 年・月・日・時・分・秒をボタンまたは数値入力で変更
- 針をドラッグして操作できるアナログ時計
- 年・月・日・時・分・秒を個別のダイヤルで表示
- 月の日数、年内・月内の進み具合を視覚化
- 学習時に見やすい横画面固定レイアウト

## 対応環境

- Android 8.0（API 26）以上
- 横画面での利用
- 日本語UI

アプリはネットワーク接続や特別な端末権限を必要としません。

## インストール

### 配布APKを使う

リポジトリ内の [`dist/school-clock-game.apk`](dist/school-clock-game.apk) を Android 端末へ転送して開いてください。端末の設定によっては、ブラウザやファイル管理アプリからの「不明なアプリのインストール」を許可する必要があります。

### 開発環境から実行する

必要なもの：

- Android Studio
- JDK 21
- Android SDK 35
- Android 8.0（API 26）以上の実機、またはエミュレーター

1. このリポジトリをクローンします。
2. Android Studio でリポジトリのルートディレクトリを開きます。
3. Gradle Sync の完了を待ちます。
4. 実行先の端末を選択し、`app` 構成を実行します。

## 使い方

画面上部のモードを切り替えて操作します。

- **現在値**：端末の現在日時に合わせて表示が進みます。
- **停止**：表示の進行を止め、各値を自由に変更できます。
- **デジタル表示**：各単位の増減や、数値の直接入力ができます。
- **アナログ表示**：時計の針や各ダイヤルをドラッグして値を変更できます。

手動で値を変更すると停止状態になります。再び現在日時を表示するには「現在値」を選択してください。

## ビルド

リポジトリのルートで次のコマンドを実行します。

```bash
./gradlew assembleDebug
```

生成されたAPKは `app/build/outputs/apk/debug/app-debug.apk` に出力されます。

Makefileを利用する場合は、次のコマンドも使用できます。

```bash
make debug    # デバッグAPKをビルド
make install  # 接続中の端末へデバッグAPKをインストール（adbが必要）
make apk      # ビルドしたAPKをdist/school-clock-game.apkへコピー
make clean    # ビルド成果物を削除
```

署名されていないリリースAPKを作る場合は `make release` を実行します。一般配布には別途署名が必要です。

## 技術構成

- Kotlin 2.0.21
- Jetpack Compose / Material 3
- Android Gradle Plugin 8.7.3
- JDK 21
- MVVM（ViewModel + StateFlow）

## プロジェクト構成

```text
app/src/main/
├── AndroidManifest.xml
├── kotlin/com/example/schoolclockgame/
│   ├── MainActivity.kt       # アプリのエントリーポイント
│   ├── domain/
│   │   ├── calendar/         # 暦の日付計算
│   │   └── clock/            # 時計の時刻計算
│   ├── presentation/clock/   # 画面、状態管理、操作ロジック
│   └── ui/theme/             # Composeテーマ
└── res/                      # アイコンなどのAndroidリソース
```

## ライセンス

このプロジェクトは MIT License のもとで公開されています。詳細は [LICENSE](LICENSE) を参照してください。
