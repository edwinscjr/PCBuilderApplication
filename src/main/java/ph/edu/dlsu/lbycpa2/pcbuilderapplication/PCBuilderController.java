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
import java.util.Stack;

public class PCBuilderController implements Initializable {
        public static String current;
        public Stack<String> selectedParts = new Stack<>();
        public static String[] compatibilityMotherboard = new String[5];
        public static int[] priceList;
        public static double buildTotalPrice;
        int stage = 0;
        @FXML
        private AnchorPane startPane, programPane;
        @FXML
        private Label partLabel, costLabel;
        @FXML
        public ListView<String> partsList;
        @FXML
        private ListView<String> specsList;
        @FXML
        private ImageView partImage;
        @FXML
        private Button prevButton, nextButton;
        ArrayList<String> allCPU;

        @FXML
        private void onStartClick() throws FileNotFoundException {
            setTitles();
            startPane.setVisible(false);
            programPane.setVisible(true);
            allCPU = new ArrayList<>(cpu());
            for (int i = 0; i < allCPU.size(); i++) {
                String[] temp = allCPU.get(i).split(",");
                partsList.getItems().add(temp[0]);
            }
            costLabel.setText(String.valueOf(buildTotalPrice));
        }

        @FXML
        public void onNextClick() throws FileNotFoundException {
            addCosts();
            selectedParts.push(current);
            defaultImage();
            clearListViews();
            if(stage <5) {
                stage++;
            }
            switch (stage) {
                case 0 -> {
                    prevButton.setDisable(true);
                    partLabel.setText("Central Processing Unit (CPU)");
                }
                case 1 -> {
                    prevButton.setDisable(false);
                    partLabel.setText("Motherboard");
                    initMobo();
                }
                case 2 -> {
                    partLabel.setText("Random Access Memory (RAM)");
                    clearListViews();
                    initRAM();
                }
                case 3 -> {
                    partLabel.setText("Case");
                    initCase();
                }
                case 4 -> {
                    partLabel.setText("Graphics Processing Unit");
                    initGPU();
                }
                case 5 -> {
                    partLabel.setText("Power Supply");
                }
            }
            getComponentCost(current, stage);
        }

        @FXML
        public void onPrevClick() throws FileNotFoundException {
//            selectedParts.pop();
            subtractPrevious();
            defaultImage();
            clearListViews();
            stage--;
            switch (stage) {
                case 0 -> {
                    prevButton.setDisable(true);
                    partLabel.setText("Central Processing Unit (CPU)");
                    onStartClick();
                }
                case 1 -> {
                    partLabel.setText("Motherboard");
                    initMobo();

                }
                case 2 -> {
                    partLabel.setText("Random Access Memory (RAM)");
                    initRAM();
                }
                case 3 -> {
                    partLabel.setText("Case");
                    initCase();
                }
                case 4 -> {
                    partLabel.setText("Graphics Processing Unit");
                    initGPU();
                }
                case 5 -> {
                    partLabel.setText("Power Supply");
                }
            }

        }

        private ArrayList<String> cpu() throws FileNotFoundException {
            ArrayList<String> listCPU = new ArrayList<>();
            Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2CPU.csv"));
            s.useDelimiter(",");
            while (s.hasNextLine()) {
                String line = s.nextLine();
                listCPU.add(line);
            }
            return listCPU;
        }

        ArrayList<String> allMobo;
        private void initMobo() throws FileNotFoundException {
            allMobo = new ArrayList<>(mobo(compatibilityMotherboard[0], compatibilityMotherboard[1], compatibilityMotherboard[2]));
            for (int i = 0; i < allMobo.size(); i++) {
                String[] specs = allMobo.get(i).split(",");
                partsList.getItems().add(specs[0]);
            }

        }
        ArrayList<String> allRAM;
        private void initRAM() throws FileNotFoundException {
            allRAM = new ArrayList<>(ram(compatibilityMotherboard[0], compatibleQuantity));
            for (int i = 0; i < allRAM.size(); i++) {
                String[] specs = allRAM.get(i).split(",");
                partsList.getItems().add(specs[0]);
            }
        }

        private ArrayList<String> mobo(String typeRAM, String typeSocket, String chipset) throws FileNotFoundException {
            ArrayList<String> listMobo = new ArrayList<>();
            Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2Motherboard.csv"));
            s.useDelimiter(",");
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] specs = line.split(",");
                String[] chipsets = specs[10].split("/");
                for (int i = 0; i < chipsets.length; i++) {
                    if (line.contains(typeRAM) && line.contains(typeSocket) && line.contains(chipsets[i])) {
                        listMobo.add(line);
                    }
                }
            }
            return listMobo;
        }

        private ArrayList<String> ram(String typeRAM, int quantity) throws FileNotFoundException {
            ArrayList<String> listRAM = new ArrayList<>();
            Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2Memory.csv"));
            s.useDelimiter(",");
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String [] specs = line.split(",");
                if (line.contains(typeRAM) && Integer.parseInt(specs[3]) <= quantity) {
                    listRAM.add(line);
                }
            }
            return listRAM;
        }

        private ArrayList<String> cases(String size) throws FileNotFoundException {
            ArrayList<String> listCases = new ArrayList<>();
            Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2Case.csv"));
            s.useDelimiter(",");
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.contains(size)) {
                    listCases.add(line);
                }
            }
            return listCases;
        }

        ArrayList<String> allCase;

        private void initCase() throws FileNotFoundException {
            allCase = new ArrayList<>(cases(compatibleCase));
            for (int i = 0; i < allCase.size(); i++) {
                String[] specs = allCase.get(i).split(",");
                partsList.getItems().add(specs[0]);
            }
        }

        private void getMotherboardCompatibility() {
            if (stage == 0) {
                String target;
                for (int i = 0; i < allCPU.size(); i++) {
                    if (allCPU.get(i).contains(current)) {
                        target = allCPU.get(i);
                        String[] specsSelectedCPU = target.split(",");
                        compatibilityMotherboard[0] = specsSelectedCPU[3]; // RAM
                        compatibilityMotherboard[1] = specsSelectedCPU[8]; // Socket
                        compatibilityMotherboard[2] = specsSelectedCPU[6]; // Chipset
                        compatibilityMotherboard[3] = specsSelectedCPU[7]; // PCIe
                        break;
                    }
                }
            }
        }

        String compatibleCase;
        int compatibleQuantity;
        private void getCaseAndRamCompatibility() {
            if (stage == 1) {
                String target;
                for (int i = 0; i < allMobo.size(); i++) {
                    if (allMobo.get(i).contains(current)) {
                        target = allMobo.get(i);
                        String[] specsSelectedMobo = target.split(",");
                        compatibleCase = specsSelectedMobo[1];
                        compatibleQuantity = Integer.parseInt(specsSelectedMobo[3]);
                    }
                }
            }
        }

        private double getComponentCost(String selected, int Stage) {
            double partCost = 0;
            ArrayList [] arrays = new ArrayList[] {allCPU, allMobo, allRAM, allCase,allGPU};
            if(stage < 5) {
                for (int i = 0; i < arrays[Stage].size(); i++) {
                    String[] specs = String.valueOf(arrays[Stage].get(i)).split(",");
                    if (specs[0].equals(selected)) {
                        partCost = Double.parseDouble(specs[(specs.length - 1)]);
                        break;
                    }
                }
            }
            return partCost;
        }
        private void subtractPrevious(){
            double sub = getComponentCost(selectedParts.pop(), stage-1);
            buildTotalPrice = buildTotalPrice - sub;
            costLabel.setText(String.format("%.2f", buildTotalPrice));

        }

        private void clearListViews() {
            partsList.getItems().clear();
            specsList.getItems().clear();
        }

        String[] cpuSpecsLabel = new String[13];
        String[] moboSpecsLabel = new String[11];
        String[] ramSpecsLabel = new String[7];
        String[] casesSpecsLabel = new String[6];
        String[] gpuSpecsLabel = new String[8];
        String[] psuSpecsLabel = new String[6];

    private ArrayList<String> psu(int tdp) throws FileNotFoundException {
        ArrayList<String> listGPU = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2PSU.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String [] specs = line.split(",");
            if(Integer.parseInt(specs[7])<= tdp) listGPU.add(line);
        }
        return listGPU;
    }

    private ArrayList<String> gpu(int maxLength) throws FileNotFoundException {
        ArrayList<String> listGPU = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2GPU.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String [] specs = line.split(",");
            if(Integer.parseInt(specs[7])<= maxLength) listGPU.add(line);
        }
        return listGPU;
    }

    ArrayList<String> allGPU;
    private void initGPU() throws FileNotFoundException{
        allGPU = new ArrayList<>(gpu(compatibleLengthGPU));
        for (int i = 0; i<allGPU.size(); i++){
            String[] specs = allGPU.get(i).split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    int compatibleLengthGPU = 0;
    private void getGPUCompatibility(){
        if (stage == 3) {
            String target;
            for (int i = 0; i < allCase.size(); i++) {
                if (allCase.get(i).contains(current)) {
                    target = allCase.get(i);
                    String[] specsSelectedCase = target.split(",");
                    compatibleLengthGPU = Integer.parseInt(specsSelectedCase[3]);
                }
            }
        }
    }

    private void getPSUCompatibility(){
        if(stage == 4){
            String target;

        }
    }
    private void setTitles(){
        cpuSpecsLabel = new String[]{"Processor:\t\t\t\t", "Actual Cores:\t\t\t\t", "Threads: \t\t\t\t\t", "Memory Type: \t\t\t", "Base Clock Frequency: \t\t", "Max Clock Frequency: \t\t", "Chipset: \t\t\t\t\t", "PCIe Generation: \t\t\t", "Socket: \t\t\t\t\t", "Total Power Draw: \t\t\t", "Integrated Graphics: \t\t", "CPU Cooler: \t\t\t\t", "Suggested Retail Price in U$: \t"};
        moboSpecsLabel = new String[]{"Motherboard:\t\t", "Size:\t\t\t\t", "Memory Type:\t\t", "RAM Slots:\t\t", "PCIe Gen: \t\t", "PCI-E x16 Slots: \t", "PCI-E x8 Slots: \t\t", "PCI-E x4 Slots: \t\t", "PCI-E x1 Slots: \t\t", "LAN: \t\t\t","Chipset:\t\t\t","Supported Socket:  ", "Price in USD: \t\t"};
        ramSpecsLabel = new String[]{"Name: \t\t\t","Memory Type:\t\t", "GB per Module:\t","Quantity:\t\t\t", "Memory Speed:\t", "Available Colors:\t", "Price in U$:\t\t"};
        casesSpecsLabel = new String[]{"Name:\t\t\t","Size:\t\t\t\t", "Color:\t\t\t", "Max GPU Length:\t", "Front I/O Ports:\t", "Price:\t\t\t"};
        gpuSpecsLabel = new String[]{"Name:\t\t\t","Chipset:\t\t\t","TDP:\t\t\t\t","Memory: \t\t\t" ,"Core Clock:\t\t", "Boost Clock:\t\t", "Color:\t\t\t", "Length:\t\t\t", "Price:\t\t\t"};
    }


    private void setSpecsList(String selected){
        ArrayList [] arrays = new ArrayList[] {allCPU, allMobo, allRAM, allCase,allGPU};
        String[][] strings = new String[][]{cpuSpecsLabel, moboSpecsLabel, ramSpecsLabel, casesSpecsLabel, gpuSpecsLabel};
        specsList.getItems().clear();
        ArrayList stageArray = arrays[stage];
        String specs = "";
        for(int i = 0; i<stageArray.size(); i++) {
            if (stageArray.get(i).toString().contains(selected)) specs = String.valueOf(stageArray.get(i));
        }
        String [] specsArray = specs.split(",");
        for(int i = 0; i < specsArray.length; i++ ) specsList.getItems().add(strings[0][i] + specsArray[i]);
    }
        double tempCost=0;
        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            partsList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
                double t = 0;
                current = String.valueOf(partsList.getSelectionModel().getSelectedItem());
                getMotherboardCompatibility();
                getCaseAndRamCompatibility();
                getGPUCompatibility();
                setSpecsList(current);
                setPartImage(current);
                tempCost = getComponentCost(current, stage);
                t = tempCost + buildTotalPrice;
                costLabel.setText(String.format("%.2f", t));

            });
        }
        private void addCosts(){
            buildTotalPrice = buildTotalPrice + tempCost;
            costLabel.setText(String.format("%.2f", buildTotalPrice));
        }
        private void defaultImage() {
            File img = new File("src/main/resources/assets/noImage.jpg");
            Image prodPic = new Image(img.toURI().toString());
            partImage.setImage(prodPic);
        }
        private void setPartImage(String selected) {
            defaultImage();
            File img = new File("src/main/resources/assets/", selected + ".jpg");
            Image prodPic = new Image(img.toURI().toString());
            partImage.setImage(prodPic);
        }
    }