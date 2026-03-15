package com.example.exporter;

import com.example.exporter.exporter.CsvExporter;
import com.example.exporter.exporter.JsonExporter;
import com.example.exporter.model.SampleUser;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String OUTPUT_CSV = "output/sample.csv";
    private static final String OUTPUT_JSON = "output/sample.json";

    public static void main(String[] args) {
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
}
