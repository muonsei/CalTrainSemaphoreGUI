package model;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Station {
	private Semaphore loadingSpot;
	private final int stationNo;
	private Station nextStation;
	private Train currentlyLoading;
	private ArrayList<Passenger> passengersWaiting;
	public static int stationsSpawned = 0; // for stationNo purposes
	
	public Station() {
		stationsSpawned++;
		stationNo = stationsSpawned;
		// isa lang yung free na "parking spot" sa station
		loadingSpot = new Semaphore(1);
		nextStation = null;
		System.out.println("Spawned Station " + stationNo + ".");
		passengersWaiting = new ArrayList<Passenger>();
	}
	
	/*--------------------------------------
	 *	      SPAWNER / OTHER METHODS 
	 *--------------------------------------*/
	
	public void spawnPassenger(Station destination) {
		passengersWaiting.add(new Passenger(this, destination));
	}
	
	public void spawnTrain(int capacity) {
		loadTrain(new Train(capacity, this));
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
			
			// Wait while there are seats left and there are still waiting passengers
			while(currentlyLoading.getSeats().availablePermits() > 0 &&
				passengersWaiting.isEmpty() == false);
			System.out.println("Train " + currentlyLoading.getTrainNo() + 
				" in Station " + getStationNo() + " finished loading.");
			
			// Depart
			Thread.sleep(3000);
			loadingSpot.release();
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
