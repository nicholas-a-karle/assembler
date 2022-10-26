import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Assembler {

    HashMap<String, Integer> symbols;
    int newVarMem;
    int labelMem;

    BufferedReader r;
    BufferedWriter w;

    public Assembler() {
        symbols = new HashMap<String, Integer>();
        resetAssembler();
    }

    public void resetAssembler() {
        symbols.clear();
        symbols.put("R0", 0);
        symbols.put("R1", 1);
        symbols.put("R2", 2);
        symbols.put("R3", 3);
        symbols.put("R4", 4);
        symbols.put("R5", 5);
        symbols.put("R6", 6);
        symbols.put("R7", 7);
        symbols.put("R8", 8);
        symbols.put("R9", 9);
        symbols.put("R10", 10);
        symbols.put("R11", 11);
        symbols.put("R12", 12);
        symbols.put("R13", 13);
        symbols.put("R14", 14);
        symbols.put("R15", 15);
        symbols.put("SP", 0);
        symbols.put("LCL", 1);
        symbols.put("ARG", 2);
        symbols.put("THIS", 3);
        symbols.put("THAT", 4);
        symbols.put("SCREEN", 16384);
        symbols.put("KBD", 24576);
        newVarMem = 16;
        labelMem = 0;
    }

    public boolean setInputFile(String filePath) throws IOException {
        try {
            r = new BufferedReader(new FileReader(filePath));
            return r.ready();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setOutputFile(String filePath) throws IOException {
        try {
            w = new BufferedWriter(new FileWriter(filePath));
            return true;
        } catch (IOException e) {
            File file = new File(filePath);
            file.createNewFile();
            if (file.exists()) {
                w = new BufferedWriter(new FileWriter(filePath));
                return true;
            } else return false;
        }
    }

    public String assembleFile(String inputFilePath) throws IOException, Exception {
        if (inputFilePath.contains(".")) {
            return assembleFile(inputFilePath, 
                inputFilePath.substring(0, inputFilePath.indexOf('.')) + ".hack"
            );
        } else {
            return assembleFile(inputFilePath, 
                inputFilePath + ".hack"
            );
        }
    }

    public boolean isBinary(String str) {
        if (str.length() < 1) return false;
        for (char each: str.toCharArray()) {
            if (each != '1' && each != '0') return false;
        }
        return true;
    }

    public String assembleFile(String inputFilePath, String outputFilePath) throws IOException, Exception {
        if (!setInputFile(inputFilePath)) throw (new Exception("Input File Not Found"));
        if (!setOutputFile(outputFilePath)) throw (new Exception("Output File Not Found"));
        resetAssembler();

        String line = "";
        String bin = "";
        while ((line = r.readLine()) != null) {
            parseLine(line, true);
        }
        if (!setInputFile(inputFilePath)) throw (new Exception("Input File Not Found"));
        while ((line = r.readLine()) != null) {
            bin = parseLine(line, false);
            System.out.println(line + "\t\t" + bin);
            if (isBinary(bin)) { w.write(bin); w.newLine();}
        }

        /*System.out.println("SYMBOL TABLE:::::::::::::::::::::::::::::::::::::::::::;");
        for (String each : symbols.keySet()) {
            System.out.println(each + " :\t" + symbols.get(each));
        }*/

        r.close();
        w.close();
        return outputFilePath;
    }

    public String parseLine(String line, boolean labelSearch) throws Exception {
        String binary = "";

        // eliminate whitespace
        // "\t\r\n "
        line = line.replaceAll("\s+", "");

        // send reasons to skip this line at parse
        // errors
        // Empty line or whitespace
        if (line.length() < 1) return("Attempt to Parse Empty Line");
        // Only 1 symbol
        if (line.length() < 2) return("Attempt to Parse Short Line");
        // Only one side of equals is filled out
        if (line.contains("=") && line.length() < 3) return("Attempt to Parse Short Line for equation instruction");
        // Value and jmp plus smcln
        if (line.contains(";") && line.length() < 5) return("Attempt to Parse Short Line for jump instruction");
        // parentheses exist but in wrong spot
        if (line.contains("(") &&
                line.contains(")") &&
                (line.indexOf('(') != 0 || line.indexOf(')') != line.length() - 1)) 
                return("Attempt to Parse with incorrect Parentheses");
        // One parentheses is present but not the other
        if (!line.contains("(") && line.contains(")")) return("Attempt to Parse with unclosed Parentheses");
        if (line.contains("(") && !line.contains(")")) return("Attempt to Parse with unclosed Parentheses");
        // At symbol someone random af in line
        if (line.contains("@") && line.indexOf('@') != 0) return("Attempt to Parse with at symbol in incorrect location");

        // find instruction type
        char inType = 'I';
        /*
         * I=Ignore
         * E=Error
         * C=Comp/Jump
         * A=Address
         * L=Label
         */
        switch (line.charAt(0)) {
            case '@':
                inType = 'A';
                break;
            case '(':
                inType = 'L';
                break;
            case 'A':
            case 'D':
            case 'M':
            case '0':
            case '1':
                inType = 'C';
                break;
            case '-':
                if (line.charAt(1) == '1' ||
                        line.charAt(1) == 'A' ||
                        line.charAt(1) == 'D')
                    inType = 'C';
                else
                    inType = 'E';
                break;
            case '!':
                if (line.charAt(1) == 'A' ||
                        line.charAt(1) == 'D')
                    inType = 'C';
                else
                    inType = 'E';
                break;
            case '/':
                if (line.charAt(1) == '/')
                    inType = 'I';
                else
                    inType = 'E';
                break;
            default:
                inType = 'E';
                break;
        }

        switch (inType) {
            case 'I':
                // do nothing, ignore
                break;
            case 'E':
                // error, do nothing but report error
                break;
            case 'C':
                if (labelSearch) ++labelMem;
                else binary = cToBin(line);
                break;
            case 'A':
                if (labelSearch) ++labelMem;
                else binary = aToBin(line);
                break;
            case 'L':
                if (labelSearch) createLabelFromline(line);
                break;
        }
        return binary;
    }

    void createLabelFromline(String line) throws Exception {
        if (line.length() < 2) throw (new Exception("String passed to function too short"));
        //chop off ( and )
        line = line.substring(1, line.length() - 1);
        symbols.put(line, labelMem);
    }

    int createVariable(String symbol) {
        symbols.put(symbol, newVarMem);
        return newVarMem++;
    }

    boolean isInteger(String str) {
        for (char each: str.toCharArray()) if (!Character.isDigit(each)) return false;
        return true;
    }

    String aToBin(String line) {
        // 0 vvvvvvvvvvvvvvv
        String binary = "";
        //chop off @
        line = line.substring(1);
        // if numeric, just convert value to binary
        if (isInteger(line)) {
            binary += Integer.toBinaryString(Integer.valueOf(line));
        } else { // if symbol, lookup in dictionary
            if (symbols.containsKey(line)) { // if in, use that value
                binary += Integer.toBinaryString(symbols.get(line));
            } else { // if not in, create variable
                binary += Integer.toBinaryString(createVariable(line));
            }
        }

        while (binary.length() < 16) binary = "0" + binary;
        return binary;
    }   

    String cToBin(String line) {
        // 111 a cccccc ddd jjj
        // 111 a cccccc ADM jjj

        // start
        String binary = "111";

        // a
        if (line.contains("M"))
            binary += "1";
        else
            binary += "0";

        int eq = line.indexOf('=');
        int sc = line.indexOf(';');

        String comp = "";
        String dest = "";
        String jump = "";

        if (eq == -1 && sc == -1) { // comp (jump, dest = 000, 000)
            comp = line;
        } else if (eq != -1 && sc == -1) { // dest=comp (jump=000)
            dest = line.substring(0, eq);
            comp = line.substring(eq + 1);
        } else if (eq == -1 && sc != -1) { // comp;jump (dest=000)
            comp = line.substring(0, sc);
            jump = line.substring(sc + 1);
        } else { // dest=comp;jump
            dest = line.substring(0, eq);
            comp = line.substring(eq + 1, sc);
            jump = line.substring(sc + 1);
        }

        // comp
        // replace M with D for switch
        comp = comp.replaceAll("M", "A");
        switch (comp) {
            // 0 101010
            case "0":
                binary += "101010";
                break;
            // 1 111111
            case "1":
                binary += "111111";
                break;
            // -1 111010
            case "-1":
                binary += "111010";
                break;
            // D 001100
            case "D":
                binary += "001100";
                break;
            // A 110000
            case "A":
                binary += "110000";
                break;
            // !D 001101
            case "!D":
                binary += "001101";
                break;
            // !A 110001
            case "!A":
                binary += "110001";
                break;
            // -D 001111
            case "-D":
                binary += "001111";
                break;
            // -A 110011
            case "-A":
                binary += "110011";
                break;
            // D+1 011111
            case "D+1":
            case "1+D":
                binary += "011111";
                break;
            // A+1 110111
            case "A+1":
            case "1+A":
                binary += "110111";
                break;
            // D-1 001110
            case "D-1":
                binary += "001110";
                break;
            // A-1 110010
            case "A-1":
                binary += "110010";
                break;
            // D+A 000010
            case "D+A":
            case "A+D":
                binary += "000010";
                break;
            // D-A 010011
            case "D-A":
                binary += "010011";
                break;
            // A-D 000111
            case "A-D":
                binary += "000111";
                break;
            // D&A 000000
            case "D&A":
            case "A&D":
                binary += "000000";
                break;
            // D|A 010101
            case "D|A":
            case "A|D":
                binary += "010101";
                break;
            default:
                binary += "000000";
                break;
        }

        // dest
        if (dest.contains("A"))
            binary += "1";
        else
            binary += "0";
        if (dest.contains("D"))
            binary += "1";
        else
            binary += "0";
        if (dest.contains("M"))
            binary += "1";
        else
            binary += "0";

        // jump
        switch (jump) {
            case "":
                binary += "000";
                break;
            case "JGT":
                binary += "001";
                break;
            case "JEQ":
                binary += "010";
                break;
            case "JGE":
                binary += "011";
                break;
            case "JLT":
                binary += "100";
                break;
            case "JNE":
                binary += "101";
                break;
            case "JLE":
                binary += "110";
                break;
            case "JMP":
                binary += "111";
                break;
            default:
                binary += "000";
                break;
        }
        return binary;
    }

}
