package ph.edu.dlsu.lbycpa2.pcbuilderapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

class Component{
    String specs;
    Component left, right;

    public Component(String specs){
        this.specs = specs;
        this.left = null;
        this.right = null;
    }
}
class TreeCPU {
    Component rootCPU;

    TreeCPU() {
        rootCPU = null;
    }

    private Component recursive(Component current, String specs) {
        if (current == null) {
            return new Component(specs);
        }
        if (specs.compareTo(current.specs) < 0) {
            current.left = recursive(current.left, specs);
        } else if (specs.compareTo(current.specs) > 0) {
            current.right = recursive(current.right, specs);
        } else {
            return current;
        }
        return current;
    }

    public void addCPU(String specs) {
        rootCPU = recursive(rootCPU, specs);
    }
}

    public class PCBuilderController implements Initializable {
        String [] compatibleParts = new String[5];
        int stage = 0;
        @FXML
        private AnchorPane startPane, programPane;
        @FXML
        private Label partLabel;
        @FXML
        private ListView<String> partsList;
        @FXML
        private ListView<String> specsList;
        @FXML
        private ImageView partImage;
        @FXML
        private Button prevButton, nextButton;
        @FXML
        private void onStartClick() throws FileNotFoundException {
            startPane.setVisible(false);
            programPane.setVisible(true);
            ArrayList<String> list = new ArrayList<>(cpu());
            for(int i=0; i < list.size(); i++){
                String[] temp = list.get(i).split(",");
                partsList.getItems().add(temp[0]);
            }
        }
        @FXML
        public void onNextClick() throws FileNotFoundException {
            stage++;
            switch (stage) {
                case 0 -> {
                    prevButton.setDisable(true);
                    partLabel.setText("Central Processing Unit (CPU)");
                    cpu();
                }
                case 1 -> {
                    prevButton.setDisable(false);
                    partLabel.setText("Motherboard");
                }
                case 2 -> {
                    partLabel.setText("Random Access Memory (RAM)");
                }
                case 3 -> {
                    partLabel.setText("Case");
                }
                case 4 -> {
                    partLabel.setText("Graphics Processing Unit");
                }
                case 5 -> {
                    partLabel.setText("Power Supply");
                }
            }
        }

        @FXML
        public void onPrevClick() {
            stage--;
            switch (stage) {
                case 0 -> {
                    prevButton.setDisable(true);
                    partLabel.setText("Central Processing Unit (CPU)");
                }
                case 1 -> {
                    partLabel.setText("Motherboard");
                }
                case 2 -> {
                    partLabel.setText("Random Access Memory (RAM)");
                }
                case 3 -> {
                    partLabel.setText("Case");
                }
                case 4 -> {
                    partLabel.setText("Graphics Processing Unit");
                }
                case 5 -> {
                    partLabel.setText("Power Supply");
                }
            }
        }

        private ArrayList<String> cpu() throws FileNotFoundException {
            ArrayList<String> listCPU = new ArrayList<>();
            TreeCPU tCPU = new TreeCPU();
            Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2CPU.csv"));
            s.useDelimiter(",");
            while (s.hasNextLine()) {
                String line = s.nextLine();
                tCPU.addCPU(line);
                listCPU.add(line);
            }
            return listCPU;
        }
        private void getCompatibility(String selected){

        }
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            partsList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
                String current = String.valueOf(partsList.getSelectionModel().getSelectedItem());
                System.out.println(current);
            });

    }
    }


