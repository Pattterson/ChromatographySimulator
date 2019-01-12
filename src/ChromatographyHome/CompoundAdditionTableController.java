package ChromatographyHome;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class CompoundAdditionTableController {
    ObservableList<Compound> compoundList = FXCollections.observableArrayList();


    public CompoundAdditionTableController(ObservableList<Compound> compoundList) {
        this.compoundList = compoundList;

    }

    @FXML
    public TableColumn compoundNameColumn;
    @FXML
    public TableColumn compoundNumberColumn;
    @FXML
    public TableColumn smilesColumn;
    @FXML
    public TableColumn concentrationColumn;
    @FXML
    public TableColumn offsetColumn;
    @FXML
    public Button addCompoundButton;
    @FXML
    public Button removeSelectedButton;
    @FXML
    public Button finishButton;
    public TableView<Compound> CompoundsTable;

    public void initialize(){

        //note to self, should create helper class / static methods to automate initialization of tables
        populateDummyData();
        CompoundsTable.setItems(compoundList);
        compoundNameColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("name"));
        compoundNumberColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("number"));
        smilesColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("smiles"));
        concentrationColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("concentration"));
        offsetColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("offset"));

        CompoundsTable.setEditable(true);
        compoundNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        compoundNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        smilesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        concentrationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        offsetColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }


    public void addCompoundButton(ActionEvent actionEvent) {
        System.out.println("test");
    }

    public void finishButtonClicked(ActionEvent actionEvent) {
    }

    public void removeSelectedButtonPushed(ActionEvent actionEvent) {
    }

    private void populateDummyData(){
        compoundList.add(new Compound("CHHHOH"));
        compoundList.add(new Compound("CCCHH"));
        compoundList.add(new Compound("CHCHHH"));
        compoundList.add(new Compound("CH3C3C"));
        System.out.println(compoundList);
    }


}
