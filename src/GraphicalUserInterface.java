import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.text.LayeredHighlighter.LayerPainter;
import javax.swing.text.DefaultHighlighter.*;
import javax.swing.text.BadLocationException;
import java.util.Random;
import java.io.*;


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

	private boolean lightTurnedOn = false;

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

	// colors 
	private static Color colors[] = new Color[Memory.NUMBER_OF_BLOCKS];

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
		 new JTextField("C: "),
		 new JTextField("CX: ")
	};



	// constructor
	protected GraphicalUserInterface() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initPanels();
		initLabels();
		initLightButtons();
		initWritingArea();
		initOutputArea();
		initRAMlist();
		initColors();

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
		
		left.setLayout(new GridLayout(6, 1)); 
		centerLeftMost.setLayout(new BorderLayout());
		centerLeft.setLayout(new BorderLayout()); 
		right.setLayout(new BorderLayout()); 

		pane.add(left);
		pane.add(centerLeftMost);
		pane.add(centerLeft);
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

	private void initLightButtons() {
		JButton status = new JButton("Status");
		JButton turnOn = new JButton("ON");
		JButton turnOff = new JButton("OFF");
		left.add(status);
		left.add(turnOn);
		left.add(turnOff);


		status.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// m.out.println(OsUtils.getOsName());
				try {
					if(OsUtils.isUnix()) {
						Process p = Runtime.getRuntime().exec("cat /sys/class/power_supply/BAT1/capacity");
						getBatteryStatus(p);
					}
					if(OsUtils.isMac()) {
						Process p = Runtime.getRuntime().exec("pmset -g batt | egrep \"([0-9]+\\%).*\" -o --colour=auto | cut -f1 -d';'");
						getBatteryStatus(p);
					}
				}
				catch(Exception ex) {
					System.out.println("Command execution for battery");
				}
				
			}	
		});

		turnOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!lightTurnedOn) {
					lightTurnedOn = true;
					setOutputText("Light turned on!");
				}
				else {
					setOutputText("Light is already turned on");
				}
			}	
		});

		turnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(lightTurnedOn) {
					lightTurnedOn = false;
					setOutputText("Light turned off!");
				}
				else {
					setOutputText("Light is already turned off");
				}
			}
		});
	}

	private void getBatteryStatus(Process p) {
		try {
			// p.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(
			p.getInputStream()));
			String line = "";
			String output = "";

			while ((line = buf.readLine()) != null) {
				output += line;
			}
			setOutputText("Battery: " + output + "%");
			System.out.println(output);
		}
		catch(Exception e) {
			System.out.println("Battery read exception");
		}
		
	}

	private void initWritingArea() {
		JPanel subPanel = new JPanel();
		JButton executeBtn = new JButton("Execute");
		JButton loadBtn = new JButton("Load");
 		writingArea = new JTextArea();
		writingArea.setEditable(false);
		JScrollPane listScroller = new JScrollPane(writingArea);
		listScroller.setPreferredSize(new Dimension(150, 600));
		centerLeftMost.add(initTextField("CODE"), BorderLayout.NORTH);
		centerLeftMost.add(listScroller, BorderLayout.CENTER);
		subPanel.add(executeBtn);
		subPanel.add(loadBtn);
		centerLeftMost.add(subPanel, BorderLayout.SOUTH);


		// execute button listener
		executeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
				RealMachine.getInstance().execute();
            }
        });   

		// load button listener
        loadBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		RealMachine.getInstance().loadCode();
        	}
        });
	}

	private void initColors() {
		Random rand = new Random();
		float luminance = 0.7f;

		for(int i = 0; i < Memory.NUMBER_OF_BLOCKS; i++){
	    	float hue = rand.nextFloat();
			// Saturation between 0.1 and 0.3
			float saturation = (rand.nextInt(7000) + 1000) / 10000f;
			Color color = Color.getHSBColor(hue, saturation, luminance);
			colors[i] = color;	
		}
		
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

	private JLabel initTextField(String text) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setMaximumSize(label.getPreferredSize());
		return label;
	}	

	public static void loadCodeToWritingArea(String[] code) {
		writingArea.setText(null);
		for(String command : code) {
			writingArea.append(command + "\n");
		}
	}

	public static void highlightCurrentCodeLine(int index) {
		if(index <= writingArea.getLineCount()-2) {
			try {
			if(index >= 0) {
				int length = writingArea.getHighlighter().getHighlights().length;
				for(int i=0; i < length; ++i ) {
					writingArea.getHighlighter().removeHighlight(writingArea.getHighlighter().getHighlights()[0]);
				}
			}
			int startIndex = writingArea.getLineStartOffset(index);
        	int endIndex = writingArea.getLineEndOffset(index);
			DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.cyan);
			writingArea.getHighlighter().addHighlight(startIndex, endIndex, painter);
		} 
		catch (BadLocationException ex) { ex.printStackTrace(); }
		}	
	}

	public static void updateRAMCell(int index, String data) {
		ramData.remove(index);
		ramData.add(index, Utilities.getInstance().decToHex(index) + ". " + data);
	}

	// index - shows cell position (0..65535)
	// data - data to print
	public static void printDataToRAMCell(int index, String data) {
		ramData.add(index, Utilities.getInstance().decToHex(index) + ". " + data);
		ram.setCellRenderer(new DefaultListCellRenderer() {
		    @Override
		    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		        int block = index / 256;
		        label.setBackground(colors[block]); 
		        
		        return label;
		    }
		});
	}

	// index - shows cell position (0..65535)
	// method returns data from index cell
	public static String getDataFromRAMCell(int index) {
		String cell = ramData.get(index);
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
		String[] reg = {"ESP:", "DS:", "CS:", "SS:", "PTR:", "MODE:", "FLAGS:", "IOI:", "PI:", "SI:", "TI:", "TM:", "IP:", "C:", "CX:"};
		for(int i = 0; i < 15; i++) {
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