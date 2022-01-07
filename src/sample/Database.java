package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static java.sql.DriverManager.getConnection;

public class Database {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String faculty){
        Parent root = null;

        if (username != null && faculty != null){
            try{
                FXMLLoader loader = new FXMLLoader(Database.class.getResource(fxmlFile));
                root = loader.load();
                LoginController loginController = loader.getController();
                loginController.setUserInfo(username, faculty);

            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(Database.class.getResource(fxmlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    public static void signUpUser(ActionEvent event, String username, String password, String faculty) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUser = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection("jdbc:mysql://localhost:3306/javafx_pr", "root", "123456");
            psCheckUser = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUser.setString(1, username);
            resultSet = psCheckUser.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO users (username, password, faculty) VALUES (?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, faculty);
                psInsert.executeUpdate();

                changeScene(event, "login.fxml", "Welcome", username, faculty);

            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUser != null) {
                try {
                    psCheckUser.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logInUser(ActionEvent event, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx_pr", "root", "123456");
            preparedStatement = connection.prepareStatement("SELECT password, faculty FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found in the database!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Records are incorrect!");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String extractPassword = resultSet.getString("password");
                    String extractFaculty = resultSet.getString("faculty");
                    if (extractPassword.equals(password)) {
                        changeScene(event, "login.fxml", "Welcome!", username, extractFaculty);
                    } else {
                        System.out.println("Passwords did not match");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Records are incorrect!");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void changeScene(ActionEvent event, String s, Object o, Object o1) {
    }

}
