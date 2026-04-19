package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import services.ReportService;

public class AvgCostController {

    @FXML private Label lblAvgCost;
    @FXML private Label lblBookCount;

    private ReportService reportService;

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @FXML
    private void calculate() {
        if (reportService == null) return;

        double avg = reportService.getAverageCost();
        int count  = reportService.getBooks().size();

        lblAvgCost.setText(String.format("$%.2f", avg));
        lblBookCount.setText("Based on " + count + " book(s) in the library.");
    }
}