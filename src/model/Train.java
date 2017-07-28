package model;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import controller.MainViewController;

public class Train extends Thread {
	private Semaphore seats;
	private final int trainNo;
	private Station currentStation;
	private ArrayList<Passenger> passengersOnTrain;
	public static int trainsSpawned = 0; // for trainNo purposes
	private MainViewController c;
	
	public Train (int cap, Station sourceStation, MainViewController controller) {
		trainsSpawned++;
		trainNo = trainsSpawned;
		seats = new Semaphore(cap);
		currentStation = sourceStation;
		passengersOnTrain = new ArrayList<Passenger>();
		c = controller;
		this.start();
		System.out.println("Spawned Train " + trainNo + 
			" in Station " + sourceStation.getStationNo());
	}
	
	/*--------------------------------------
	 *	            RUN THREAD 
	 *--------------------------------------*/
	
	public void run()
	{
		while(true) {
			currentStation.loadTrain(this);
			currentStation = currentStation.getNextStation();
		}
	}
	
	/*--------------------------------------
	 *	        GETTERS / SETTERS 
	 *--------------------------------------*/
	
	public Semaphore getSeats() {
		return seats;
	}
	
	public int getTrainNo() {
		return trainNo;
	}
	
	public Station getCurrentStation() {
		return currentStation;
	}
	
	public void setCurrentStation(Station s) {
		currentStation = s;
	}
	
	public ArrayList<Passenger> getPassengersOnTrain() {
		return passengersOnTrain;
	}
	
	public void passengerRidesTrain(Passenger p){
		try {
			currentStation.getPassengersWaiting().remove(p);
			passengersOnTrain.add(p);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void passengerDepartsFromTrain(Passenger p) {
		try {
			passengersOnTrain.remove(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
