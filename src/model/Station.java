package model;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import controller.MainViewController;
import javafx.application.Platform;

public class Station {
	private Semaphore loadingSpot;
	private final int stationNo;
	private Station nextStation;
	private Station previousStation;
	private Train currentlyLoading;
	private ArrayList<Passenger> passengersWaiting;
	private ArrayList<Passenger> passengersDeparting;
	public static int stationsSpawned = 0; // for stationNo purposes
	private MainViewController c;
	
	public Station(MainViewController controller) {
		stationsSpawned++;
		stationNo = stationsSpawned;
		// isa lang yung free na "parking spot" sa station
		loadingSpot = new Semaphore(1);
		nextStation = null;
		previousStation = null;
		c = controller;
		System.out.println("Spawned Station " + stationNo + ".");
		passengersWaiting = new ArrayList<Passenger>();
		passengersDeparting = new ArrayList<Passenger>();
	}
	
	/*--------------------------------------
	 *	      SPAWNER / OTHER METHODS 
	 *--------------------------------------*/
	
	public void spawnPassenger(Station destination) {
		passengersWaiting.add(new Passenger(this, destination, c));
		Platform.runLater(() -> {
			c.updateStationWaiting(stationNo, passengersWaiting.size());
		});
	}
	
	public void spawnTrain(int capacity) {
		Train t = new Train(capacity, this, c);
		Platform.runLater(() -> {
			c.updateTrainPassengers(t);
			c.updateTrainLocation(t.getTrainNo(), stationNo);
			c.updateTrainStatus(t.getTrainNo(), "SPAWNED");
		});
	}
	
	public synchronized void loadTrain(Train t) {
		try {
			/* The function must not return until the train is 
			 * satisfactorily loaded (all passengers are in their
			 * seats, and either the train is full or all waiting
			 * passengers have boarded).
			 */
			System.out.println("Entered Station.loadTrain() method");
			loadingSpot.acquire();
			setCurrentlyLoading(t);
			System.out.println("Received Train " + t.getTrainNo() + 
				" in Station " + stationNo);
			Platform.runLater(() -> {
				c.updateStationLoading(stationNo, t.getTrainNo());
				c.updateTrainLocation(t.getTrainNo(), stationNo);
				c.updateTrainStatus(t.getTrainNo(), "LOADING");
			});
			
			// Wait while there are seats left and there are still waiting passengers
			for (Passenger p : currentlyLoading.getPassengersOnTrain()) {
				if (p.getDestinationStation() == this)
					passengersDeparting.add(p);
			}
			for (Passenger p : passengersDeparting) {
				p.depart();
				Platform.runLater(() -> {
					c.updateTrainPassengers(t);
				}); 
			}
			passengersDeparting.clear();
			
			while(currentlyLoading.getSeats().availablePermits() > 0 &&
				passengersWaiting.isEmpty() == false);
			System.out.println("Train " + currentlyLoading.getTrainNo() + 
				" in Station " + getStationNo() + " finished loading.");
			
			// Depart
			Thread.sleep(2500);
			int currentTrainNumber = currentlyLoading.getTrainNo();
			Platform.runLater(() -> {
				c.moveTrainSprite(getStationNo(), currentTrainNumber);
				c.updateTrainStatus(currentTrainNumber, "DEPARTING");
			});
			Thread.sleep(2500);
			loadingSpot.release();
			System.out.println("Train " + currentlyLoading.getTrainNo() +
					" departing from Station " + getStationNo() + ".");
			currentlyLoading = null;
			Platform.runLater(() -> {
				c.updateStationLoading(stationNo, "-");
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*--------------------------------------
	 *	        GETTERS / SETTERS 
	 *--------------------------------------*/
	
	public Semaphore getLoadingSpot() {
		return loadingSpot;
	}
	
	public int getStationNo() {
		return stationNo;
	}
	
	public Station getNextStation() {
		return nextStation;
	}
	
	public void setNextStation(Station s) {
		nextStation = s;
	}
	
	public Station getPreviousStation() {
		return previousStation;
	}
	
	public void setPreviousStation(Station s) {
		previousStation = s;
	}
	
	public Train getCurrentlyLoading() {
		return currentlyLoading;
	}
	
	public void setCurrentlyLoading(Train t) {
		currentlyLoading = t;
	}
	
	public ArrayList<Passenger> getPassengersWaiting() {
		return passengersWaiting;
	}
}