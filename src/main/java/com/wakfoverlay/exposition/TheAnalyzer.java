package com.wakfoverlay.exposition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TheAnalyzer {
    private String filePath;
    private List<String> logLines;

    public TheAnalyzer(String filePath) {
        this.filePath = filePath;
        this.logLines = new ArrayList<>();
    }

    public boolean readLogFile() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.err.println("Le fichier n'existe pas : " + filePath);
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logLines.add(line);
                }
            }

            System.out.println("Lecture complète - " + logLines.size() + " lignes lues du fichier : " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            return false;
        }
    }
}
