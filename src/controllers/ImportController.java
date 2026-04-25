package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import services.FileImportService;

import java.io.File;

public class ImportController {

    @FXML private TextField txtBookPath;
    @FXML private TextField txtStudentPath;
    @FXML private TextField txtTransactionPath;
    @FXML private Label lblImportStatus;

    // Book error table
    @FXML private TableView<String[]>  tblBookErrors;
    @FXML private TableColumn<String[], String> colBookLine;
    @FXML private TableColumn<String[], String> colBookRaw;
    @FXML private TableColumn<String[], String> colBookError;
    @FXML private TextField txtBookCorrection;

    // Student error table
    @FXML private TableView<String[]>  tblStudentErrors;
    @FXML private TableColumn<String[], String> colStudentLine;
    @FXML private TableColumn<String[], String> colStudentRaw;
    @FXML private TableColumn<String[], String> colStudentError;
    @FXML private TextField txtStudentCorrection;

    // Transaction error table
    @FXML private TableView<String[]>  tblTransactionErrors;
    @FXML private TableColumn<String[], String> colTransactionLine;
    @FXML private TableColumn<String[], String> colTransactionRaw;
    @FXML private TableColumn<String[], String> colTransactionError;
    @FXML private TextField txtTransactionCorrection;

    private FileImportService importService;
    private MainController mainController;

    // ── Setters injected by MainController ───────────────────────────
    public void setImportService(FileImportService importService) {
        this.importService = importService;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("GrazieInspectionRunner")
    @FXML
    public void initialize() {
        // Wire table columns to String[] indices
        colBookLine.setCellValueFactory(d  -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colBookRaw.setCellValueFactory(d   -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colBookError.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        colStudentLine.setCellValueFactory(d  -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colStudentRaw.setCellValueFactory(d   -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colStudentError.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        colTransactionLine.setCellValueFactory(d  -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colTransactionRaw.setCellValueFactory(d   -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colTransactionError.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        // Auto-populate correction field when a row is selected
        tblBookErrors.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> { if (selected != null) txtBookCorrection.setText(selected[1]); });
        tblStudentErrors.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> { if (selected != null) txtStudentCorrection.setText(selected[1]); });
        tblTransactionErrors.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> { if (selected != null) txtTransactionCorrection.setText(selected[1]); });
    }

    // ── Browse buttons ────────────────────────────────────────────────

    @FXML
    private void browseBooks() {
        File f = openFileChooser("Select book.csv");
        if (f != null) txtBookPath.setText(f.getAbsolutePath());
    }

    @FXML
    private void browseStudents() {
        File f = openFileChooser("Select student.csv");
        if (f != null) txtStudentPath.setText(f.getAbsolutePath());
    }

    @FXML
    private void browseTransactions() {
        File f = openFileChooser("Select transactions.csv");
        if (f != null) txtTransactionPath.setText(f.getAbsolutePath());
    }

    private File openFileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        return chooser.showOpenDialog(txtBookPath.getScene().getWindow());
    }

    // ── Import ────────────────────────────────────────────────────────

    @FXML
    private void importAll() {
        String bookPath    = txtBookPath.getText().trim();
        String studentPath = txtStudentPath.getText().trim();
        String txnPath     = txtTransactionPath.getText().trim();

        if (bookPath.isEmpty() || studentPath.isEmpty() || txnPath.isEmpty()) {
            setStatus("Please select all three CSV files before importing.", false);
            return;
        }

        importService.importAll(bookPath, studentPath, txnPath);
        refreshErrorTables();

        int books    = importService.getBooks().size();
        int students = importService.getStudents().size();
        int txns     = importService.getTransactions().size();
        int errors   = importService.getBookErrors().size()
                     + importService.getStudentErrors().size()
                     + importService.getTransactionErrors().size();

        if (errors == 0) {
            setStatus("Import successful. " + books + " books, " + students + " students, " + txns + " transactions.", true);
            mainController.onImportComplete();
        } else {
            setStatus("Import complete with " + errors + " error(s). Correct them below.", false);
            mainController.onImportComplete();
        }
    }

    // ── Fix error rows ────────────────────────────────────────────────

    @FXML
    private void fixBookError() {
        int index = tblBookErrors.getSelectionModel().getSelectedIndex();
        if (index < 0) { setStatus("Select a book error row first.", false); return; }
        String corrected = txtBookCorrection.getText().trim();
        if (corrected.isEmpty()) { setStatus("Enter the corrected CSV row.", false); return; }
        importService.correctBookError(index, corrected);
        refreshErrorTables();
        txtBookCorrection.clear();
        mainController.onImportComplete();
    }

    @FXML
    private void fixStudentError() {
        int index = tblStudentErrors.getSelectionModel().getSelectedIndex();
        if (index < 0) { setStatus("Select a student error row first.", false); return; }
        String corrected = txtStudentCorrection.getText().trim();
        if (corrected.isEmpty()) { setStatus("Enter the corrected CSV row.", false); return; }
        importService.correctStudentError(index, corrected);
        refreshErrorTables();
        txtStudentCorrection.clear();
        mainController.onImportComplete();
    }

    @FXML
    private void fixTransactionError() {
        int index = tblTransactionErrors.getSelectionModel().getSelectedIndex();
        if (index < 0) { setStatus("Select a transaction error row first.", false); return; }
        String corrected = txtTransactionCorrection.getText().trim();
        if (corrected.isEmpty()) { setStatus("Enter the corrected CSV row.", false); return; }
        importService.correctTransactionError(index, corrected);
        refreshErrorTables();
        txtTransactionCorrection.clear();
        mainController.onImportComplete();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void refreshErrorTables() {
        tblBookErrors.setItems(FXCollections.observableArrayList(importService.getBookErrors()));
        tblStudentErrors.setItems(FXCollections.observableArrayList(importService.getStudentErrors()));
        tblTransactionErrors.setItems(FXCollections.observableArrayList(importService.getTransactionErrors()));
    }

    private void setStatus(String message, boolean success) {
        lblImportStatus.setStyle(success
                ? "-fx-text-fill: #a6e3a1; -fx-font-size: 12px;"
                : "-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
        lblImportStatus.setText(message);
    }
}