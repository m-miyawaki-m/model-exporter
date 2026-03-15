# 利用方法ガイド

## 1. 概要

model-exporter は、Java オブジェクト（モデル）を CSV および JSON 形式でファイルに出力するユーティリティです。
ジェネリクスを活用した汎用的なエクスポーターにより、任意のモデルクラスのデータを簡単にファイル出力できます。

## 2. 前提条件

| 項目 | バージョン |
|------|-----------|
| Java (JDK) | 11 以上 |
| Gradle | 7.2（Wrapper 同梱） |
| OS | Windows / macOS / Linux |

## 3. セットアップ

### 3.1 リポジトリの取得

```bash
git clone <repository-url>
cd model-exporter
```

### 3.2 ビルド

```bash
./gradlew compileJava
```

ビルドが成功すると `build/classes/` 配下にコンパイル済みクラスが生成されます。

## 4. 実行方法

### 4.1 アプリケーションの実行

```bash
./gradlew run
```

### 4.2 実行結果

実行が成功すると以下のメッセージが表示されます。

```
CSV exported: output/sample.csv
JSON exported: output/sample.json
```

## 5. 出力ファイル

### 5.1 出力先

| 形式 | ファイルパス |
|------|-------------|
| CSV | `output/sample.csv` |
| JSON | `output/sample.json` |

`output/` ディレクトリは実行時に自動作成されます。

### 5.2 CSV 出力フォーマット

```csv
AGE,EMAIL,NAME
30,tanaka@example.com,Tanaka Taro
25,suzuki@example.com,Suzuki Hanako
35,sato@example.com,Sato Ichiro
```

- ヘッダー行は `@CsvBindByName` アノテーションの `column` 属性値
- クォート文字なし（`CSVWriter.NO_QUOTE_CHARACTER` 指定）

### 5.3 JSON 出力フォーマット

```json
[
  {
    "name": "Tanaka Taro",
    "email": "tanaka@example.com",
    "age": 30
  },
  {
    "name": "Suzuki Hanako",
    "email": "suzuki@example.com",
    "age": 25
  },
  {
    "name": "Sato Ichiro",
    "email": "sato@example.com",
    "age": 35
  }
]
```

- インデント付き整形出力（`SerializationFeature.INDENT_OUTPUT` 有効）

## 6. カスタマイズ

### 6.1 独自モデルの追加

**Step 1:** `src/main/java/com/example/exporter/model/` にモデルクラスを作成します。

```java
package com.example.exporter.model;

import com.opencsv.bean.CsvBindByName;

public class Product {

    @CsvBindByName(column = "PRODUCT_NAME")
    private String productName;

    @CsvBindByName(column = "PRICE")
    private int price;

    // デフォルトコンストラクタ（CSV出力に必要）
    public Product() {}

    public Product(String productName, int price) {
        this.productName = productName;
        this.price = price;
    }

    // getter / setter 省略
}
```

**Step 2:** `Main.java` でエクスポーターを利用します。

```java
List<Product> products = Arrays.asList(
    new Product("Widget", 1000),
    new Product("Gadget", 2500)
);

CsvExporter<Product> csvExporter = new CsvExporter<>();
csvExporter.export(products, "output/products.csv");

JsonExporter<Product> jsonExporter = new JsonExporter<>();
jsonExporter.export(products, "output/products.json");
```

### 6.2 出力先の変更

`Main.java` 内の定数を変更します。

```java
private static final String OUTPUT_CSV = "output/sample.csv";   // 任意のパスに変更
private static final String OUTPUT_JSON = "output/sample.json";  // 任意のパスに変更
```

## 7. トラブルシューティング

| 症状 | 原因 | 対処法 |
|------|------|--------|
| `JAVA_HOME is not set` | JDK が未設定 | `JAVA_HOME` 環境変数を JDK 11 以上のパスに設定 |
| `Could not resolve dependencies` | Maven Central に接続不可 | ネットワーク接続・プロキシ設定を確認 |
| `FileNotFoundException` | 出力先ディレクトリの親パスが不正 | ファイルパスが正しいか確認（ディレクトリは自動作成される） |
| `Export failed` + スタックトレース | モデルクラスにデフォルトコンストラクタがない | 引数なしコンストラクタを追加 |
| CSV ヘッダーが出ない | `@CsvBindByName` アノテーション未付与 | フィールドにアノテーションを付与 |
