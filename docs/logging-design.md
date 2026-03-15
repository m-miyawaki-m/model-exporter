# ログ出力方式検討書

## 1. 背景・目的

### 1.1 現状の課題

現在の model-exporter は、ログ出力に `System.out.println` / `System.err.println` を直接使用している。

| 課題 | 詳細 |
|------|------|
| ログレベル制御不可 | 全メッセージが同一レベルで出力され、重要度の判別ができない |
| 出力先固定 | コンソールのみで、ファイル出力やログ集約ができない |
| フォーマット未統一 | タイムスタンプ、クラス名、スレッド名などのコンテキスト情報がない |
| 運用監視不可 | ログローテーション、アーカイブ、検索ができない |

### 1.2 改善目的

- ログレベルによる出力制御（開発時は DEBUG、本番は INFO 以上など）
- タイムスタンプ・クラス名を含む統一フォーマットでの出力
- ファイル出力・ローテーションによる運用対応
- 将来的なログ集約基盤への接続を見据えた構成

## 2. 技術選定

### 2.1 候補比較

| 項目 | SLF4J + Log4j2 | SLF4J + Logback | java.util.logging |
|------|----------------|-----------------|-------------------|
| パフォーマンス | 非同期 Appender で高性能 | 標準的 | 低い |
| 設定柔軟性 | XML / JSON / YAML / Properties | XML / Groovy | Properties |
| 非同期ログ | AsyncLogger（LMAX Disruptor） | AsyncAppender | なし |
| フィルタリング | 柔軟なフィルタ機能 | 基本的 | 限定的 |
| 依存サイズ | やや大きい | 中程度 | JDK 標準（追加不要） |
| 設定ホットリロード | 対応 | 対応 | 非対応 |
| 採用実績 | Spring Boot 等で広く利用 | Spring Boot デフォルト | レガシー |

### 2.2 選定結果

**SLF4J + Log4j2** を採用する。

**選定理由:**
- 非同期ログの高いパフォーマンス（大量エクスポート処理時に有利）
- XML ベースの柔軟な設定ファイル（`log4j2.xml`）
- 設定のホットリロード対応（運用時にアプリ再起動なしでログレベル変更可能）
- 業界での広い採用実績

## 3. ログレベル方針

### 3.1 ログレベル定義

| レベル | 用途 | 出力例 |
|--------|------|--------|
| ERROR | 処理を継続できない異常 | ファイル書き込み失敗、シリアライズエラー |
| WARN | 処理は継続できるが注意が必要 | 出力ディレクトリの新規作成、空リストのエクスポート |
| INFO | 正常な処理の経過・結果 | エクスポート開始/完了、出力件数 |
| DEBUG | 開発・デバッグ用の詳細情報 | 出力先パス、ObjectMapper 設定詳細 |
| TRACE | 最も詳細なトレース情報 | 各レコードのシリアライズ内容 |

### 3.2 環境別ログレベル

| 環境 | ルートレベル | 用途 |
|------|-------------|------|
| 開発 | DEBUG | 詳細なトレースで問題の早期発見 |
| テスト | INFO | テスト実行結果と処理結果の確認 |
| 本番 | INFO | 処理結果と警告・エラーのみ |
| 障害調査 | DEBUG | 一時的に詳細ログを有効化（ホットリロード利用） |

## 4. 出力先・フォーマット

### 4.1 出力先設計

| Appender | 出力先 | 用途 | 対象レベル |
|----------|--------|------|-----------|
| Console | 標準出力 | 開発時の即時確認 | 全レベル |
| RollingFile | `logs/app.log` | アプリケーションログ保存 | INFO 以上 |
| RollingFile | `logs/error.log` | エラーログ専用 | ERROR のみ |

### 4.2 ログフォーマット

**コンソール出力:**

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
```

**出力例:**

```
2026-03-16 10:30:45.123 [main] INFO  c.e.e.exporter.CsvExporter - CSV export started: output/sample.csv
2026-03-16 10:30:45.456 [main] INFO  c.e.e.exporter.CsvExporter - CSV export completed: 3 records written
2026-03-16 10:30:45.789 [main] ERROR c.e.e.exporter.CsvExporter - CSV export failed: /invalid/path.csv
```

**ファイル出力:**

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{1.} - %msg%n
```

コンソールはクラス名短縮（36文字）、ファイルは省略形（`c.e.e.exporter.CsvExporter`）を使用。

## 5. ローテーション方針

### 5.1 ローテーション設計

| 項目 | app.log | error.log |
|------|---------|-----------|
| ローテーション条件 | 日次 + 10MB 超過時 | 日次 + 5MB 超過時 |
| ファイル名パターン | `logs/app-%d{yyyy-MM-dd}-%i.log.gz` | `logs/error-%d{yyyy-MM-dd}-%i.log.gz` |
| 最大保持期間 | 30日 | 90日 |
| 同一日内の最大ファイル数 | 10 | 10 |
| 最大ファイルサイズ | 10MB | 5MB |
| 圧縮 | gzip | gzip |

> **注:** 保持期間の制御には `DefaultRolloverStrategy` の `Delete` アクション（`IfLastModified`）を使用する。`max` 属性はローリングカウンター `%i` の上限値であり、保持日数ではない。

### 5.2 ディレクトリ構成

```
logs/
├── app.log                          # 現在のアプリケーションログ
├── app-2026-03-15-1.log.gz          # 前日分（圧縮済み）
├── error.log                        # 現在のエラーログ
└── error-2026-03-15-1.log.gz        # 前日分（圧縮済み）
```

## 6. 各モジュールのログ出力設計

### 6.1 Main

| 箇所 | レベル | メッセージ |
|------|--------|-----------|
| アプリケーション開始 | INFO | `Application started` |
| エクスポート成功（CSV） | INFO | `CSV exported: {filePath}` |
| エクスポート成功（JSON） | INFO | `JSON exported: {filePath}` |
| エクスポート失敗 | ERROR | `Export failed: {errorMessage}` |
| アプリケーション終了 | INFO | `Application finished` |

### 6.2 CsvExporter

| 箇所 | レベル | メッセージ |
|------|--------|-----------|
| エクスポート開始 | INFO | `CSV export started: {filePath}` |
| ディレクトリ作成 | WARN | `Created output directory: {dirPath}` |
| エクスポート完了 | INFO | `CSV export completed: {recordCount} records written to {filePath}` |
| ファイル書き込み失敗 | ERROR | `CSV export failed for {filePath}: {errorMessage}` |
| レコード処理詳細 | DEBUG | `Processing {recordCount} records for CSV export` |

### 6.3 JsonExporter

| 箇所 | レベル | メッセージ |
|------|--------|-----------|
| エクスポート開始 | INFO | `JSON export started: {filePath}` |
| ディレクトリ作成 | WARN | `Created output directory: {dirPath}` |
| エクスポート完了 | INFO | `JSON export completed: {recordCount} records written to {filePath}` |
| ファイル書き込み失敗 | ERROR | `JSON export failed for {filePath}: {errorMessage}` |
| ObjectMapper 設定 | DEBUG | `ObjectMapper configured with INDENT_OUTPUT` |

## 7. 設定ファイル仕様

### 7.1 log4j2.xml

配置先: `src/main/resources/log4j2.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">

    <Properties>
        <Property name="LOG_DIR">logs</Property>
        <Property name="CONSOLE_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Property>
        <Property name="FILE_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{1.} - %msg%n</Property>
    </Properties>

    <Appenders>
        <!-- コンソール出力 -->
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${CONSOLE_PATTERN}" />
        </Console>

        <!-- アプリケーションログ -->
        <RollingFile name="AppLog"
                     fileName="${LOG_DIR}/app.log"
                     filePattern="${LOG_DIR}/app-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${FILE_PATTERN}" />
            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true" />
                <SizeBasedTriggeringPolicy size="10MB" />
            </Policies>
            <DefaultRolloverStrategy max="10">
                <Delete basePath="${LOG_DIR}" maxDepth="1">
                    <IfFileName glob="app-*.log.gz" />
                    <IfLastModified age="30d" />
                </Delete>
            </DefaultRolloverStrategy>
        </RollingFile>

        <!-- エラーログ -->
        <RollingFile name="ErrorLog"
                     fileName="${LOG_DIR}/error.log"
                     filePattern="${LOG_DIR}/error-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${FILE_PATTERN}" />
            <Filters>
                <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY" />
            </Filters>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true" />
                <SizeBasedTriggeringPolicy size="5MB" />
            </Policies>
            <DefaultRolloverStrategy max="10">
                <Delete basePath="${LOG_DIR}" maxDepth="1">
                    <IfFileName glob="error-*.log.gz" />
                    <IfLastModified age="90d" />
                </Delete>
            </DefaultRolloverStrategy>
        </RollingFile>
    </Appenders>

    <Loggers>
        <!-- アプリケーションロガー -->
        <Logger name="com.example.exporter" level="INFO" additivity="false">
            <AppenderRef ref="Console" />
            <AppenderRef ref="AppLog" />
            <AppenderRef ref="ErrorLog" />
        </Logger>

        <!-- ルートロガー -->
        <Root level="WARN">
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>

</Configuration>
```

### 7.2 build.gradle 追加依存関係

```groovy
dependencies {
    // 既存
    implementation 'com.opencsv:opencsv:5.7.1'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.4'

    // ログ出力（追加）
    implementation 'org.apache.logging.log4j:log4j-api:2.23.1'
    implementation 'org.apache.logging.log4j:log4j-core:2.23.1'
    implementation 'org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1'
    implementation 'org.slf4j:slf4j-api:2.0.13'
}
```

### 7.3 .gitignore 追加

```
# ログファイル
logs/
```
