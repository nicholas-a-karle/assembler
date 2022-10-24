import java.io.*;

public class Parser {
    
    BufferedReader in;
    String currentInstruction;
    int eqp;
    int smclnp;
    int iType;

    public Parser(String file){
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        currentInstruction = "";
        advance();
    }

    public void resetFile() {
        in = new BufferedReader(in);
    }

    public void closeFile() {
        try {
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setFile(String file) {
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        currentInstruction = "";
        advance();
    }

    /**
     * Checks if there are any more lines in the program
     * @return
     */
    public boolean hasMoreLines() {
        try {
            return in.ready();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the next instruction and makes it the current instruction
     * @return next instruction
     */
    public String advance() {
        try {
            currentInstruction = in.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            currentInstruction = "";
        }
        eqp = currentInstruction.indexOf('=');
        smclnp = currentInstruction.indexOf(';');
        switch(currentInstruction.charAt(0)) {
            case '@':
                iType = 1;
                break;
            case 'A':
            case 'D':
            case 'M':
                iType = 2;
                break;
            case '(':
                iType = 3;
                break;
            case '/':
                iType = 0;
                break;
            default:
                iType = 0;
                break;
        }
        return currentInstruction;
    }

    /**
     * -1- ERROR
     * 0 - COMMENT or WHITESPACE
     * 1 - A    @xxx, set address
     * 2 - C    dest=comp;jump
     * 3 - L    label
     * @return instruction type
     */
    public int instructionType() {
        return iType;
    }

    /**
     * 
     * @return instruction's symbol
     */
    public String symbol() {
        switch(iType) {
            case 0:
                //return nothing
                return "";
            case 1:
                //first character is '@'
                return currentInstruction.substring(1);
            case 2:
                //return nothing
                return "";
            case 3:
                //return every except first and last character as they are '(' and ')' respectively
                return currentInstruction.substring(1, currentInstruction.length() - 1);
            default:
                return "";
        }
    }

    /**
     * 
     * @return dest field
     */
    public String dest() {
        return currentInstruction.substring(0, eqp);
    }

    /**
     * 
     * @return comp field
     */
    public String comp() {
        return currentInstruction.substring(eqp + 1, smclnp);
    }

    /**
     * 
     * @return jump field
     */
    public String jump() {
        if (smclnp == -1) return "null";
        String jmp = currentInstruction.substring(smclnp + 1);
        if (jmp == "") return "null";
        return jmp;
    }
    

}
