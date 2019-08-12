package servers.rm3.common;



import java.util.ArrayList;
import java.util.List;

public class EventsData {
	
	   public int capacityOfBooking = 0;
	    public int seatsFilled = 0;
	    public int seatsLeft = 0;
	    public String bookingCity = "";
	    public List<String> registerdCustomer = new ArrayList<String>();
    
    
    public int getcapacityOfBooking() {
		return capacityOfBooking;
	}



	public void setcapacityOfBooking(int capacityOfBooking) {
		this.capacityOfBooking = capacityOfBooking;
	}



	public int getSeatsFilled() {
		return seatsFilled;
	}



	public void setSeatsFilled(int seatsFilled) {
		this.seatsFilled = seatsFilled;
	}



	public int getSeatsLeft() {
		return seatsLeft;
	}



	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}



	public String getbookingCity() {
		return bookingCity;
	}



	public void setbookingCity(String bookingCity) {
		this.bookingCity = bookingCity;
	}



	public List<String> getregisterdCustomer() {
		return registerdCustomer;
	}



	public void setregisterdCustomer(List<String> registerdCustomer) {
		this.registerdCustomer = registerdCustomer;
	}



	@Override
	public String toString() {
		return "EventsData [capacityOfBooking=" + capacityOfBooking + ", seatsFilled=" + seatsFilled + ", seatsLeft="
				+ seatsLeft + ", bookingCity=" + bookingCity + ", registerdCustomer=" + registerdCustomer
				+ "]";
	}
    
    

    
}
