import java.lang.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class RealMachine {
      public boolean changedCS = false;

    private static RealMachine instance = null;
    private VirtualMachine vm = null;
    private Memory ram = null;
      private Swapping swapping = null;
      //commands interpretator
      private CommandsInterpretator ci = null;

    // Registers
    private char[] esp = {'0', '0', '0', '0'};      // Steko rodykles registras
    private char[] ds = {'0', '0', '0', '0'};       // Data segmentas
    private char[] cs = {'0', '0', '0', '0'};       // Kodo segmentas
    private char[] ss = {'0', '0', '0', '0'};       // Steko segmentas
    private char[] ptr = {'0', '0', '0', '0'};      // Puslapiu lenteles registras
    private char[] ip = {'0', '0'};               // VM programos skaitiklias
    private char[] flags = {'0','0','0', '0'};    // Pozymiu registras
    private char[] c = {'1', '1'};             // Kanalo registas
    private char[] ti = {'0', '0'};                 // Taimerio pertraukimo registras
    private char[] pi = {'0', '0'};                 // Programiniu pertraukimu registras  
    private char[] si = {'0', '0'};                 // Supervizoriniu pertraukimu registras 
    private char[] ioi = {'0', '0'};                // I/O registas 
    private char[] mode = {'0', '1'};               // MODE registas
    private char[] tm = {'0', '9'};                 // TM registras
      private char[] cx = {'0', '0'};                 // cx register for loop
      private char[] li = {'0', '0'};
      private int codeSize = 0;

      /*
         FLAGS :
         0 - 
         1 - >0
         2 - Zero flag 
         3 - <0

      */

    protected RealMachine() {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  GraphicalUserInterface.getInstance();
               }
            });
         } catch (Exception e) {

         }
      
      initRM();
    }

    public static RealMachine getInstance() {
      if(instance == null) {
        instance = new RealMachine();
      }
      return instance;
      } 

    private void initRM() {
      ram = new Memory();
         swapping = new Swapping();
         ci = new CommandsInterpretator();

     //     // setting PTR register
     //     setPTR(ram.newPageTable());
     //     countMaxPages();
     //     initStack();
     //     setESP(new char[]{'0', '0', 'F', 'F'});
     //     initDataSegment();
     //     initCodeSegment();


      char[][] memory = ram.getMemory();
      String word = "";
      for(int i = 0; i < ram.NUMBER_OF_BLOCKS * ram.NUMBER_OF_WORDS; i++) {
        word = "";
        for(int j = 0; j < ram.WORD_SIZE; j++) {
          word += memory[i][j];
        }
        GraphicalUserInterface.getInstance().printDataToRAMCell(i, word);
      }



      GraphicalUserInterface.getInstance().getRAMJList().setModel(GraphicalUserInterface.getInstance().getRAMModel());
      GraphicalUserInterface.getInstance().setRegisters(collectAllRegisters());
       GraphicalUserInterface.getInstance().appendOutputText("REAL MACHINE READY!\n");
    }


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

    public String[] collectAllRegisters() {
      String[] array = new String[16];
      array[0] = new String(esp);
      array[1] = new String(ds);
      array[2] = new String(cs);
      array[3] = new String(ss);
      array[4] = new String(ptr);
      array[5] = String.valueOf(mode);
      array[6] = new String(flags);
      array[7] = String.valueOf(ioi);
      array[8] = String.valueOf(pi);
      array[9] = String.valueOf(si);
      array[10] = String.valueOf(ti);
      array[11] = String.valueOf(tm);
      array[12] = new String(ip);
      array[13] = new String(c);
         array[14] = new String(cx);
         array[15] = new String(li);
      return array;
    }

      public void initStack() {

         int ssAddress = Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK;         
         System.out.println("STACK address " + ssAddress);
         String ssValue = new String(ram.getWord(ssAddress - (Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK), ssAddress));
         if(ssValue.equals("----")) {
            String activeVMBlock = new String( ram.getActiveVMblockForSwapping(getHalfPTR(), getDS(), getSS(), getCS() ) );
            if(activeVMBlock.equals("----")) {
              System.out.println("NO FREE BLOCKS!");
              setPTR(new char[] {'E', 'R', 'O', 'R'});
              return;
            }
            System.out.println("init stack: " + activeVMBlock);
            int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getHalfPTR(), activeVMBlock);
            int activeBlockNr = Utilities.getInstance().hexToDec(activeVMBlock);
            
            swapping.swap(pageTablePlaceForActiveBlock, ram.getBlock(activeBlockNr / Memory.NUMBER_OF_WORDS));
            ram.setBlockInactive(Utilities.getInstance().hexToDec(new String(getHalfPTR())), pageTablePlaceForActiveBlock);

            ram.setWord(Utilities.getInstance().hexToDec(new String(getHalfPTR())), (Memory.NUMBER_OF_WORDS - Memory.NUMBER_OF_STACK_BLOCK), activeVMBlock.toCharArray());
            setSS(Utilities.getInstance().decToHex(ssAddress).toCharArray());            
         } 
      }

      public void initDataSegment() {
         System.out.println("INIT DATA SEGMEN: " + new String(getHalfPTR()));
         String findActiveVmBlock = new String( ram.getActiveVMblockForSwapping( getHalfPTR(), getDS(), getSS(), getCS() ) ); 
         System.out.println("active data segment = " + findActiveVmBlock);
         if(findActiveVmBlock.equals("----")) {
              System.out.println("NO FREE BLOCKS!");
              setPTR(new char[] {'E', 'R', 'O', 'R'});
              return;
          }
         int block = Utilities.getInstance().hexToDec(findActiveVmBlock);
         System.out.println("INIT DATA SEGME BLOCK: " + block);
         for(int i = 0; i < ram.NUMBER_OF_WORDS - 1; i++) {
            ram.setWord(block, i, Utilities.getInstance().decToHex(i).toCharArray());
         }
         ram.setWord(block, 255, new char[] {'F', 'F', 'F', 'F'});
         int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getHalfPTR(), findActiveVmBlock);
         System.out.println("init data segm paget table: " + pageTablePlaceForActiveBlock);
         setDS(Utilities.getInstance().decToHex(pageTablePlaceForActiveBlock).toCharArray());
      }

      public void initCodeSegment() {
         String findActiveVmBlock = new String( ram.getActiveVMblockForSwapping( getHalfPTR(), getDS(), getSS(), getCS() ) ); 
         System.out.println("active code segment = " + findActiveVmBlock + " ds " + new String(getDS()));
         if(findActiveVmBlock.equals("----")) {
              System.out.println("NO FREE BLOCKS!");
              setPTR(new char[] {'E', 'R', 'O', 'R'});
              return;
         }
         int pageTablePlaceForActiveBlock = ram.getPageTablePlaceForActiveBlock(getHalfPTR(), findActiveVmBlock);
         System.out.println(pageTablePlaceForActiveBlock);
         setCS(Utilities.getInstance().decToHex(pageTablePlaceForActiveBlock).toCharArray());
      }

      public void countMaxPages() {
         int counter = 0;
         for(int i = 0; i < ram.NUMBER_OF_WORDS; ++i) {
            if(ram.usedWords[Utilities.charToInt(getHalfPTR(), 16)][i]) {
               ++counter;
            }
         }
         ptr[1] = Utilities.decToHex(counter/15).charAt(3);
      }

      public void execute() {
         ci.executeCommand();
      }

      public void loadCode() {
         vm = new VirtualMachine();
         System.out.println("as dasd fasd gfad gasd gasdg " + new String(getHalfPTR()));
         if(new String(getHalfPTR()).equals("00OR")) {
            System.out.println("asldkhafjks dkgfsa d");
            return;
         }
         List<String> code = new ArrayList<String>();
         BufferedReader br = null;
         try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("code.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
               code.add(sCurrentLine);
            } 
         }
         catch (IOException e) {
            e.printStackTrace();
         }
         finally {
            try {
               if (br != null)br.close();
            }  
            catch (IOException ex) {
               ex.printStackTrace();
            }
         }
         String[] convertedCode = new String[code.size()-1];
         int codSz = code.size() - 1;
         setCodeSize(codSz);
         if(!code.get(0).equals("PROG")) {
             System.out.println("Bad program begining");
                     return;
         }
         int memoryBlockForCode = Utilities.getInstance().charToInt(ram.getWord(Utilities.getInstance().charToInt(getHalfPTR(), 16), Utilities.getInstance().charToInt(getCS(), 16)), 16);
         for(int i = 1; i < code.size(); ++i) {
            convertedCode[i-1] = code.get(i);
            System.out.println(convertedCode[i-1]);
         }

         //change first PTR bytes for new info
         char[] currentPTR = getPTR();
         if(!changedCS) {
            currentPTR[0] = '1';
         }
         else {
            currentPTR[0] = (char)(Character.getNumericValue(getPTR()[0]) + 1);
            changedCS = false;
         }
         System.out.println("PTR::: " + new String(getPTR()) + " da " + new String(currentPTR));
         // convertedCode = code.toArray(convertedCode);
         loadCodeToMemory(memoryBlockForCode, convertedCode);
         // ci = new CommandsInterpretator();
         GraphicalUserInterface.getInstance().loadCodeToWritingArea(convertedCode);
         GraphicalUserInterface.getInstance().setRegisters(collectAllRegisters());      
      }

      public void changeValue() {
        System.out.println("--Changing value!");
        String[] reg = {"ESP", "DS", "CS", "SS", "PTR", "MODE", "FLAGS", "IOI", "PI", "SI", "TI", "TM", "IP", "C", "CX", "LI"};

        JPanel panel = new JPanel();
        panel.add(new JLabel("Which register's value do you want to change:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        for (int i = 0; i < 16; i++) {
            model.addElement(reg[i]); 
        }

        JComboBox comboBox = new JComboBox(model);
        panel.add(comboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit value", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (result) {
            case JOptionPane.OK_OPTION:
                String selectedItem = (String) comboBox.getSelectedItem();
                String inputValue = new String();
                // after selecting what to change, direct to each case
                switch (selectedItem) {
                    case "ESP":
                        System.out.println("Changing ESP");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setESP(inputValue.toCharArray());
                        System.out.println("New ESP value: " + new String(getESP()));
                        break;
                    case "DS":
                        System.out.println("Changing DS");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setDS(inputValue.toCharArray());
                        System.out.println("New DS value: " + new String(getDS()));               
                        break;
                    case "CS":
                        System.out.println("Changing CS");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setCS(inputValue.toCharArray());
                        System.out.println("New CS value: " + new String(getCS()));
                        break;
                    case "SS":
                        System.out.println("Changing SS");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setSS(inputValue.toCharArray());
                        System.out.println("New SS value: " + new String(getSS()));
                    break;
                    case "PTR":
                        System.out.println("Changing PTR");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        String twoLastBytes = inputValue;
                        int lastByt = Integer.parseInt(twoLastBytes, 16);
                        char[] inputChar = inputValue.toCharArray();
                        String finalInput = new String(inputChar).substring(2, 4);
                        if ((lastByt <= ram.NUMBER_OF_BLOCKS)) {
                          System.out.println("*" + lastByt + " <= " + ram.NUMBER_OF_BLOCKS);
                        } else {
                          setPTR(inputValue.toCharArray());
                          setPI(new char[] {'0', '7'});
                          System.out.println("Wrong input.");
                          break;
                        }

                        int SS = Utilities.getInstance().charToInt(getSS(), 16);
                        int DS = Utilities.getInstance().charToInt(getDS(), 16);
                        int CS = Utilities.getInstance().charToInt(getCS(), 16);
                        if(new String(ram.getWord(lastByt, SS)).equals("----") || new String(ram.getWord(lastByt, CS)).equals("----") 
                                || new String(ram.getWord(lastByt, DS)).equals("----")) {
                          setPI(new char[] {'0', '7'});
                          System.out.println("Wrong input.");
                          break;
                        }
                        setPTR(inputValue.toCharArray());
                        System.out.println("New PTR value: " + new String(getPTR()));
                    break;
                    case "MODE":
                        System.out.println("Changing MODE");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setMODE(inputValue.toCharArray());
                        System.out.println("New MODE value: " + new String(getMODE()));
                    break;
                    case "FLAGS":
                        System.out.println("Changing FLAGS");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setFLAGS(inputValue.toCharArray());
                        System.out.println("New FLAGS value: " + new String(getFLAGS()));
                    break;
                    case "IOI":
                        System.out.println("Changing IOI");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setIOI(inputValue.toCharArray());
                        System.out.println("New IOI value: " + new String(getIOI()));
                    break;
                    case "PI":
                        System.out.println("Changing PI");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setPI(inputValue.toCharArray());
                        System.out.println("New PI value: " + new String(getPI()));
                    break;
                    case "SI":
                        System.out.println("Changing SI");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setSI(inputValue.toCharArray());
                        System.out.println("New SI value: " + new String(getSI()));
                    break;
                    case "TI":
                        System.out.println("Changing TI");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setTI(inputValue.toCharArray());
                        System.out.println("New TI value: " + new String(getTI()));
                    break;
                    case "TM":
                        System.out.println("Changing TM");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setTM(inputValue.toCharArray());
                        System.out.println("New TM value: " + new String(getTM()));
                    break;
                    case "IP":
                        System.out.println("Changing IP");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setIP(inputValue.toCharArray());
                        System.out.println("New IP value: " + new String(getIP()));
                    break;
                    case "C":
                        System.out.println("Changing C");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setC(inputValue.toCharArray());
                        System.out.println("New C value: " + new String(getC()));
                    break;
                    case "CX":
                        System.out.println("Changing CX");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setCX(inputValue.toCharArray());
                        System.out.println("New CX value: " + new String(getCX()));
                    break;
                    case "LI":
                        System.out.println("Changing LI");
                        inputValue = JOptionPane.showInputDialog("Please input a value");
                        setLI(inputValue.toCharArray());
                        System.out.println("New LI value: " + new String(getLI()));
                    break;
                }
                break;
        }
        GraphicalUserInterface.getInstance().setRegisters(collectAllRegisters());
      }

      public void changeMemValue() {
        System.out.println("--Changing memory value!");

        JTextField block = new JTextField(5);
        JTextField place = new JTextField(5);
        JTextField newValue = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("block:"));
        myPanel.add(block);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("place:"));
        myPanel.add(place);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("newValue:"));
        myPanel.add(newValue);

        int result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter block, place and new value", JOptionPane.OK_CANCEL_OPTION);
        
        switch (result) {
            case JOptionPane.OK_OPTION:
                String blockSval = block.getText();
                String placeSval = place.getText();
                String newValueSval = newValue.getText();

                System.out.println("block value: " + blockSval);
                System.out.println("place value: " + placeSval);
                System.out.println("newValue value: " + newValueSval); 

                int whichBlock = Integer.parseInt(blockSval, 16);
                int whichPlace = Integer.parseInt(placeSval, 16);

                System.out.println("block integer value: " + whichBlock);
                System.out.println("place integer value: " + whichPlace);

                ram.setWord(whichBlock, whichPlace, newValueSval.toCharArray());   
                GraphicalUserInterface.getInstance().updateRAMCell(Memory.NUMBER_OF_WORDS * whichBlock + whichPlace, newValueSval);
            break;
        }
      }

      public void loadCodeToMemory(int blockNumber, String[] code) {
         int firstFreePlace = ram.getFreeWord(blockNumber);
         if(code.length > 255) {
            System.out.println("Too big code! No free space left in memory"); //is it enough??
            GraphicalUserInterface.getInstance().setOutputText("Too big code! No free space left in memory");
         }
         else {
            for(int i = 0; i < code.length ; ++i) {
               ram.setWord(blockNumber, i, code[i].toCharArray());
               GraphicalUserInterface.getInstance().updateRAMCell(blockNumber * 256 + i, new String(ram.getWord(blockNumber, i)));
            }
            setIP(Utilities.decToHex(0).toCharArray());
         }
      }

      public String getCodeFromMemory(int ip) {
         char[] realBlock = ram.getWord(Utilities.getInstance().charToInt(getHalfPTR(), 16), Utilities.getInstance().charToInt(getCS(), 16));
         int blockNumber = Utilities.getInstance().charToInt(realBlock, 16);
         return new String(ram.getWord(blockNumber, ip));
      }

      public void test() {
         int pi = Utilities.getInstance().charToInt(getPI(), 16);
         int ti = Utilities.getInstance().charToInt(getTI(), 16);
         int si = Utilities.getInstance().charToInt(getSI(), 16);
         int ioi = Utilities.getInstance().charToInt(getIOI(), 16);
         int li = Utilities.getInstance().charToInt(getLI(), 16);
         if(pi > 0) {
            switch(pi) {
               case 1: GraphicalUserInterface.getInstance().setOutputText("Division by 0");
                       System.out.println("Division by 0"); //done
                       stopExecution();
                       break;
               case 2: GraphicalUserInterface.getInstance().setOutputText("Negative value");
                       System.out.println("Negative value"); //done
                       break;
               case 3: GraphicalUserInterface.getInstance().setOutputText("Wrong address");
                       System.out.println("Wrong address"); //done
                       stopExecution();
                       break;
               case 4: GraphicalUserInterface.getInstance().setOutputText("Stack is full");
                       System.out.println("Stack is full"); //done
                       stopExecution();
                       break;
               case 5: GraphicalUserInterface.getInstance().setOutputText("Stack is empty");
                       System.out.println("Stack is empty"); //done
                       stopExecution();
                       break;
               case 6: GraphicalUserInterface.getInstance().setOutputText("Overflow");
                       System.out.println("Overflow"); //done maybe
                       stopExecution();
                       break;
               case 7: GraphicalUserInterface.getInstance().setOutputText("Wrong operation code");
                       System.out.println("Wrong operation code"); //done
                       stopExecution();
                       break;
               case 8: GraphicalUserInterface.getInstance().setOutputText("DS changed");
                       System.out.println("DS changed");  //done (not sure all)
                       break;
               case 9: GraphicalUserInterface.getInstance().setOutputText("CS changed");
                       System.out.println("CS changed");  //done (not sure all)
                       break;
               case 10: GraphicalUserInterface.getInstance().setOutputText("SS changed");
                        System.out.println("SS changed"); //done (not sure all)
                        break;
            }
         }
         if(ti == 1) {
            System.out.println("Time is out");
            setTM(new char[] {'0', '9'});
            setTI(new char[] {'0', '0'});
         }
         if(si > 0) {
            switch(si) {
               case 1: GraphicalUserInterface.getInstance().setOutputText("Input command");
                       System.out.println("Input command");
                       break;
               case 2: GraphicalUserInterface.getInstance().setOutputText("Output command");
                       System.out.println("Output command");
                       break;
               case 3: GraphicalUserInterface.getInstance().setOutputText("HALT");
                       System.out.println("HALT");  //done
                       break;
            }
         }
         if(ioi > 0) {
             switch(ioi) {
               case 1: GraphicalUserInterface.getInstance().setOutputText("Interrupt in 1st channel");
                       System.out.println("Interrupt in 1st channel");
                       break;
               case 2: GraphicalUserInterface.getInstance().setOutputText("Interrupt in 2nd channel");
                       System.out.println("Interrupt in 2nd channel");
                       break;
               case 3: GraphicalUserInterface.getInstance().setOutputText("Interrupt in 3rd channel");
                       System.out.println("Interrupt in 3rd channel");
                       break;
            }
         }
         if(li > 0) {

         }
      }

      public void stopExecution() {
         setSI(new char[] {'0', '3'});
      }

    // =========== SETERS AND GETTERS ===========
      public boolean getSF() {
         char[] value = getFLAGS();
         return (value[3] == '1');
      }

      public boolean getZF() {
         char[] value = getFLAGS();
         return (value[2] == '1');
      }

    public void setESP(char[] reg) {
         if((Utilities.getInstance().charToInt(reg, 16) < 256) & ((Utilities.getInstance().charToInt(reg, 16) >= 192))) {
            this.esp = reg;   
         }
      else {
            System.out.println("Too big or too small ESP set");
         }
    }
      public boolean incESP(){
         int decValue = Utilities.charToInt(getESP(), 16);
         int decValueSS = Utilities.charToInt(getSS(), 16);
         if(decValue - decValueSS == 255) {
            return false;
         }
         else {
            ++decValue;
            String hex = Integer.toHexString(decValue);
            setESP(hex.toCharArray());
            return true;
         }
      }
      public boolean decESP() {
         int decValue = Utilities.charToInt(getESP(), 16);
         int decValueSS = Utilities.charToInt(getSS(), 16);
         if(decValue - decValueSS == 0) {
            return false;
         }
         else {
            --decValue;
            String hex = Integer.toHexString(decValue);
            setESP(hex.toCharArray());
            return true;
         }
      }
      public char[] incReg(char[] value) {
         int decValue = Utilities.getInstance().charToInt(value, 16);
         if(decValue + 1 == 255) {
            System.out.println("Cannot increase!");
            return value;
         }
         else {
            ++decValue;
            char[] hex = Integer.toHexString(decValue).toCharArray();
            return hex;
         }
      }
      public char[] decReg(char[] value) {
         int decValue = Utilities.getInstance().charToInt(value, 16);
         if(decValue - 1 < 0) {
            System.out.println("Cannot decrease!");
            return value;
         }
         else {
            --decValue;
            char[] hex = Integer.toHexString(decValue).toCharArray();
            return hex;
         }
      }

      public boolean decThreeTM() {
         int decValue = Utilities.getInstance().charToInt(RealMachine.getInstance().getTM(), 16);
         if(decValue > 2) {
            decValue = decValue - 3;
            RealMachine.getInstance().setTM(Integer.toHexString(decValue).toCharArray());
            return true;
         }
         else {
            return false;
         }
      }

      public void setCX(char[] reg) {
         if (Utilities.getInstance().charToInt(reg, 16) < 256 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
           this.cx = reg;
         } else {
           System.out.println("Too big or small CX set.");
         }
      }

    public void setDS(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 256 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
            this.ds = reg; 
        } else {
            System.out.println("Too big or small DS set");
        }
    }
    public void setCS(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 256 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
            this.cs = reg; 
        } else {
            System.out.println("Too big or small CS set");
        }
    }
    public void setSS(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 256 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
            this.ss = reg; 
        } else {
            System.out.println("Too big or small SS set");
        }
    }
    public void setPTR(char[] reg) {
      this.ptr = reg;
    }
    public void setIP(char[] reg) {
         if(Utilities.getInstance().charToInt(reg, 16) <= (getCodeSize() - 1) && Utilities.getInstance().charToInt(reg, 16) >= 0) {
            this.ip = reg;
         }
         else {
            System.out.println("Too big or small IP set");
         }
    }
    public void setFLAGS(char[] reg) {
      if ((reg[0] == '0' || reg[0] == '1') && (reg[1] == '0' || reg[1] == '1') && (reg[2] == '0' || reg[2] == '1') 
        && (reg[3] == '0' || reg[3] == '1')) {
        this.flags = reg;
      } else {
        System.out.println("Wrong input.");
      }
    }
    public void setC(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 2 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
        this.c = reg;
      } else {
        System.out.println("Wrong input."); 
      }
    }
    public void setTI(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 2 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
        this.ti = reg;
      } else {
        System.out.println("Wrong input."); 
      }
    }
    public void setPI(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 11 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
        this.pi = reg;
      } else {
        System.out.println("Wrong input."); 
      }
    }
    public void setSI(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 4 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
        this.si = reg;
      } else {
        System.out.println("Wrong input."); 
      }
    }
    public void setIOI(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 4 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
        this.ioi = reg;
      } else {
        System.out.println("Wrong input."); 
      }
    }
    public void setMODE(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 2 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
            this.mode = reg; 
        } else {
            System.out.println("Too big or small MODE set");
        }
    }
    public void setTM(char[] reg) {
      if (Utilities.getInstance().charToInt(reg, 16) < 10 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
        this.tm = reg;
      } else {
        System.out.println("Wrong input.");  
      }
    }
      public void setLI(char[] reg) {
        if (Utilities.getInstance().charToInt(reg, 16) < 2 && Utilities.getInstance().charToInt(reg, 16) >= 0) {
          this.li = reg;
        } else {
          System.out.println("Wrong input.");  
        }
      }
      public void setCodeSize(int reg) {
        this.codeSize = reg;
      }
      public char[] getCX() {
         return this.cx;
      }
    public char[] getESP() {
      return this.esp;
    }
    public char[] getDS() {
      return this.ds;
    }
    public char[] getCS() {
      return this.cs;
    }
    public char[] getSS() {
      return this.ss;
    }
    public char[] getPTR() {
      return this.ptr;
    }
      public char[] getHalfPTR() {
         return new char[] {'0', '0', getPTR()[2], getPTR()[3]};
      }
    public char[] getIP() {
      return this.ip;
    }
    public char[] getFLAGS() {
      return this.flags;
    }
    public char[] getC() {
      return this.c;
    }
    public char[] getTI() {
      return this.ti;
    }
    public char[] getPI() {
      return this.pi;
    }
    public char[] getSI() {
      return this.si;
    }
    public char[] getIOI() {
      return this.ioi;
    }
    public char[] getMODE() {
      return this.mode;
    }
    public char[] getTM() {
      return this.tm;
    }
      public Memory getRAM() {
         return this.ram;
      }

      public char[] getLI() {
         return this.li;
      }

      public Swapping getSwapping() {
         return this.swapping;
      }
      public int getCodeSize() {
        return this.codeSize;
      }
    // ============================================

}