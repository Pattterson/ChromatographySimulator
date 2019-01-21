package com.chromasim.chromatographyhome;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.IOException;

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
    @FXML
    public TableView<Compound> compoundsTable;
    @FXML
    public Hyperlink hyperlink;


    public void initialize(){

        //note to self, should create helper class / static methods to automate initialization of tables
        populateDummyData();
        compoundsTable.setItems(compoundList);
        compoundNameColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("name"));
        compoundNumberColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("number"));

        smilesColumn.setCellValueFactory(new PropertyValueFactory<Compound, SimpleStringProperty>("smiles"));
        concentrationColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("concentration"));
        offsetColumn.setCellValueFactory(new PropertyValueFactory<Compound,String>("offset"));
//
        compoundsTable.setEditable(true);
        compoundNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        compoundNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        smilesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        concentrationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        offsetColumn.setCellFactory(TextFieldTableCell.forTableColumn());


    }


    public void addCompoundButton(ActionEvent actionEvent) {
        Compound compound = new Compound(new SimpleStringProperty(""));
        compound.setNumber(Integer.toString(compoundList.size()+1));
        compoundsTable.getItems().add(compound);

    }

    public void finishButtonClicked(ActionEvent actionEvent) {
        for(Compound compound: compoundList){
            System.out.println(compound.getSmiles());
        }
    }

    public void removeSelectedButtonPushed(ActionEvent actionEvent) {
        Compound compoundToRemove = compoundsTable.getSelectionModel().getSelectedItem();
        compoundList.remove(compoundToRemove);
        for (int i=0;i<compoundList.size(); i++){
            compoundList.get(i).setNumber(Integer.toString(i+1));
        }
    }

    private void populateDummyData(){
        compoundList.add(new Compound(new SimpleStringProperty("CO")));
        compoundList.add(new Compound(new SimpleStringProperty("CC1=CC=CC=C1")));
        compoundList.add(new Compound(new SimpleStringProperty("CC(C)(C)c1cc(C)cc(c1O)C(C)(C)C")));

        //Compounds in list are assigned integer in the order in which they are added
        for(int i=0; i<compoundList.size(); i++){
            compoundList.get(i).setNumber(Integer.toString(i+1));
        }

    }

    public void hyperlinkPressed(){
        Hyperlink hyperlink = new Hyperlink();
        hyperlink.setText("Molecular editor");
        openMolecularEditor();

        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openMolecularEditor();
            }
        });
    }



    public void editCommitted(TableColumn.CellEditEvent<Compound,String> event) {
        String newValue = event.getNewValue();
        compoundsTable.getSelectionModel().getSelectedItem().setSmiles(newValue);



        }








    private void openMolecularEditor() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MolecularEditor.fxml"));
        MolecularEditorController controller = new MolecularEditorController();
        loader.setController(controller);

        try {
            Scene MolecularEditorScene = new Scene(loader.load(),600,425);
            Stage window = new Stage();

            window.setScene(MolecularEditorScene);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to load the molecular editor");
        }
    }


    }





//
//        System.out.println("edit committed fired");
//        smilesColumn.up
//        Compound updatedCompound = compoundsTable.getSelectionModel().getSelectedItem();
//        System.out.println("new Smiles" + updatedCompound.getSmiles());
//        updatedCompound.setSmiles(new SimpleStringProperty(updatedCompound.getSmiles()));









