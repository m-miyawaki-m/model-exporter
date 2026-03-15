package com.example.exporter;

import com.example.exporter.exporter.CsvExporter;
import com.example.exporter.exporter.JsonExporter;
import com.example.exporter.model.FunctionClass;
import com.example.exporter.model.SampleUser;
import com.example.exporter.model.ScreenAction;
import com.example.exporter.model.SqlSession;
import com.example.exporter.model.TableUsage;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String OUTPUT_CSV = "output/sample.csv";
    private static final String OUTPUT_JSON = "output/sample.json";
    private static final String OUTPUT_SCREEN_ACTION = "output/screen-action.json";

    public static void main(String[] args) {
        exportSampleUsers();
        exportScreenAction();
    }

    /**
     * 既存のサンプルユーザーエクスポート
     */
    private static void exportSampleUsers() {
        List<SampleUser> users = Arrays.asList(
                new SampleUser("Tanaka Taro", "tanaka@example.com", 30),
                new SampleUser("Suzuki Hanako", "suzuki@example.com", 25),
                new SampleUser("Sato Ichiro", "sato@example.com", 35)
        );

        try {
            CsvExporter<SampleUser> csvExporter = new CsvExporter<>();
            csvExporter.export(users, OUTPUT_CSV);
            System.out.println("CSV exported: " + OUTPUT_CSV);

            JsonExporter<SampleUser> jsonExporter = new JsonExporter<>();
            jsonExporter.export(users, OUTPUT_JSON);
            System.out.println("JSON exported: " + OUTPUT_JSON);

        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ScreenAction の再帰的ツリー構築デモ
     *
     * 構造:
     * SCR001 / ACT001
     * ├── loginCheck()
     * │   ├── validateInput()
     * │   │   └── SQL: SEL001 SELECT → R:[users, user_roles]
     * │   └── updateLoginHistory()
     * │       └── SQL: INS001 INSERT → C:[login_history]
     * └── loadUserData()
     *     └── SQL: SEL002 SELECT → R:[users, departments], R:[user_settings]
     */
    private static void exportScreenAction() {
        // --- ボトムアップでツリーを構築 ---

        // 末端: validateInput() → SQL: SEL001
        SqlSession sel001 = new SqlSession("SEL001", "SELECT");
        sel001.addTable(new TableUsage("R", Arrays.asList("users", "user_roles")));

        FunctionClass validateInput = new FunctionClass("validateInput", 10, 8);
        validateInput.addSqlSession(sel001);

        // 末端: updateLoginHistory() → SQL: INS001
        SqlSession ins001 = new SqlSession("INS001", "INSERT");
        ins001.addTable(new TableUsage("C", Arrays.asList("login_history")));

        FunctionClass updateLoginHistory = new FunctionClass("updateLoginHistory", 8, 6);
        updateLoginHistory.addSqlSession(ins001);

        // 中間: loginCheck() → 子関数2つ（SQL直接呼び出しなし）
        FunctionClass loginCheck = new FunctionClass("loginCheck", 20, 15);
        loginCheck.addChildFunction(validateInput);
        loginCheck.addChildFunction(updateLoginHistory);

        // 末端: loadUserData() → SQL: SEL002
        SqlSession sel002 = new SqlSession("SEL002", "SELECT");
        sel002.addTable(new TableUsage("R", Arrays.asList("users", "departments")));
        sel002.addTable(new TableUsage("R", Arrays.asList("user_settings")));

        FunctionClass loadUserData = new FunctionClass("loadUserData", 15, 12);
        loadUserData.addSqlSession(sel002);

        // ルート: ScreenAction
        ScreenAction screenAction = new ScreenAction("ユーザー管理", "SCR001", "ACT001");
        screenAction.addFunction(loginCheck);
        screenAction.addFunction(loadUserData);

        // JSON出力
        try {
            JsonExporter<ScreenAction> jsonExporter = new JsonExporter<>();
            jsonExporter.export(Arrays.asList(screenAction), OUTPUT_SCREEN_ACTION);
            System.out.println("ScreenAction exported: " + OUTPUT_SCREEN_ACTION);
        } catch (Exception e) {
            System.err.println("ScreenAction export failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
