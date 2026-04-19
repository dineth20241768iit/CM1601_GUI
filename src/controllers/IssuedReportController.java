package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ReportService;

import java.util.List;

public class IssuedReportController {

    @FXML private TextField txtDate;
    @FXML private Label lblStatus;
    @FXML private Label lblCount;
    @FXML private TableView<String[]> tblIssued;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colBookId;
    @FXML private TableColumn<String[], String> colTitle;
    @FXML private TableColumn<String[], String> colStudentId;
    @FXML private TableColumn<String[], String> colStudent;

    private ReportService reportService;

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(d      -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colBookId.setCellValueFactory(d    -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colTitle.setCellValueFactory(d     -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colStudentId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colStudent.setCellValueFactory(d   -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
    }

    @FXML
    private void searchIssued() {
        String date = txtDate.getText().trim();

        if (date.isEmpty()) {
            setStatus("Please enter a date.", false);
            return;
        }

        String dateError = model.Transaction.validateDate(date);
        if (dateError != null) {
            setStatus(dateError, false);
            return;
        }

        List<String[]> rows = reportService.getIssuedReportRows(date);
        tblIssued.setItems(FXCollections.observableArrayList(rows));

        if (rows.isEmpty()) {
            setStatus("No books issued on " + date + ".", false);
            lblCount.setText("");
        } else {
            setStatus("", true);
            lblCount.setText(rows.size() + " record(s) found for " + date + ".");
        }
    }

    private void setStatus(String message, boolean success) {
        lblStatus.setStyle(success
                ? "-fx-text-fill: #a6e3a1; -fx-font-size: 12px;"
                : "-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
        lblStatus.setText(message);
    }
}