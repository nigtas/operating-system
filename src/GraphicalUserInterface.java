import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;


/* METHODS
	1 - printDataToRAMCell(int index, String data) - sets cell data at index cell
	2 - getDataFromRAMCell(int index) - return String data from cell at index position
	3 - printDataToVRAMCell(int index, String data) - sets cell data at index cell
	4 - getDataFromVRAMCell(int index) - return String data from cell at index position
	5 - printDataToHDDCell(int index, String data) - sets cell data at index cell
	6 - getDataFromHDDCell(int index) - return String data from cell at index position
	7 - setOutputText(String text) - sets text to be displayed in output window (overwrites old text)
	8 - appendOutputText(String text) - appends text to displayed in output (concatenates old text with new)
	9 - getProgramCode() - returns array of Strings, each line of code window is one array element
	10 - setRegisters(String[] array) - sets all registers, need to pass array of registers (array structure written below)
*/

/* ARRAY OF REGISTERS 
	0 - ESP
	1 - DS
	2 - CS
	3 - SS
	4 - PTR
	5 - MODE
	6 - FLAGS
	7 - IOI
	8 - PI
	9 - SI
	10 - TI
	11 - TM
	12 - IP
	13 - C
*/

public class GraphicalUserInterface {
	private static GraphicalUserInterface instance = null;

	private final JFrame frame = new JFrame("Virtual Machine");
	
	// data of rams
	private static DefaultListModel<String> ramData = new DefaultListModel<String>();
	// data of vram
	private static DefaultListModel<String> vramData = new DefaultListModel<String>();
	
	// containers
	private Container pane = null;
	private JPanel left = new JPanel();
	private JPanel centerLeftMost = new JPanel();
	private JPanel centerLeft = new JPanel();
	private JPanel centerRight = new JPanel();
	private JPanel right = new JPanel();
	
	// lists
	private static JList<String> ram = null;
	private static JList<String> vram = null;
	private static JTextArea writingArea = null;
	private static JTextArea outputArea = null;

	private static JTextField[] fields = {
		 new JTextField("ESP: "),
		 new JTextField("DS: "),
		 new JTextField("CS: "),
		 new JTextField("SS: "),
		 new JTextField("PTR: "),
		 new JTextField("MODE: "),
		 new JTextField("FLAGS: "),
		 new JTextField("IOI: "),
		 new JTextField("PI: "),
		 new JTextField("SI: "),
		 new JTextField("TI: "),
		 new JTextField("TM: "),
		 new JTextField("IP: "),
		 new JTextField("C: ")
	};

	// constructor
	protected GraphicalUserInterface() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initPanels();
		initLabels();
		initWritingArea();
		initOutputArea();
		initRAMlist();
		initVRAMlist();

		frame.setSize(1000, 700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	
	}

	public static GraphicalUserInterface getInstance() {
      if(instance == null) {
         instance = new GraphicalUserInterface();
      }
      return instance;
   	}	

	// methods
	private void initPanels() {
		pane = frame.getContentPane();
		pane.setLayout(new GridLayout(1, 0));
		
		left.setLayout(new GridLayout(7, 1)); 
		centerLeftMost.setLayout(new BorderLayout());
		centerLeft.setLayout(new BorderLayout());
		centerRight.setLayout(new BorderLayout()); 
		right.setLayout(new BorderLayout()); 

		pane.add(left);
		pane.add(centerLeftMost);
		pane.add(centerLeft);
		pane.add(centerRight);
		pane.add(right);
	}

	private void initLabels() {
		for(JTextField tf : fields) {
			tf.setColumns(5);
			tf.setEditable(false);
			tf.setHorizontalAlignment(JTextField.CENTER);
			left.add(tf);
		}
	}

	private void initWritingArea() {
		JButton executeBtn = new JButton("Execute");
 		writingArea = new JTextArea();
		writingArea.setWrapStyleWord(true);
		writingArea.setLineWrap(true);
		writingArea.setCaretPosition(0);
		JScrollPane listScroller = new JScrollPane(writingArea);
		listScroller.setPreferredSize(new Dimension(150, 600));
		centerLeftMost.add(initTextField("CODE"), BorderLayout.NORTH);
		centerLeftMost.add(listScroller, BorderLayout.CENTER);
		centerLeftMost.add(executeBtn, BorderLayout.SOUTH);


		// execute button listener
		executeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                String[] lines = getProgramCode();
				for(String line : lines) {
					System.out.println(line);
				}
				RealMachine.getInstance().execute(lines);
            }
        });      
	}

	private void initOutputArea() {
		outputArea = new JTextArea();
		outputArea.setWrapStyleWord(true);
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);
		JScrollPane listScroller = new JScrollPane(outputArea);
		listScroller.setPreferredSize(new Dimension(150, 600));
		centerLeft.add(initTextField("OUTPUT"), BorderLayout.NORTH);
		centerLeft.add(listScroller, BorderLayout.CENTER);
	}

	private void initRAMlist() {	
		ram = new JList<String>();
		ram.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane listScroller = new JScrollPane(ram);
		listScroller.setPreferredSize(new Dimension(150, 600));
		right.add(initTextField("RAM"), BorderLayout.NORTH);
		right.add(listScroller, BorderLayout.CENTER);
	}

	private void initVRAMlist() {
		vram = new JList<String>();
		vram.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane listScroller = new JScrollPane(vram);
		listScroller.setPreferredSize(new Dimension(150, 600));
		centerRight.add(initTextField("VRAM"), BorderLayout.NORTH);
		centerRight.add(listScroller, BorderLayout.CENTER);
	}

	private JLabel initTextField(String text) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setMaximumSize(label.getPreferredSize());
		return label;
	}	

	public static void updateRAMCell(int index, String data) {
		ramData.remove(index);
		ramData.add(index, Utilities.getInstance().decToHex(index) + ". " + data);
	}

	// index - shows cell position (0..65535)
	// data - data to print
	public static void printDataToRAMCell(int index, String data) {
		ramData.add(index, Utilities.getInstance().decToHex(index) + ". " + data);
	}

	// index - shows cell position (0..65535)
	// method returns data from index cell
	public static String getDataFromRAMCell(int index) {
		String cell = ramData.get(index);
		String cellData = cell.substring(cell.lastIndexOf(".") + 1).trim();
		return cellData;
	}

	// index - shows cell position (0..65535)
	// data - data to print
	public static void printDataToVRAMCell(int index, String data) {
		vramData.add(index, Utilities.getInstance().decToHex(index) + ". " + data);
	}

	// index - shows cell position (0..65535)
	// method returns data from index cell
	public static String getDataFromVRAMCell(int index) {
		String cell = vramData.get(index);
		String cellData = cell.substring(cell.lastIndexOf(".") + 1).trim();
		return cellData;
	}

	// text - a text to set into output window
	public static void setOutputText(String text) {
		outputArea.setText(text);
	}

	// text - a text to append into output window
	public static void appendOutputText(String text) {
		outputArea.append(text);
	}

	public static String[] getProgramCode() {
		String[] lines = writingArea.getText().split("\\n");
		return lines;
	}

	// sets new register parameters
	// array has to be of size 14
	public static void setRegisters(String[] array) {
		String[] reg = {"ESP:", "DS:", "CS:", "SS:", "PTR:", "MODE:", "FLAGS:", "IOI:", "PI:", "SI:", "TI:", "TM:", "IP:", "C:"};
		for(int i = 0; i < 14; i++) {
			fields[i].setText(reg[i] + " " + array[i]);
		}
	}

	/* SETTERS AND GETTERS */
	public static DefaultListModel<String> getRAMModel() {
		return ramData;
	}
	public static JList<String> getRAMJList() {
		return ram;
	}
	public static DefaultListModel<String> getVRAMModel() {
		return vramData;
	}
	public static JList<String> getVRAMJList() {
		return vram;
	}


}