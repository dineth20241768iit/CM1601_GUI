package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import services.FileImportService;
import services.ReportService;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label lblStatus;

    // Shared service instances passed to all child controllers
    private final FileImportService importService = new FileImportService();
    private ReportService reportService;

    @FXML
    public void showImport() {
        loadScreen("import");
    }

    @FXML
    public void showIssuedReport() {
        if (checkImported()) return;
        loadScreen("issued_report");
    }

    @FXML
    public void showAvgCost() {
        if (checkImported()) return;
        loadScreen("avg_cost");
    }

    @FXML
    public void showAvailability() {
        if (checkImported()) return;
        loadScreen("availability");
    }

    // Called by ImportController after successful import
    public void onImportComplete() {
        reportService = new ReportService(
                importService.getBooks(),
                importService.getStudents(),
                importService.getTransactions()
        );
        int books    = importService.getBooks().size();
        int students = importService.getStudents().size();
        int txns     = importService.getTransactions().size();
        lblStatus.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 11px;");
        lblStatus.setText(books + " books, " + students + " students, " + txns + " transactions loaded.");
    }

    private boolean checkImported() {
        if (reportService == null) {
            lblStatus.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 11px;");
            lblStatus.setText("Import files first.");
            return true;
        }
        return false;
    }

    private void loadScreen(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlName + ".fxml"));
            Node screen = loader.load();

            // Inject services into the child controller
            Object controller = loader.getController();
            if (controller instanceof ImportController ic) {
                ic.setMainController(this);
                ic.setImportService(importService);
            } else if (controller instanceof IssuedReportController irc) {
                irc.setReportService(reportService);
            } else if (controller instanceof AvgCostController ac) {
                ac.setReportService(reportService);
            } else if (controller instanceof AvailabilityController avc) {
                avc.setReportService(reportService);
            }

            contentArea.getChildren().setAll(screen);
        } catch (IOException e) {
            lblStatus.setText("Failed to load screen: " + fxmlName);
            e.printStackTrace();
        }
    }
}