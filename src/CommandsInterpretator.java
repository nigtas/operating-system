public class CommandsInterpretator {

	CommandsInterpretator() {

	}

	public void executeCommand(String[] commands) {
		for(String command : commands) {
			System.out.println(command.substring(0, 2));
			String executionCode = command.substring(0, 2);
			switch(executionCode) {
				case "LD" : ld(command.substring(2, 4));
							break;
				case "PT" : pt(command.substring(2, 4));
							break;
				case "AD" : add();
							break;
				case "SU" : sub();
							break;
				case "MU" : mul();
							break;
				case "DI" : div();
							break;
				case "CD" : cds(command.substring(2, 3));
							break;
				case "CC" : ccs(command.substring(2, 3));
							break;
				case "CS" : css(command.substring(2, 3));
							break;
				case "JP" : jp(command.substring(2, 4));
							break;
				case "JL" : jl(command.substring(2, 4));
							break;
				case "JG" : jg(command.substring(2, 4));
							break;
				case "GD" : gd(command.substring(2, 4));
							break;
				case "PD" : pd(command.substring(2, 4));
							break;
				case "HA" : halt();
							break;
			}
		}
	}

	/*commands functions*/
	public void ld(String elements) {

	}

	public void pt(String elements) {

	}

	public void add() {

	}

	public void sub() {

	}

	public void mul() {

	}

	public void div() {

	}

	public void cds(String elements) {

	}

	public void ccs(String elements) {

	}

	public void css(String elements) {

	}

	public void jp(String elements) {

	}

	public void jl(String elements) {

	}

	public void jg(String elements) {

	}

	public void gd(String elements) {

	}

	public void pd(String elements) {

	}

	public void halt() {

	}



}