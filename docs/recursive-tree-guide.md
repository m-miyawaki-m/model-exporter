# 再帰的ツリー構築ガイド

## 1. この資料の目的

再帰的なクラス構造（ScreenAction → FunctionClass → SqlSession）を **どう設計し、どう組み立てるか** を解説する。
別プロジェクトで関数フローの解析結果をこのクラス群に詰め込む際の考え方を理解するための資料。

---

## 2. 全体像：3層のクラス構造

```
【起点】ScreenAction ── 画面単位のルート
   │
   └─【再帰層】FunctionClass ── 関数フロー（自分自身をネスト可能）
        │
        └─【末端】SqlSession ── SQL情報 + テーブル用途
```

**ポイント:** FunctionClass だけが再帰的。ScreenAction と SqlSession は再帰しない。

---

## 3. 各クラスの役割

### 3.1 起点：ScreenAction

```java
ScreenAction
├── item           : String              // 項目
├── screenId       : String              // 画面ID
├── actionId       : String              // アクションID
└── functions      : List<FunctionClass> // トップレベル関数（エントリーポイント）
```

- **1つの画面アクション** に対して **1つの ScreenAction** を作る
- `functions` には、そのアクションから **直接呼ばれる関数** だけを入れる
- 関数の中からさらに呼ばれる関数は、FunctionClass 内部の `childFunctions` に入る

### 3.2 再帰層：FunctionClass

```java
FunctionClass
├── functionName    : String              // 関数名
├── definitionLines : int                 // 定義行数
├── effectiveLines  : int                 // 有効行数
├── childFunctions  : List<FunctionClass> // ★ 子関数（再帰）
└── sqlSessions     : List<SqlSession>    // この関数が直接呼ぶSQL（0個以上）
```

- **自分自身の型をリストで持つ** ことで再帰構造を実現
- `childFunctions` が空 → 末端関数（リーフ）
- `sqlSessions` が空 → SQLを直接呼ばない中間関数

#### 関数の3つのパターン

```
パターン①：中間関数（SQL なし、子関数あり）
┌─────────────────────────┐
│ loginCheck()            │
│ childFunctions: [A, B]  │  ← 子関数を持つ
│ sqlSessions: []         │  ← SQL は直接呼ばない
└─────────────────────────┘

パターン②：末端関数（SQL あり、子関数なし）
┌─────────────────────────┐
│ validateInput()         │
│ childFunctions: []      │  ← 子関数なし（リーフ）
│ sqlSessions: [SEL001]   │  ← SQL を呼ぶ
└─────────────────────────┘

パターン③：混合関数（SQL あり、子関数もあり）
┌─────────────────────────┐
│ processOrder()          │
│ childFunctions: [C]     │  ← 子関数も持つ
│ sqlSessions: [UPD001]   │  ← 自分自身もSQL を呼ぶ
└─────────────────────────┘
```

### 3.3 末端：SqlSession + TableUsage

```java
SqlSession
├── sqlId   : String           // SQL ID
├── sqlType : String           // SELECT / INSERT / UPDATE / DELETE
└── tables  : List<TableUsage> // テーブル用途一覧

TableUsage
├── crudType   : String       // C / R / U / D
└── tableNames : List<String> // テーブル名リスト
```

- 1つの SQL が複数テーブルを参照する場合がある
- CRUD タイプごとにテーブル名をグルーピング

---

## 4. 再帰の考え方：ボトムアップ構築

### 4.1 核心：「末端から作って親に渡す」

再帰的なツリーを組み立てるとき、**一番下（末端）のインスタンスから先に作り、親に詰めていく**。

```
構築順序:

Step 1: 末端の SqlSession を作る
Step 2: 末端の FunctionClass を作り、SqlSession をセット
Step 3: 親の FunctionClass を作り、子 FunctionClass をセット
Step 4: ScreenAction を作り、トップレベル FunctionClass をセット
```

### 4.2 具体例：手動構築

以下のフローを組み立てる場合：

```
loginCheck()
├── validateInput()
│   └── SQL: SEL001 SELECT → R:[users, user_roles]
└── updateLoginHistory()
    └── SQL: INS001 INSERT → C:[login_history]
```

```java
// ===== Step 1: 末端の SQL から作る =====

// validateInput が呼ぶ SQL
SqlSession sel001 = new SqlSession("SEL001", "SELECT");
sel001.addTable(new TableUsage("R", Arrays.asList("users", "user_roles")));

// updateLoginHistory が呼ぶ SQL
SqlSession ins001 = new SqlSession("INS001", "INSERT");
ins001.addTable(new TableUsage("C", Arrays.asList("login_history")));

// ===== Step 2: 末端の関数を作り、SQL をセット =====

FunctionClass validateInput = new FunctionClass("validateInput", 10, 8);
validateInput.addSqlSession(sel001);  // ← SQL を関数にセット

FunctionClass updateLoginHistory = new FunctionClass("updateLoginHistory", 8, 6);
updateLoginHistory.addSqlSession(ins001);

// ===== Step 3: 親関数を作り、子関数をセット =====

FunctionClass loginCheck = new FunctionClass("loginCheck", 20, 15);
loginCheck.addChildFunction(validateInput);       // ← 子関数をセット
loginCheck.addChildFunction(updateLoginHistory);   // ← 子関数をセット

// ===== Step 4: ScreenAction にトップレベル関数をセット =====

ScreenAction screen = new ScreenAction("ユーザー管理", "SCR001", "ACT001");
screen.addFunction(loginCheck);  // ← トップレベル関数をセット
```

### 4.3 なぜ「ボトムアップ」なのか

```
トップダウン（NG）:
  loginCheck を作る → 子関数をセットしたいが、まだ存在しない → 後から追加？
  → コードが前後に散らばり、読みにくくなる

ボトムアップ（OK）:
  validateInput を作る → 完成品
  updateLoginHistory を作る → 完成品
  loginCheck を作る → 完成済みの子を渡すだけ
  → 各ステップで「完成したもの」を扱うので明快
```

---

## 5. 再帰の考え方：traverse 関数（自動構築）

### 5.1 手動構築の限界

手動構築は理解のためには良いが、1万件の関数フローを手で書くことはできない。
**再帰関数（traverse）を使って自動的にツリーを構築する。**

### 5.2 traverse の設計原則

```
traverse(関数情報) → FunctionClass を返す
```

**ルール:**
1. 自分自身の FunctionClass インスタンスを作る
2. 自分が持つ SQL を SqlSession として追加する
3. 自分が呼ぶ子関数それぞれに対して **traverse を再帰呼び出し** し、戻り値を childFunctions に追加する
4. 完成した FunctionClass を **return する**

### 5.3 コードの全体像

```java
/**
 * 関数情報を受け取り、再帰的に FunctionClass ツリーを構築する。
 *
 * @param funcInfo 解析済みの関数情報（関数名、行数、子関数一覧、SQL一覧を持つ）
 * @return 子関数・SQL がすべてセット済みの FunctionClass
 */
public FunctionClass traverse(FuncInfo funcInfo) {

    // Step 1: 自分自身のインスタンスを作る
    FunctionClass self = new FunctionClass(
        funcInfo.getName(),
        funcInfo.getDefinitionLines(),
        funcInfo.getEffectiveLines()
    );

    // Step 2: SQL をセット（0個の場合もある）
    for (SqlInfo sqlInfo : funcInfo.getSqlList()) {
        SqlSession sql = new SqlSession(sqlInfo.getId(), sqlInfo.getType());
        for (TableInfo tableInfo : sqlInfo.getTables()) {
            sql.addTable(new TableUsage(
                tableInfo.getCrudType(),
                tableInfo.getTableNames()
            ));
        }
        self.addSqlSession(sql);
    }

    // Step 3: 子関数を再帰的にたどる
    for (FuncInfo childInfo : funcInfo.getChildFunctions()) {
        FunctionClass child = traverse(childInfo);  // ★ 再帰呼び出し
        self.addChildFunction(child);               // ★ 戻り値を自分にセット
    }

    // Step 4: 完成したインスタンスを返す
    return self;
}
```

### 5.4 呼び出し側

```java
// エントリーポイント: ScreenAction の組み立て
public ScreenAction buildScreenAction(ScreenInfo screenInfo) {
    ScreenAction screen = new ScreenAction(
        screenInfo.getItem(),
        screenInfo.getScreenId(),
        screenInfo.getActionId()
    );

    // トップレベル関数それぞれを traverse で構築
    for (FuncInfo topFunc : screenInfo.getTopLevelFunctions()) {
        FunctionClass func = traverse(topFunc);  // ← ここで再帰ツリー全体が構築される
        screen.addFunction(func);
    }

    return screen;
}
```

### 5.5 実行時の流れ（トレース）

```
buildScreenAction(SCR001) 開始
│
├── traverse(loginCheck) 開始
│   ├── FunctionClass("loginCheck") 作成
│   ├── SQL なし
│   ├── traverse(validateInput) 開始          ← 再帰
│   │   ├── FunctionClass("validateInput") 作成
│   │   ├── SqlSession("SEL001") 作成・セット
│   │   ├── 子関数なし
│   │   └── return FunctionClass("validateInput")  ← ★ 完成品を返す
│   │
│   ├── loginCheck.addChildFunction(validateInput)  ← ★ 戻り値をセット
│   │
│   ├── traverse(updateLoginHistory) 開始     ← 再帰
│   │   ├── FunctionClass("updateLoginHistory") 作成
│   │   ├── SqlSession("INS001") 作成・セット
│   │   ├── 子関数なし
│   │   └── return FunctionClass("updateLoginHistory")
│   │
│   ├── loginCheck.addChildFunction(updateLoginHistory)
│   └── return FunctionClass("loginCheck")    ← ★ 子関数が詰まった完成品
│
├── screen.addFunction(loginCheck)
│
├── traverse(loadUserData) 開始
│   ├── FunctionClass("loadUserData") 作成
│   ├── SqlSession("SEL002") 作成・セット
│   ├── 子関数なし
│   └── return FunctionClass("loadUserData")
│
├── screen.addFunction(loadUserData)
│
└── return ScreenAction(SCR001)               ← ★ 全ツリー完成
```

---

## 6. 循環参照ガード

### 6.1 なぜ必要か

関数 A が関数 B を呼び、関数 B が関数 A を呼ぶようなケース：

```
A() → B() → A() → B() → A() → ... （無限ループ）
```

ガードなしでは `StackOverflowError` でクラッシュする。

### 6.2 実装方法

```java
public FunctionClass traverse(FuncInfo funcInfo, Set<String> visited) {

    // 循環チェック
    if (visited.contains(funcInfo.getName())) {
        System.err.println("WARNING: Circular reference: " + funcInfo.getName());
        return null;  // または空の FunctionClass を返す
    }
    visited.add(funcInfo.getName());

    FunctionClass self = new FunctionClass(...);

    // SQL セット（省略）

    // 子関数を再帰
    for (FuncInfo childInfo : funcInfo.getChildFunctions()) {
        FunctionClass child = traverse(childInfo, visited);  // visited を引き回す
        if (child != null) {
            self.addChildFunction(child);
        }
    }

    visited.remove(funcInfo.getName());  // 戻る時に解放（別ルートの同名関数は許可）
    return self;
}
```

### 6.3 visited の add / remove の意味

```
A → B → C（正常）
         ↓
visited: {A, B, C}

C の処理完了、戻る
         ↓
visited: {A, B}      ← C を remove

B の処理完了、戻る
         ↓
visited: {A}          ← B を remove

次に A → D → B の場合：
         ↓
visited: {A, D, B}    ← B は再度 add できる（別ルートなので OK）
```

**remove しないと:** 一度通った関数が別ルートからも呼べなくなる。
**remove すると:** 同じ関数でも「別の経路」からの呼び出しは許可される。

---

## 7. 全体のまとめ

### 7.1 設計の3原則

| 原則 | 内容 |
|------|------|
| **再帰は戻り値で組み立てる** | traverse が FunctionClass を return → 親が addChildFunction |
| **末端から完成していく** | SQL → 末端関数 → 中間関数 → ScreenAction の順に完成 |
| **循環は visited セットで防ぐ** | add で記録、remove で解放、contains で検出 |

### 7.2 別プロジェクトへの適用手順

```
Step 1: model クラス（ScreenAction, FunctionClass, SqlSession, TableUsage）をコピー
Step 2: 既存の解析処理から FuncInfo 相当のデータを取得
Step 3: traverse 関数を実装（上記テンプレートをベースに）
Step 4: buildScreenAction でツリーを構築
Step 5: JsonExporter / CsvExporter + ScreenActionFlattener で出力
```

### 7.3 クラス図（まとめ）

```
┌──────────────────────────────────────────────────────────┐
│                       利用側コード                         │
│                                                          │
│  ScreenAction screen = buildScreenAction(screenInfo);    │
│  // ↑ 内部で traverse が再帰的にツリーを構築              │
│                                                          │
│  // JSON 出力                                            │
│  new JsonExporter<>().export(List.of(screen), path);     │
│                                                          │
│  // CSV 出力（テーブル単位でフラット化）                    │
│  List<ScreenActionRow> rows = flattener.flatten(screen); │
│  new CsvExporter<>().export(rows, path);                 │
└──────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
   ScreenAction         FunctionClass          SqlSession
   ├ item               ├ functionName         ├ sqlId
   ├ screenId           ├ definitionLines      ├ sqlType
   ├ actionId           ├ effectiveLines       └ tables: List<TableUsage>
   └ functions ─────→   ├ childFunctions ─→ (自分自身の型)
                        └ sqlSessions ──────→
```
