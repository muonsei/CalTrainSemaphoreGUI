package model;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import controller.MainViewController;

public class Station {
	private Semaphore loadingSpot;
	private final int stationNo;
	private Station nextStation;
	private Train currentlyLoading;
	private ArrayList<Passenger> passengersWaiting;
	public static int stationsSpawned = 0; // for stationNo purposes
	private MainViewController c;
	
	public Station(MainViewController controller) {
		stationsSpawned++;
		stationNo = stationsSpawned;
		// isa lang yung free na "parking spot" sa station
		loadingSpot = new Semaphore(1);
		nextStation = null;
		c = controller;
		System.out.println("Spawned Station " + stationNo + ".");
		passengersWaiting = new ArrayList<Passenger>();
	}
	
	/*--------------------------------------
	 *	      SPAWNER / OTHER METHODS 
	 *--------------------------------------*/
	
	public void spawnPassenger(Station destination) {
		passengersWaiting.add(new Passenger(this, destination, c));
		c.updateStationWaiting(this);
	}
	
	public void spawnTrain(int capacity) {
		Train t = new Train(capacity, this, c);
		c.updateTrainPassengers(t);
		c.updateTrainLocation(t);
		c.updateTrainStatus(t, "SPAWNED");
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
			c.updateStationLoading(this);
			c.updateTrainLocation(t);
			c.updateTrainStatus(t, "LOADING");
			
			// Wait while there are seats left and there are still waiting passengers
			while(currentlyLoading.getSeats().availablePermits() > 0 &&
				passengersWaiting.isEmpty() == false);
			System.out.println("Train " + currentlyLoading.getTrainNo() + 
				" in Station " + getStationNo() + " finished loading.");
			
			// Depart
			Thread.sleep(3000);
			c.updateStationLoading(this);
			c.updateTrainLocation(t);
			c.updateTrainStatus(t, "DEPARTING");
			loadingSpot.release();
			c.moveTrainSprite(nextStation.getStationNo(), currentlyLoading.getTrainNo());
			nextStation.loadTrain(currentlyLoading);
			System.out.println("Train " + currentlyLoading.getTrainNo() +
					" departing from Station " + getStationNo() + ".");
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