# 設計書: ScreenAction 再帰的関数フロー構造

## 1. 概要

画面アクションから呼び出される関数フローを再帰的なツリー構造で表現するクラス群を追加する。
各関数が内部で呼び出す子関数を再帰的にネストし、末端でSQL情報を保持する。

## 2. クラス構成

### 2.1 ScreenAction

画面アクションのルートクラス。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| item | `String` | 項目 |
| screenId | `String` | 画面ID |
| actionId | `String` | アクションID |
| functions | `List<FunctionClass>` | トップレベル関数リスト |

### 2.2 FunctionClass

関数情報を保持するクラス。再帰的に子関数をネストする。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| functionName | `String` | 関数名 |
| definitionLines | `int` | 定義行数 |
| effectiveLines | `int` | 有効行数 |
| childFunctions | `List<FunctionClass>` | 子関数フロー（再帰） |
| sqlSessions | `List<SqlSession>` | SQL呼び出し（0個以上） |

### 2.3 SqlSession

SQL情報を保持するクラス。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| sqlId | `String` | SQL ID |
| sqlType | `String` | SQLタイプ（SELECT, INSERT, UPDATE, DELETE等） |
| tables | `List<TableUsage>` | テーブル用途一覧 |

### 2.4 TableUsage

CRUD種別ごとのテーブル名リストを保持するクラス。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| crudType | `String` | CRUDタイプ（C / R / U / D） |
| tableNames | `List<String>` | テーブル名リスト |

## 3. ツリー構築パターン

再帰関数の戻り値で `FunctionClass` を返し、ボトムアップでツリーを組み立てる。

```
FunctionClass traverse(関数情報) {
    FunctionClass self = new FunctionClass(名前, 定義行数, 有効行数);

    for (SQL情報 : この関数のSQL一覧) {
        self.addSqlSession(new SqlSession(...));
    }

    for (子関数 : この関数の呼び出し関数一覧) {
        FunctionClass child = traverse(子関数);
        self.addChildFunction(child);
    }

    return self;
}
```

## 4. デモ構造例

```
ScreenAction(item="ユーザー管理", screenId="SCR001", actionId="ACT001")
├── loginCheck()            [定義:20行, 有効:15行]
│   ├── validateInput()     [定義:10行, 有効:8行]
│   │   └── SQL: SEL001 (SELECT)
│   │       └── R: [users, user_roles]
│   └── updateLoginHistory() [定義:8行, 有効:6行]
│       └── SQL: INS001 (INSERT)
│           └── C: [login_history]
└── loadUserData()          [定義:15行, 有効:12行]
    └── SQL: SEL002 (SELECT)
        ├── R: [users, departments]
        └── R: [user_settings]
```
