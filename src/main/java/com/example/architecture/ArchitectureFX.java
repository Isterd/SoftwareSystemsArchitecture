package com.example.architecture;

import com.example.architecture.controllers.*;
import com.example.architecture.messages.EventResponse;
import com.example.architecture.messages.ExperimentResponse;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;

import java.io.IOException;

@Data
public class ArchitectureFX extends Application {

    private Stage primaryStage;
    private VBox rootLayout;
    private ExperimentResponse experimentResponse;
    private EventResponse eventResponse;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Simulation app");
        showBaseWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showBaseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArchitectureFX.class.getResource("/windows/helloWindow.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            HelloController controller = loader.getController();
            controller.setArchitectureFX(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createParametersWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArchitectureFX.class.getResource("/windows/paramsWindow.fxml"));
            AnchorPane page = loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Enter params:");
            ParamsController controller = loader.getController();
            controller.setArchitectureFX(this);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showResultWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArchitectureFX.class.getResource("/windows/autoWindow.fxml"));
            AutoController controller = new AutoController(this);
            loader.setController(controller);

            AnchorPane page = loader.load();
            controller.showResults();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);

            primaryStage.setTitle("Result");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showStepsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArchitectureFX.class.getResource("/windows/stepsWindow.fxml"));

            StepsController controller = new StepsController(this);

            loader.setController(controller);
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Steps");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setScene(new Scene(page));
            controller.setDialogStage(dialogStage);
            controller.showSteps();
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
