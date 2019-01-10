package ChromatographyHome;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CompoundAdditionTableController {




    @FXML
    public TableColumn CompoundNameColumn;
    @FXML
    public TableColumn CompoundNumberColumn;
    @FXML
    public TableColumn SmilesColumn;
    @FXML
    public TableColumn ConcentrationColumn;
    @FXML
    public TableColumn OffsetColumn;
    @FXML
    public Button AddCompoundButton;
    @FXML
    public Button removeSelectedButton;
    @FXML
    public Button finishButton;
    public TableView CompoundsTable;

    public void initialize(){




    }


    public void addCompoundButton(ActionEvent actionEvent) {
    }

    public void finishButtonClicked(ActionEvent actionEvent) {
    }

    public void removeSelectedButtonPushed(ActionEvent actionEvent) {
    }
}
