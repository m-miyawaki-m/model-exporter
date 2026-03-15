package com.example.exporter.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JsonExporter<T> {

    private final ObjectMapper objectMapper;

    public JsonExporter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void export(List<T> data, String filePath) throws Exception {
        Files.createDirectories(Paths.get(filePath).getParent());
        objectMapper.writeValue(new File(filePath), data);
    }
}
