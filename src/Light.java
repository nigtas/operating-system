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
		if(!isTurnedOn || new String(RealMachine.getInstance().getLI()).equals("00")) {
			isTurnedOn = true;
			RealMachine.getInstance().setLI(new char[] {'0', '1'});
			GraphicalUserInterface.getInstance().setOutputText("Light is turned on");
		}
		else {
			GraphicalUserInterface.getInstance().setOutputText("Light is already turned on!");
		}
	}

	public void turnOff() {
		if(isTurnedOn || new String(RealMachine.getInstance().getLI()).equals("01")) {
			isTurnedOn = false;
			RealMachine.getInstance().setLI(new char[] {'0', '0'});
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