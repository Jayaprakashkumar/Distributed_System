package servers.rm3.common;

import java.io.Serializable;

public class CustomerData implements Serializable {
	
	private String custID;
	private String eveID;
	private String eveTyp;
	private int otherCityEve;
	
	
	public String getcustID() {
		return custID;
	}
	public void setcustID(String custID) {
		this.custID = custID;
	}
	public String geteveID() {
		return eveID;
	}
	public void seteveID(String eveID) {
		this.eveID = eveID;
	}
	
	
	
	
	public String geteveTyp() {
		return eveTyp;
	}
	public void seteveTyp(String eveTyp) {
		this.eveTyp = eveTyp;
	}
	
	
	public int getotherCityEve() {
		return otherCityEve;
	}
	public void setotherCityEve(int otherCityEve) {
		this.otherCityEve = otherCityEve;
	}
	
	@Override
	public String toString() {
		return "CustomerData [custID=" + custID + ", eveID=" + eveID + ", eveTyp=" + eveTyp
				+ ", otherCityEve=" + otherCityEve + "]";
	}

	
	
	
	
	
	
	

}
