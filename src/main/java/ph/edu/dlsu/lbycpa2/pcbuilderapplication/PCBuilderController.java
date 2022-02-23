package ph.edu.dlsu.lbycpa2.pcbuilderapplication;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.*;

public class PCBuilderController implements Initializable {
    String current;
    Stack<String> selectedParts = new Stack<>();
    static String[] compatibilityMotherboard = new String[8];
    Stack<Double> priceList = new Stack<>();
    double buildTotalPrice;
    double tempCost = 0;
    int stage = 0;
    @FXML
    public AnchorPane startPane, programPane, endPane, auxPane;
    @FXML
    public Label partLabel, costLabel, cpuLabel,mbLabel,  ramLabel, caseLabel, gpuLabel, psuLabel, coolerLabel, storageLabel;
    @FXML
    public Label cpuPrice,mbPrice, ramPrice, casePrice, gpuPrice, psuPrice, coolerPrice, storagePrice, totalLabel;
    @FXML
    public Label cpuPHP,mbPHP, ramPHP, casePHP, gpuPHP, psuPHP, coolerPHP, storagePHP, totalPHP;
    @FXML
    private ListView<String> partsList, specsList;
    @FXML
    private ImageView partImage, cpuImage,mbImage, ramImage, caseImage, gpuImage, psuImage, coolerImage, storageImage;
    @FXML
    private Button prevButton, nextButton;
    ArrayList<String> allCPU;
    ArrayList<String> allMobo;
    ArrayList<String> allRAM;
    ArrayList<String> allCase;
    ArrayList<String> allGPU;
    ArrayList<String> allPSU;
    ArrayList<String> allCooler;
    ArrayList<String> allStorage;
    int[] tdp = new int[]{0, 0, 150}; //CPU, GPU, Headroom
    String compatibleCase;
    int compatibleQuantity;
    String iGLine;
    String[] cpuSpecsLabel = new String[13];
    String[] moboSpecsLabel = new String[12];
    String[] ramSpecsLabel = new String[7];
    String[] casesSpecsLabel = new String[6];
    String[] gpuSpecsLabel = new String[8];
    String[] psuSpecsLabel = new String[7];
    String[] coolerSpecsLabel = new String[6];
    String[] storageSpecsLabel = new String[8];

    int compatibleLengthGPU = 0;
    @FXML
    private void onStartClick() throws FileNotFoundException {
        stage = 0;
        setTitles();
        startPane.setVisible(false);
        programPane.setVisible(true);
        allCPU = new ArrayList<>(cpu());
        for (String s : allCPU) {
            String[] temp = s.split(",");
            partsList.getItems().add(temp[0]);
        }costLabel.setText(String.valueOf(buildTotalPrice));
    }

    @FXML
    public void onNextClick() throws IOException, InvalidFormatException {
        if (partsList.getSelectionModel().getSelectedIndex() != -1) {
            addCosts();
            selectedParts.push(current);
            defaultImage();
            clearListViews();
            stage++;
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
                    initPSU();
                }
                case 6 ->{
                    partLabel.setText("Cooler");
                    initCooler();
                }
                case 7 ->{
                    partLabel.setText("Storage");
                    initStorage();
                }
                case 8 -> {
                    int i = 0;
                    partLabel.setText("Review of Selected Parts");
                    endPane.setVisible(true);
                    auxPane.setVisible(true);
                    programPane.setVisible(false);
                    Label [] partsLabel = new Label[]{storageLabel,coolerLabel,psuLabel, gpuLabel, caseLabel, ramLabel, mbLabel, cpuLabel};
                    Label [] costsLabel = new Label[]{storagePrice,coolerPrice,psuPrice, gpuPrice, casePrice, ramPrice, mbPrice, cpuPrice};
                    Label [] costsPHPLabel = new Label[]{storagePHP,coolerPHP,psuPHP, gpuPHP, casePHP, ramPHP, mbPHP, cpuPHP};
                    ImageView[] images = new ImageView[]{storageImage, coolerImage, psuImage,gpuImage,caseImage,ramImage,mbImage,cpuImage};
                    totalLabel.setText("$"+String.format("%.2f", buildTotalPrice));
                    totalPHP.setText("₱"+String.format("%.2f", buildTotalPrice*51.29));
                    while (!selectedParts.empty()){
                        double costUSD = priceList.pop();
                        String currentPart = selectedParts.pop();
                        File image = new File("src/main/resources/assets/", currentPart + ".jpg");
                        Image pic = new Image(image.toURI().toString());
                        images[i].setImage(pic);
                        partsLabel[i].setText(currentPart);
                        costsLabel[i].setText("$"+String.format("%.2f",costUSD));
                        costsPHPLabel[i].setText("₱"+String.format("%.2f",costUSD*51.29));
                        i++;
                    }
                    WritableImage image = endPane.snapshot(new SnapshotParameters(), null);
                    File file = new File("src/main/resources/output.png");
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    }catch (IOException e) {
                        // TODO: handle exception here
                    }

                    XWPFDocument document = new XWPFDocument();
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    FileOutputStream fout = new FileOutputStream("src/main/resources/parts.docx");
                    File img = new File("src/main/resources/output.png");
                    FileInputStream imageData = new FileInputStream(img);
                    int imageType = XWPFDocument.PICTURE_TYPE_PNG;
                    String imageFileName = img.getName();
                    int width = 450;
                    int height = 400;
                    run.addPicture(imageData, imageType, imageFileName, Units.toEMU(width), Units.toEMU(height));
                    document.write(fout);
                    fout.close();
                    document.close();
                }
            }
        }
    }
    @FXML
    private void onStartOverClick() throws FileNotFoundException {
        buildTotalPrice = 0;
        auxPane.setVisible(false);
        partLabel.setText("Central Processing Unit (CPU)");
        endPane.setVisible(false);
        onStartClick();
    }
    @FXML
    public void onPrevClick() throws FileNotFoundException {
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
                initPSU();
            }
            case 6 -> {
                partLabel.setText("Cooler");
                initCooler();
            }
            case 7 -> {
                partLabel.setText("Storage");
                initStorage();
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
    private ArrayList<String> mobo(String typeRAM, String typeSocket, String chipset) throws FileNotFoundException {
        ArrayList<String> listMobo = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2Motherboard.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] chips = chipset.split("/");
            String[] ram = typeRAM.split("/");
            if(chips.length>1){
                for (String chip : chips) {
                    if (line.contains(typeRAM) && line.contains(typeSocket) && line.contains(chip)) {
                        listMobo.add(line);
                    }
                }
            }
            if(ram.length>1) {
                for (String r : ram) {
                    if (line.contains(typeSocket) && line.contains(r) && !listMobo.contains(line)) {
                        listMobo.add(line);
                    }
                }
            }
            else if (line.contains(typeRAM) && line.contains(typeSocket) && line.contains(chipset)&&!listMobo.contains(line)) {
                listMobo.add(line);
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
            String[] specs = line.split(",");
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
    private ArrayList<String> gpu(int maxLength) throws FileNotFoundException {
        iGLine = "Integrated Graphics,"+compatibilityMotherboard[4]+",0,N/A,N/A,N/A,N/A,N/A,0.00";
        ArrayList<String> listGPU = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2GPU.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] specs = line.split(",");
            if (Integer.parseInt(specs[7]) <= maxLength) listGPU.add(line);
        }
        if(!compatibilityMotherboard[4].equals("None")) listGPU.add(iGLine);
        return listGPU;
    }
    private ArrayList<String> psu(int cpuTDP, int gpuTDP, int headroomTDP) throws FileNotFoundException {
        ArrayList<String> listPSU = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2PSU.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] specs = line.split(",");
            if (Integer.parseInt(specs[2]) >= (cpuTDP + gpuTDP + headroomTDP)) listPSU.add(line);
        }
        return listPSU;
    }
    private ArrayList<String> cooler(String included) throws FileNotFoundException {
        ArrayList<String> listCooler = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2Cooler.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] specs = line.split(",");
            int length = Integer.parseInt(specs[4]);
            if(length < compatibleLengthGPU) listCooler.add(line);
        }
        if(included.equals("Yes"))listCooler.add("Included Cooler,N/A,N/A,N/A,0,0.00");
        return listCooler;
    }
    private ArrayList<String> storage(String compatible) throws FileNotFoundException {
        ArrayList<String> listStorage = new ArrayList<>();
        Scanner s = new Scanner(new File("src/main/resources/ph/edu/dlsu/lbycpa2/pcbuilderapplication/CPA2Storage.csv"));
        s.useDelimiter(",");
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] specs = line.split(",");
            if(compatible.equals("Yes")){
                if(specs[5].equals("M.2-2280")) listStorage.add(line);
            }
            if(specs[5].equals("2.5")||specs[5].equals("3.5"))listStorage.add(line);
        }
        return listStorage;
    }
    private void initMobo() throws FileNotFoundException {
        allMobo = new ArrayList<>(mobo(compatibilityMotherboard[0], compatibilityMotherboard[1], compatibilityMotherboard[2]));
        for (String s : allMobo) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void initRAM() throws FileNotFoundException {
        allRAM = new ArrayList<>(ram(moboRam, compatibleQuantity));
        for (String s : allRAM) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void initCase() throws FileNotFoundException {
        allCase = new ArrayList<>(cases(compatibleCase));
        for (String s : allCase) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void initGPU() throws FileNotFoundException {
        allGPU = new ArrayList<>(gpu(compatibleLengthGPU));
        for (String s : allGPU) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void initPSU() throws FileNotFoundException {
        allPSU = new ArrayList<>(psu(tdp[0], tdp[1], tdp[2]));
        for (String s : allPSU) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void initCooler() throws FileNotFoundException{
        allCooler = new ArrayList<>(cooler(compatibilityMotherboard[5]));
        for (String s : allCooler) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void initStorage() throws FileNotFoundException{
        allStorage = new ArrayList<>(storage(m2Compatible));
        for (String s : allStorage) {
            String[] specs = s.split(",");
            partsList.getItems().add(specs[0]);
        }
    }
    private void getMotherboardCompatibility() {
        if (stage == 0) {
            String target;
            for (String s : allCPU) {
                if (s.contains(current)) {
                    target = s;
                    String[] specsSelectedCPU = target.split(",");
                    compatibilityMotherboard[0] = specsSelectedCPU[3]; // RAM
                    compatibilityMotherboard[1] = specsSelectedCPU[8]; // Socket
                    compatibilityMotherboard[2] = specsSelectedCPU[6]; // Chipset
                    compatibilityMotherboard[3] = specsSelectedCPU[7]; // PCIe
                    compatibilityMotherboard[4] = specsSelectedCPU[10]; // Integrated Graphics
                    compatibilityMotherboard[5] = specsSelectedCPU[11]; // Cooler
                    tdp[0] = Integer.parseInt(specsSelectedCPU[9]); // TDP
                    break;
                }
            }
        }
    }
    String m2Compatible;
    String moboRam;
    private void getCaseAndRamCompatibility() {
        if (stage == 1) {
            String target;
            for (String s : allMobo) {
                if (s.contains(current)) {
                    target = s;
                    String[] specsSelectedMobo = target.split(",");
                    compatibleCase = specsSelectedMobo[1]; // Size
                    moboRam = specsSelectedMobo[2]; //Motherboard RAM
                    compatibleQuantity = Integer.parseInt(specsSelectedMobo[3]);// Quantity
                    m2Compatible = specsSelectedMobo[4]; //M.2
                }
            }
        }
    }

    private double getComponentCost(String selected, int Stage) {
        double partCost = 0;
        ArrayList[] arrays = new ArrayList[]{allCPU, allMobo, allRAM, allCase, allGPU, allPSU, allCooler, allStorage};
        for (int i = 0; i < arrays[Stage].size(); i++) {
            String[] specs = String.valueOf(arrays[Stage].get(i)).split(",");
            if (specs[0].equals(selected)) {
                partCost = Double.parseDouble(specs[(specs.length - 1)]);
                break;
            }
        }
        return partCost;
    }

    private void subtractPrevious() {
        double sub = getComponentCost(selectedParts.pop(), stage - 1);
        buildTotalPrice = buildTotalPrice - sub;
        costLabel.setText(String.format("%.2f", buildTotalPrice));

    }
    private void clearListViews() {
        partsList.getItems().clear();
        specsList.getItems().clear();
    }

    private void getGPUCompatibility() {
        if (stage == 3) {
            String target;
            for (String s : allCase) {
                if (s.contains(current)) {
                    target = s;
                    String[] specsSelectedCase = target.split(",");
                    compatibleLengthGPU = Integer.parseInt(specsSelectedCase[3]);
                }
            }
        }
    }

    private void getPSUCompatibility() {
        if (stage == 4) {
            String target;
            for (String s : allGPU) {
                if (s.contains(current)) {
                    target = s;
                    String[] specsSelectedGPU = target.split(",");
                    tdp[1] = Integer.parseInt(specsSelectedGPU[2]);
                }
            }

        }
    }
    private void setTitles() {
        cpuSpecsLabel = new String[]{"Processor:\t\t\t\t", "Actual Cores:\t\t\t\t", "Threads: \t\t\t\t\t", "Memory Type: \t\t\t", "Base Clock Frequency: \t\t", "Max Clock Frequency: \t\t", "Chipset: \t\t\t\t\t", "PCIe Generation: \t\t\t", "Socket: \t\t\t\t\t", "Total Power Draw: \t\t\t", "Integrated Graphics: \t\t", "CPU Cooler: \t\t\t\t", "Suggested Retail Price in U$: \t"};
        moboSpecsLabel = new String[]{"Motherboard:\t\t", "Size:\t\t\t\t", "Memory Type:\t\t", "RAM Slots:\t\t","M.2 Compatible:\t", "PCIe Gen: \t\t", "PCI-E x16 Slots: \t", "PCI-E x8 Slots: \t\t", "PCI-E x4 Slots: \t\t", "PCI-E x1 Slots: \t\t", "LAN: \t\t\t", "Chipset:\t\t\t", "Supported Socket:  ", "Price in USD: \t\t"};
        ramSpecsLabel = new String[]{"Name: \t\t\t", "Memory Type:\t\t", "GB per Module:\t", "Quantity:\t\t\t", "Memory Speed:\t", "Available Colors:\t", "Price in U$:\t\t"};
        casesSpecsLabel = new String[]{"Name:\t\t\t", "Size:\t\t\t\t", "Color:\t\t\t", "Max GPU Length:\t", "Front I/O Ports:\t", "Price:\t\t\t"};
        gpuSpecsLabel = new String[]{"Name:\t\t\t", "Chipset:\t\t\t", "TDP:\t\t\t\t", "Memory: \t\t\t", "Core Clock:\t\t", "Boost Clock:\t\t", "Color:\t\t\t", "Length:\t\t\t", "Price:\t\t\t"};
        psuSpecsLabel = new String[]{"Name:\t\t\t\t", "Form Factor:\t\t\t", "Continuous Power (W):\t","Certification:\t\t\t", "Available Color/s:\t\t", "Modularity:\t\t\t", "Price in U$:\t\t\t"};
        coolerSpecsLabel = new String[]{"Name:\t\t\t", "Rated RPM:\t\t", "Noise Level:\t\t", "Available Colors:\t", "Length (Radiator):\t", "Price:\t\t\t"};
        storageSpecsLabel = new String[]{"Name:\t\t", "Capacity:\t\t", "Price/GB:\t\t","Type:\t\t","Cache:\t\t", "Form Factor:\t", "Interface:\t\t", "Price:\t\t"};
    }
    private void setSpecsList(String selected) {
        ArrayList[] arrays = new ArrayList[]{allCPU, allMobo, allRAM, allCase, allGPU, allPSU, allCooler, allStorage};
        String[][] strings = new String[][]{cpuSpecsLabel, moboSpecsLabel, ramSpecsLabel, casesSpecsLabel, gpuSpecsLabel, psuSpecsLabel, coolerSpecsLabel, storageSpecsLabel};
        specsList.getItems().clear();
        ArrayList stageArray = arrays[stage];
        String specs = "";
        for (Object o : stageArray) if (o.toString().contains(selected)) specs = String.valueOf(o);
        String[] specsArray = specs.split(",");
        String[] labelArray = strings[stage];
        for (int i = 0; i < specsArray.length; i++)
            specsList.getItems().add(labelArray[i] + specsArray[i]);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        partsList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            double t;
            current = String.valueOf(partsList.getSelectionModel().getSelectedItem());
            setPartImage(current);
            getMotherboardCompatibility();
            getCaseAndRamCompatibility();
            getGPUCompatibility();
            getPSUCompatibility();
            setSpecsList(current);
            tempCost = getComponentCost(current, stage);
            t = tempCost + buildTotalPrice;
            costLabel.setText(String.format("%.2f", t));

        });
    }

    private void addCosts() {
        buildTotalPrice = buildTotalPrice + tempCost;
        priceList.push(tempCost);
        costLabel.setText(String.format("%.2f", buildTotalPrice));
    }

    private void defaultImage() {
        File img = new File("src/main/resources/assets/noImage.jpg");
        Image prodPic = new Image(img.toURI().toString());
        partImage.setImage(prodPic);
    }

    private void setPartImage(String selected)  {

        File file = new File("src/main/resources/assets/" , selected + ".jpg");
        Image prodPic = new Image(file.toURI().toString());
        partImage.setImage(prodPic);
    }
}