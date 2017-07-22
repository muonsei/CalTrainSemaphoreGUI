package application;
import java.util.Scanner;
import model.Station;
import view.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("railmap.jpg"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
		
		Scanner sc = new Scanner(System.in);
		int choice = 0, trainCap = 0, passengerSpawn = 0, 
			passengerDropOff = 0, passengerCount = 0;
		
		// Spawn stations first
		Station[] stationArray = new Station[8];
		int x;
		
		for (x = 0; x < 8; x++) { // instantiate new stations
			stationArray[x] = new Station();
		}
		
		for (x = 0; x < 7; x++) // set next station for station 0-6
			stationArray[x].setNextStation(stationArray[x + 1]);
		
		stationArray[7].setNextStation(stationArray[0]);
		
		do {
			System.out.println("1 - Spawn passengers");
			System.out.println("2 - Spawn trains");
			System.out.println("3 - Exit");
			System.out.print("Enter choice: ");
			choice = sc.nextInt();
			
			switch(choice) {
				case 1: // Spawn passengers
					// Enter # of passengers
					System.out.print("Enter # of passengers: ");
					passengerCount = sc.nextInt();
					// Enter station to spawn
					System.out.print("Which station (1-8) would you like to spawn them?");
					passengerSpawn = sc.nextInt();
					if (passengerSpawn >= 1 && passengerSpawn <= 8) { // within bounds
						System.out.print("Which station (1-8) would you like to drop them off?");
						passengerDropOff = sc.nextInt();
						if (passengerSpawn >= 1 && passengerSpawn <= 8) { // within bounds
							while (passengerCount > 0) {
								stationArray[passengerSpawn - 1].spawnPassenger(stationArray[passengerDropOff - 1]);
								passengerCount--;
							}
						}
						else { // out of bounds
							System.out.println("!!! Station out of bounds. !!!");
						}
					} 
					else { // out of bounds
						System.out.println("!!! Station out of bounds. !!!");
					}
					break;
				case 2: // Spawn trains
					System.out.print("Enter train capacity: ");
					trainCap = sc.nextInt();
					// Only allow spawning in Station 0
					stationArray[0].spawnTrain(trainCap);
					break;
				case 3: // Exit
					System.out.println("!!! Exiting... !!!");
					break;
				default:
					System.out.println("Invalid choice. Doing nothing...");
			}
		} while (choice != 3);
		
		sc.close();
	}

}
