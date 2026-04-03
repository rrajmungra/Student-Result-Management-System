import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.*;

public class StudentApp extends Application {

    // Database Credentials
    private final String url = "jdbc:mysql://localhost:3306/school_db";
    private final String user = "root";
    private final String password = ""; 
    private Stage primaryStage;

    // --- CLASS-LEVEL UI COMPONENTS (Fixes VS Code Lambda Errors) ---
    private TextField r, n, s1, s2, s3, s4, s5, s6;
    private TextArea log;

    // Modern UI Styles
    private final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-spacing: 20;";
    private final String FIELD_STYLE = "-fx-background-radius: 8; -fx-border-color: #ced4da; -fx-border-radius: 8; -fx-padding: 10; -fx-font-size: 14px;";
    private final String BTN_PRIMARY = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;";
    private final String HEADER_STYLE = "-fx-background-color: #343a40; -fx-padding: 15 25;";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showWelcomePage();
    }

    // --- 1. WELCOME PAGE ---
    public void showWelcomePage() {
        VBox card = new VBox(25);
        card.setStyle(CARD_STYLE);
        card.setAlignment(Pos.CENTER);
        card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.1)));

        Label title = new Label("ACADEMIC RESULT PORTAL");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnStudent = new Button("STUDENT PORTAL");
        btnStudent.setStyle(BTN_PRIMARY); btnStudent.setMinWidth(300);
        btnStudent.setOnAction(e -> showStudentSearchPage());

        Button btnAdmin = new Button("ADMIN LOGIN");
        btnAdmin.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8;");
        btnAdmin.setMinWidth(300);
        btnAdmin.setOnAction(e -> showLoginPage());

        card.getChildren().addAll(title, btnStudent, btnAdmin);
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #f8f9fa;");
        primaryStage.setScene(new Scene(root, 650, 550));
        primaryStage.setTitle("BHGCET");
        primaryStage.show();
    }

    // --- 2. STUDENT SEARCH PAGE ---
    public void showStudentSearchPage() {
        VBox card = new VBox(20);
        card.setStyle(CARD_STYLE);
        card.setMaxWidth(500);

        TextField rollInput = new TextField();
        rollInput.setPromptText("Enter Enrollment Number");
        rollInput.setStyle(FIELD_STYLE);

        Button btnSearch = new Button("VIEW RESULT");
        btnSearch.setStyle(BTN_PRIMARY);
        btnSearch.setMaxWidth(Double.MAX_VALUE);

        TextArea display = new TextArea("Results will appear here...");
        display.setEditable(false); display.setPrefHeight(250);
        display.setStyle("-fx-font-family: 'Consolas';");

        btnSearch.setOnAction(e -> {
            String sid = rollInput.getText().trim();
            new Thread(() -> {
                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement ps = conn.prepareStatement("SELECT * FROM results WHERE roll_no=?")) {
                    ps.setString(1, sid);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String res = String.format(
                            "Name: %s\nRoll: %s\n----------------------\nENS: %d | OOP: %d | DMGT: %d\nCOA: %d | OS:  %d | ADA:  %d\n----------------------\nTotal: %d/180 | Perc: %.2f%%",
                            rs.getString("name").toUpperCase(), rs.getString("roll_no"),
                            rs.getInt("ENS"), rs.getInt("OOP"), rs.getInt("DMGT"),
                            rs.getInt("COA"), rs.getInt("OS"), rs.getInt("ADA"),
                            rs.getInt("total"), rs.getDouble("percentage"));
                        Platform.runLater(() -> display.setText(res));
                    } else { Platform.runLater(() -> display.setText("Record Not Found!")); }
                } catch (Exception ex) { Platform.runLater(() -> display.setText("Database Error!")); }
            }).start();
        });

        Button btnBack = new Button("← Back");
        btnBack.setOnAction(e -> showWelcomePage());

        card.getChildren().addAll(new Label("STUDENT SEARCH"), rollInput, btnSearch, display, btnBack);
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: #e9ecef;");
        primaryStage.setScene(new Scene(root, 600, 600));
    }

    // --- 3. ADMIN LOGIN ---
    public void showLoginPage() {
        VBox card = new VBox(15); card.setAlignment(Pos.CENTER);
        card.setStyle(CARD_STYLE); card.setMaxWidth(400);
        TextField u = new TextField(); u.setPromptText("Username"); u.setStyle(FIELD_STYLE);
        PasswordField p = new PasswordField(); p.setPromptText("Password"); p.setStyle(FIELD_STYLE);
        Button login = new Button("LOGIN"); login.setStyle(BTN_PRIMARY);
        login.setOnAction(e -> { if(u.getText().equals("admin") && p.getText().equals("admin")) showDashboard(); });
        Button back = new Button("Cancel"); back.setOnAction(e -> showWelcomePage());
        card.getChildren().addAll(new Label("ADMIN ACCESS"), u, p, login, back);
        StackPane root = new StackPane(card); root.setStyle("-fx-background-color: #dee2e6;");
        primaryStage.setScene(new Scene(root, 500, 500));
    }

    // --- 4. ADMIN DASHBOARD ---
    public void showDashboard() {
        Label headLabel = new Label("ADMIN CONTROL PANEL");
        headLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Button btnOut = new Button("LOGOUT"); btnOut.setOnAction(e -> showWelcomePage());
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(headLabel, spacer, btnOut); header.setStyle(HEADER_STYLE);

        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(15); grid.setPadding(new Insets(20));
        
        r = new TextField(); r.setPromptText("Roll No"); r.setStyle(FIELD_STYLE);
        n = new TextField(); n.setPromptText("Name"); r.setStyle(FIELD_STYLE);
        s1 = new TextField(); s1.setPromptText("ENS"); s2 = new TextField(); s2.setPromptText("OOP");
        s3 = new TextField(); s3.setPromptText("DMGT"); s4 = new TextField(); s4.setPromptText("COA");
        s5 = new TextField(); s5.setPromptText("OS"); s6 = new TextField(); s6.setPromptText("ADA");

        grid.add(new Label("Roll:"), 0, 0); grid.add(r, 1, 0);
        grid.add(new Label("Name:"), 2, 0); grid.add(n, 3, 0);
        grid.add(new Label("ENS:"), 0, 1); grid.add(s1, 1, 1);
        grid.add(new Label("OOP:"), 2, 1); grid.add(s2, 3, 1);
        grid.add(new Label("DMGT:"), 0, 2); grid.add(s3, 1, 2);
        grid.add(new Label("COA:"), 2, 2); grid.add(s4, 3, 2);
        grid.add(new Label("OS:"), 0, 3); grid.add(s5, 1, 3);
        grid.add(new Label("ADA:"), 2, 3); grid.add(s6, 3, 3);

        Button btnSrc = new Button("SEARCH"); Button btnAdd = new Button("INSERT");
        Button btnUpd = new Button("UPDATE"); Button btnClr = new Button("CLEAR");
        log = new TextArea("Ready..."); log.setPrefHeight(100);

    btnSrc.setOnAction(e -> {
    // Capture the search ID on the UI thread to avoid "effectively final" issues
    String searchId = r.getText().trim(); 
    if (searchId.isEmpty()) {
        log.setText("Error: Enter Roll Number");
        return;
    }

    new Thread(() -> {
        // Use a clean try-with-resources for Connection and PreparedStatement
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM results WHERE roll_no=?")) {
            
            ps.setString(1, searchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Extract data while on the background thread
                    String studentName = rs.getString("name");
                    String m1 = rs.getString("ENS");
                    String m2 = rs.getString("OOP");
                    String m3 = rs.getString("DMGT");
                    String m4 = rs.getString("COA");
                    String m5 = rs.getString("OS");
                    String m6 = rs.getString("ADA");

                    // Update the UI safely on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        n.setText(studentName);
                        s1.setText(m1);
                        s2.setText(m2);
                        s3.setText(m3);
                        s4.setText(m4);
                        s5.setText(m5);
                        s6.setText(m6);
                        log.setText("Loaded: " + studentName);
                    });
                } else {
                    Platform.runLater(() -> log.setText("Roll No Not Found."));
                }
            }
        } catch (Exception ex) {
            Platform.runLater(() -> log.setText("DB Error: " + ex.getMessage()));
            ex.printStackTrace(); // View actual error in VS Code console
        }
    }).start();
});

        btnAdd.setOnAction(e -> {
            int tot = sumMarks(s1, s2, s3, s4, s5, s6);
            executeSQL("INSERT INTO results VALUES(?,?,?,?,?,?,?,?,?,?)", r.getText(), n.getText(), s1.getText(), s2.getText(), s3.getText(), s4.getText(), s5.getText(), s6.getText(), String.valueOf(tot), String.valueOf(tot/1.8));
        });

        btnUpd.setOnAction(e -> {
            int tot = sumMarks(s1, s2, s3, s4, s5, s6);
            executeSQL("UPDATE results SET name=?, ENS=?, OOP=?, DMGT=?, COA=?, OS=?, ADA=?, total=?, percentage=? WHERE roll_no=?", n.getText(), s1.getText(), s2.getText(), s3.getText(), s4.getText(), s5.getText(), s6.getText(), String.valueOf(tot), String.valueOf(tot/1.8), r.getText());
        });

        btnClr.setOnAction(e -> { r.clear(); n.clear(); s1.clear(); s2.clear(); s3.clear(); s4.clear(); s5.clear(); s6.clear(); });

        VBox body = new VBox(15, grid, new HBox(10, btnSrc, btnAdd, btnUpd, btnClr), log);
        body.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(new VBox(header, body), 800, 700));
    }

    private int sumMarks(TextField... f) {
        int t = 0;
        for(TextField x : f) t += Integer.parseInt(x.getText().isEmpty() ? "0" : x.getText());
        return t;
    }

    private void executeSQL(String sql, String... p) {
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                PreparedStatement ps = conn.prepareStatement(sql);
                for(int i=0; i<p.length; i++) ps.setString(i+1, p[i]);
                int res = ps.executeUpdate();
                Platform.runLater(() -> log.setText(res > 0 ? "Success!" : "Failed."));
            } catch (Exception ex) { Platform.runLater(() -> log.setText("SQL Error!")); }
        }).start();
    }

    public static void main(String[] args) { launch(args); }
}