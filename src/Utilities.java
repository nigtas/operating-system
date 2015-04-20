class Utilities {
	private static Utilities instance = null;

	protected Utilities() {
		
	}

	public static Utilities getInstance() {
		if(instance == null) {
			instance = new Utilities();
		}
		return instance;
   	}	

   	static String decToHex(int n) {
        String s = "";
        
        s = Integer.toHexString(n);
        if (s.length() < 4) {
            switch(s.length()) {
                case 1:
                    s = "000" + s;
                    break;
                case 2:
                    s = "00" + s;
                    break;
                case 3:
                    s = "0" + s;
                    break;
            }
        }
        return s;
    }

    static int hexToDec(String n) {
    	String digits = "0123456789ABCDEF";
             n = n.toUpperCase();
             int val = 0;
             for (int i = 0; i < n.length(); i++) {
                 char c = n.charAt(i);
                 int d = digits.indexOf(c);
                 val = 16*val + d;
             }
        return val;
    }

}

