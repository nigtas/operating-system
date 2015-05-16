public class Light {
	private static Light instance = null;
	private boolean isTurnedOn;

	public static Light getInstance() {
   		if(instance == null) {
   			instance = new Light();
   		}
   		return instance;
      }	

	public void turnOn() {
		if(!isTurnedOn) {
			isTurnedOn = true;
			GraphicalUserInterface.getInstance().setOutputText("Light is turned on");
		}
		else {
			GraphicalUserInterface.getInstance().setOutputText("Light is already turned on!");
		}
	}

	public void turnOff() {
		if(isTurnedOn) {
			isTurnedOn = false;
			GraphicalUserInterface.getInstance().setOutputText("Light is turned off");
		}
		else {
			GraphicalUserInterface.getInstance().setOutputText("Light is already turned off!");
		}
	}

	public boolean isTurnedOn() {
		return isTurnedOn;
	}

}