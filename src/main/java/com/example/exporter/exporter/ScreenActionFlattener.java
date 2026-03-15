package com.example.exporter.exporter;

import com.example.exporter.model.FunctionClass;
import com.example.exporter.model.ScreenAction;
import com.example.exporter.model.ScreenActionRow;
import com.example.exporter.model.SqlSession;
import com.example.exporter.model.TableUsage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ScreenAction の再帰的ツリー構造をテーブル単位のフラットな行リストに変換する。
 */
public class ScreenActionFlattener {

    /**
     * ScreenAction をフラット化して ScreenActionRow のリストを返す。
     */
    public List<ScreenActionRow> flatten(ScreenAction screenAction) {
        List<ScreenActionRow> rows = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        for (FunctionClass func : screenAction.getFunctions()) {
            flattenFunction(screenAction, func, "", rows, visited);
        }
        return rows;
    }

    /**
     * 再帰的に FunctionClass をたどり、テーブル単位で行を生成する。
     * 循環参照を検出した場合はスキップし、警告を出力する。
     *
     * @param screenAction 親の ScreenAction（共通情報）
     * @param func         現在の関数
     * @param parentPath   親までの関数パス（空文字ならルート）
     * @param rows         結果を蓄積するリスト
     * @param visited      訪問済み関数パスのセット（循環参照検出用）
     */
    private void flattenFunction(ScreenAction screenAction, FunctionClass func,
                                 String parentPath, List<ScreenActionRow> rows,
                                 Set<String> visited) {
        String currentPath = parentPath.isEmpty()
                ? func.getFunctionName()
                : parentPath + " > " + func.getFunctionName();

        // 循環参照チェック
        if (visited.contains(currentPath)) {
            System.err.println("WARNING: Circular reference detected, skipping: " + currentPath);
            return;
        }
        visited.add(currentPath);

        // この関数が持つ SQL をテーブル単位で展開
        for (SqlSession sql : func.getSqlSessions()) {
            for (TableUsage usage : sql.getTables()) {
                for (String tableName : usage.getTableNames()) {
                    rows.add(new ScreenActionRow(
                            screenAction.getItem(),
                            screenAction.getScreenId(),
                            screenAction.getActionId(),
                            currentPath,
                            func.getDefinitionLines(),
                            func.getEffectiveLines(),
                            sql.getSqlId(),
                            sql.getSqlType(),
                            usage.getCrudType(),
                            tableName
                    ));
                }
            }
        }

        // 子関数を再帰的にたどる
        for (FunctionClass child : func.getChildFunctions()) {
            flattenFunction(screenAction, child, currentPath, rows, visited);
        }

        visited.remove(currentPath);
    }
}
