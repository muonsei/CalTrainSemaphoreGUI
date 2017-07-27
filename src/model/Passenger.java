package model;

import controller.MainViewController;

public class Passenger extends Thread {
	private final int passengerNo;
	private Station sourceStation;
	private final Station destinationStation;
	private Train currentlyRiding;
	public static int passengersSpawned = 0; // for passengerNo purposes
	private MainViewController c;
	
	public Passenger(Station source, Station destination, MainViewController controller) {
		passengersSpawned++;
		passengerNo = passengersSpawned;
		sourceStation = source;
		destinationStation = destination;
		c = controller;
		this.start(); // start thread
		System.out.println("Spawned Passenger " + passengerNo +
			" in Station " + source.getStationNo() + 
			" getting off at Station " + destination.getStationNo() + ".");
	}
	
	/*--------------------------------------
	 *	     SYNCHRONIZATION METHODS 
	 *--------------------------------------*/
	
	public void waitForTrain() {
		System.out.println("Passenger " + passengerNo +
			" is waiting in Station " + sourceStation.getStationNo() + ".");
		while (sourceStation.getLoadingSpot().availablePermits() == 1);
		while (sourceStation.getCurrentlyLoading().getSeats().availablePermits() == 0);
		System.out.println("Passenger " + passengerNo + 
			" stopped waiting in Station " + sourceStation.getStationNo() + ".");
		/* walang train sa loob ng station, just wait 
		 * walang free seats sa train just wait
		 * kapag may train na and may free seats, stop waiting
		 */
	}
	
	public synchronized void onBoard() {
		try {
			sourceStation.getCurrentlyLoading().getSeats().acquire(); // try to sit in train
			currentlyRiding = sourceStation.getCurrentlyLoading();
			currentlyRiding.passengerRidesTrain(this);
			System.out.println("Passenger " + passengerNo + 
				" is now on board Train " + currentlyRiding.getTrainNo() + ".");
			//System.out.println("Seats left in train = " + currentlyRiding.getSeats().availablePermits());
			//System.out.println("Passengers left in station = " + sourceStation.getPassengersWaiting().size());
			c.updateTrainPassengers(currentlyRiding);
			c.updateStationWaiting(sourceStation);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void depart() {
		while(currentlyRiding.getCurrentStation() != destinationStation);
		currentlyRiding.getSeats().release();
		currentlyRiding.passengerDepartsFromTrain(this);
		System.out.println("Passenger " + passengerNo + 
			" is now departing from Train " + 
			currentlyRiding.getTrainNo() + ".");
		c.updateTrainPassengers(currentlyRiding);
	}
	
	/*--------------------------------------
	 *	            RUN THREAD 
	 *--------------------------------------*/
	
	public void run() {
		waitForTrain();
		onBoard();
		depart();
	}
	
	/*--------------------------------------
	 *	        GETTERS / SETTERS 
	 *--------------------------------------*/

	public int getPassengerNo() {
		return passengerNo;
	}

	public Station getSourceStation() {
		return sourceStation;
	}

	public Station getDestinationStation() {
		return destinationStation;
	}

	public Train getCurrentlyRiding() {
		return currentlyRiding;
	}
}
