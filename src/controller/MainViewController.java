package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.Station;
import view.AlertFactory;
import view.CustomAlert;

public class MainViewController {
	
	private AlertFactory alertFactory;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane backgroundAnchor;

    @FXML
    private TextField trainPassengerCapTextField;

    @FXML
    private TextField passengerCountTextField;

    @FXML
    private ComboBox<String> passengerSourceDropdown;

    @FXML
    private ComboBox<String> passengerDestinationDropdown;
    
    @FXML
    private Button spawnTrainButton;

    @FXML
    private Button spawnPassengerButton;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private AnchorPane statusTabBackgroundAnchor;
    
    @FXML
    private AnchorPane spawnTabBackgroundAnchor;
    
    @FXML
    private AnchorPane trainViewAnchor;
    
    @FXML
    private ScrollPane trainStatusScrollPane;

    @FXML
    private ScrollPane stationStatusScrollPane;
    
    @FXML
    private Button stopSimulationButton;
    
    @FXML
    private GridPane trainStatusGridPane;
    
    @FXML
    private GridPane stationStatusGridPane;
    
    private Label[] passengersWaitingLabel = new Label[8];
    
    /* NON-GUI ATTRIBS */
    
    private Station[] stationArray = new Station[8];
	private int x, trainsSpawned;


    @FXML
    void initialize() {
        assert backgroundAnchor != null : "fx:id=\"backgroundAnchor\" was not injected: check your FXML file 'MainView.fxml'.";
        assert trainPassengerCapTextField != null : "fx:id=\"trainPassengerCapTextField\" was not injected: check your FXML file 'MainView.fxml'.";
        assert passengerCountTextField != null : "fx:id=\"passengerCountTextField\" was not injected: check your FXML file 'MainView.fxml'.";
        assert passengerSourceDropdown != null : "fx:id=\"passengerSourceDropdown\" was not injected: check your FXML file 'MainView.fxml'.";
        assert passengerDestinationDropdown != null : "fx:id=\"passengerDestinationDropdown\" was not injected: check your FXML file 'MainView.fxml'.";
        assert spawnTrainButton != null : "fx:id=\"spawnTrainButton\" was not injected: check your FXML file 'MainView.fxml'.";
        assert spawnPassengerButton != null : "fx:id=\"spawnPassengerButton\" was not injected: check your FXML file 'MainView.fxml'.";
        assert statusTabBackgroundAnchor != null : "fx:id=\"statusTabBackgroundAnchor\" was not injected: check your FXML file 'MainView.fxml'.";
        assert spawnTabBackgroundAnchor != null : "fx:id=\"spawnTabBackgroundAnchor\" was not injected: check your FXML file 'MainView.fxml'.";
        assert trainViewAnchor != null : "fx:id=\"trainViewAnchor\" was not injected: check your FXML file 'MainView.fxml'.";
        assert trainStatusScrollPane != null : "fx:id=\"trainStatusScrollPane\" was not injected: check your FXML file 'MainView.fxml'.";
        assert stationStatusScrollPane != null : "fx:id=\"stationStatusScrollPane\" was not injected: check your FXML file 'MainView.fxml'.";
        assert stopSimulationButton != null : "fx:id=\"stopSimulationButton\" was not injected: check your FXML file 'MainView.fxml'.";
        assert trainStatusGridPane != null : "fx:id=\"trainStatusGridPane\" was not injected: check your FXML file 'MainView.fxml'.";
        assert stationStatusGridPane != null : "fx:id=\"stationStatusGridPane\" was not injected: check your FXML file 'MainView.fxml'.";
        
        alertFactory = new AlertFactory();
        
        passengerSourceDropdown.getItems().removeAll(passengerSourceDropdown.getItems());
        passengerSourceDropdown.getItems().addAll("Station 1", "Station 2", "Station 3",
        		"Station 4", "Station 5", "Station 6", "Station 7", "Station 8");
        passengerSourceDropdown.getSelectionModel().select("Station 1");
        
        passengerDestinationDropdown.getItems().removeAll(passengerDestinationDropdown.getItems());
        passengerDestinationDropdown.getItems().addAll("Station 1", "Station 2", "Station 3",
        		"Station 4", "Station 5", "Station 6", "Station 7", "Station 8");
        passengerDestinationDropdown.getSelectionModel().select("Station 1");
        
        /*--------------------------------------------------*
         *                  SPAWN STATIONS
         *--------------------------------------------------*/
        
        for (x = 0; x < 8; x++) { // instantiate new stations
			stationArray[x] = new Station();
		}
		
		for (x = 0; x < 7; x++) // set next station for station 0-6
			stationArray[x].setNextStation(stationArray[x + 1]);
		
		stationArray[7].setNextStation(stationArray[0]);
        
		/*--------------------------------------------------*
         *           INITIALIZE OTHER VARIABLES
         *--------------------------------------------------*/
		trainsSpawned = 0;
		initializeStatus();
    }
    
    private void initializeStatus() {
    	for (x = 0; x < 8; x++) {
			passengersWaitingLabel[x] = new Label();
			passengersWaitingLabel[x].setText("0");
			stationStatusGridPane.add(passengersWaitingLabel[x], 1, x);
		}
    }
    
    @FXML
    private void spawnTrainAction(ActionEvent event) {
    	int trainCap;
    	
    	if (trainsSpawned < 15) {
    		try{
    	    	trainCap = Integer.parseInt(trainPassengerCapTextField.getText());
    	    	CustomAlert warning = alertFactory.createInformationAlert();
            	warning.setContentText("Spawned a train with a capacity of " +
            			trainCap + " passengers.");
            	warning.setTitle("Spawning a train");
            	warning.showAndWait();
            	trainsSpawned ++;
    	    	
            	trainStatusGridPane.add(new Label("" + trainsSpawned), 0, trainsSpawned-1);
            	trainStatusGridPane.add(new Label("0"), 1, trainsSpawned-1);
            	trainStatusGridPane.add(new Label("" + trainCap), 2, trainsSpawned-1);
            	trainStatusGridPane.add(new Label("1"), 3, trainsSpawned-1);
            	trainStatusGridPane.add(new Label("IDLE"), 4, trainsSpawned-1);
            	
    	    	stationArray[0].spawnTrain(trainCap);
    	    } catch(NumberFormatException nfe) {
        		CustomAlert error = alertFactory.createErrorAlert();
        		error.setContentText("Passenger capacity only accepts integer values.");
        		error.setTitle("Error in spawning a train");
        		error.showAndWait();
        	}	
    	}
    	
    	else {
    		CustomAlert error = alertFactory.createErrorAlert();
    		error.setContentText("Only up to 15 trains can be spawned during runtime.");
    		error.setTitle("Error in spawning a train");
    		error.showAndWait();
    	}
    	
    	trainPassengerCapTextField.clear();
    }
    
    @FXML
    private void spawnPassengerAction(ActionEvent event) {
    	int source = passengerSourceDropdown.getSelectionModel().getSelectedIndex() + 1;
    	int destination = passengerDestinationDropdown.getSelectionModel().getSelectedIndex() + 1;
    	int passengerCount = 0;
    	try {
    		passengerCount = Integer.parseInt(passengerCountTextField.getText());
    		CustomAlert warning = alertFactory.createInformationAlert();
        	warning.setContentText("Spawned " + passengerCount + 
        			" passengers in Station " + source + 
        			" to be dropped off at Station " + destination + ".");
        	warning.setTitle("Spawning passengers");
        	warning.showAndWait();
        	
        	while (passengerCount > 0) {
        		stationArray[source].spawnPassenger(stationArray[destination]);
        		passengerCount--;
        	}
    	} catch(NumberFormatException nfe) {
    		CustomAlert error = alertFactory.createErrorAlert();
    		error.setContentText("Passenger count only accepts integer values.");
    		error.setTitle("Error in spawning passengers");
    		error.showAndWait();
    	}
        	
        // Reset values
    	passengerSourceDropdown.getSelectionModel().select("Station 1");
    	passengerDestinationDropdown.getSelectionModel().select("Station 1");
    	passengerCountTextField.clear();
    }
    
    @FXML
    void stopSimulationAction(ActionEvent event) {
    	CustomAlert warning = alertFactory.createInformationAlert();
    	warning.setContentText("Simulation ended.");
    	warning.setTitle("CalTrainII");
    	warning.showAndWait();
    }
}
