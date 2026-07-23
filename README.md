# 時計アプリ

小学生向けに、日付と時刻の感覚を触って学べる Android アプリです。デジタル表示とアナログ表示を並べ、年・月・日・時・分・秒を動かしながら時間の進み方を確認できます。

## 主な機能

- 現在時刻のリアルタイム表示と一時停止
- 年・月・日・時・分・秒の増減と直接入力
- 針をドラッグして操作できるアナログ時計
- 月の日数、年内の進み具合、月内の進み具合の表示
- 横画面固定の学習向けレイアウト

## 開発環境

- Android Studio
- JDK 21
- Kotlin
- Jetpack Compose
- Android Gradle Plugin

## ビルドと実行

```bash
./gradlew assembleDebug
```

Android Studio で開く場合は、このリポジトリをプロジェクトとして開き、`app` 構成を選んで実行してください。

## プロジェクト構成

```text
app/src/main/kotlin/com/example/schoolclockgame/
├── domain/          # 日付・時刻計算
├── presentation/    # 時計学習画面と状態管理
└── ui/theme/        # Compose テーマ
```

## ライセンス

このプロジェクトのライセンスは [LICENSE](LICENSE) を参照してください。
