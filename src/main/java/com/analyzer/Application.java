package com.analyzer;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@SpringBootApplication
@Controller
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @GetMapping("/")
    public String index() { return "index"; }

    @PostMapping("/analyze")
    public String analyzeResume(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a valid PDF file!");
            return "index";
        }
        try {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String resumeText = pdfStripper.getText(document);
            document.close();
            StringBuilder report = new StringBuilder();
            if (resumeText.toLowerCase().contains("experience") || resumeText.toLowerCase().contains("project")) {
                report.append("✅ Experience/Projects Section: FOUND\n");
            } else {
                report.append("❌ Experience/Projects Section: MISSING (Adding projects is highly recommended)\n");
            }
            report.append("\n🛠️ Technical Skills Found:\n");
            String[] skillsDatabase = {"java", "python", "html", "css", "sql", "javascript", "spring", "react"};
            for (String skill : skillsDatabase) {
                if (resumeText.toLowerCase().contains(skill)) {
                    report.append("  • ").append(skill.toUpperCase()).append("\n");
                }
            }
            model.addAttribute("report", report.toString());
        } catch (IOException e) {
            model.addAttribute("error", "Error reading PDF file: " + e.getMessage());
        }
        return "index";
    }
}
