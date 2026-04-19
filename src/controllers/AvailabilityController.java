package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.Book;
import services.ReportService;

import java.io.File;
import java.util.List;

public class AvailabilityController {

    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;
    @FXML private Label lblCount;
    @FXML private TableView<Book> tblResults;
    @FXML private TableColumn<Book, String> colBookId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colIsbn;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colCopies;
    @FXML private TableColumn<Book, Integer> colAvailability;
    @FXML private TableColumn<Book, String> colPrice;

    private ReportService reportService;
    private List<Book> currentResults;

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @FXML
    public void initialize() {
        colBookId.setCellValueFactory(d      -> new javafx.beans.property.SimpleStringProperty(d.getValue().getBookId()));
        colTitle.setCellValueFactory(d       -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        colIsbn.setCellValueFactory(d        -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIsbn()));
        colAuthor.setCellValueFactory(d      -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAuthor()));
        colCopies.setCellValueFactory(d      -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCopies()).asObject());
        colAvailability.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getAvailability()).asObject());
        colPrice.setCellValueFactory(d       -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", d.getValue().getPrice())));
    }

    @FXML
    private void search() {
        if (reportService == null) return;

        String query = txtSearch.getText().trim();
        currentResults = reportService.searchByTitle(query);
        tblResults.setItems(FXCollections.observableArrayList(currentResults));

        if (currentResults.isEmpty()) {
            setStatus("No books found matching '" + query + "'.", false);
            lblCount.setText("");
        } else {
            setStatus("", true);
            lblCount.setText(currentResults.size() + " result(s) found.");
        }
    }

    @FXML
    private void exportResults() {
        if (currentResults == null || currentResults.isEmpty()) {
            setStatus("No results to export. Run a search first.", false);
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Search Results");
        chooser.setInitialFileName("availability_results.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showSaveDialog(txtSearch.getScene().getWindow());

        if (file != null) {
            boolean success = reportService.exportSearchResults(currentResults, file.getAbsolutePath());
            setStatus(success
                    ? "Results exported to " + file.getName() + "."
                    : "Export failed.", success);
        }
    }

    private void setStatus(String message, boolean success) {
        lblStatus.setStyle(success
                ? "-fx-text-fill: #a6e3a1; -fx-font-size: 12px;"
                : "-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
        lblStatus.setText(message);
    }
}