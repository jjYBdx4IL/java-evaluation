package tests.javax.ws.rs;

public class DTO {

	public String getaString() {
		return aString;
	}
	public void setaString(String aString) {
		this.aString = aString;
	}
	public int getaInt() {
		return aInt;
	}
	public void setaInt(int aInt) {
		this.aInt = aInt;
	}
	public float getaFloat() {
		return aFloat;
	}
	public void setaFloat(float aFloat) {
		this.aFloat = aFloat;
	}
	private String aString;
	private int aInt;
	private float aFloat;
	@Override
	public String toString() {
		return "DTO [aString=" + aString + ", aInt=" + aInt + ", aFloat=" + aFloat + "]";
	}
	
	
}
