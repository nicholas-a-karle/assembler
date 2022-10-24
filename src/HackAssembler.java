import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HackAssembler {

    BufferedWriter out;
    Code code;
    Parser parser;
    SymbolTable symTab;
    
    public HackAssembler(String outFile) {
        try {
            out = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void assembleFile(String inFile) {
        parser = new Parser(inFile);
        symTab = new SymbolTable();

        String symb, comp, dest, jump;

        int address = 0;
        while (parser.hasMoreLines()) {
            if (parser.instructionType() == 3) {
                symTab.addEntry(parser.symbol(), address);
            } else if (parser.instructionType() != 0) {
                ++address;
            }
            parser.advance();
        }

        parser.resetFile();

        while (parser.hasMoreLines()) {
            switch (parser.instructionType()) {
                case -1: //ERROR
                    //do nothing and hope code works
                    break;
                case 0: //COMMENT
                    //do nothing
                    break;
                case 1: //A type (@xxx)
                    symb = parser.symbol();
                    try {
                        out.write("0" + Integer.toBinaryString(Integer.valueOf(symb)));
                    } catch (NumberFormatException | IOException e) {
                        //add symbol or get
                        int symbValue = 0;
                        if (symTab.contains(symb)) {
                            symbValue = symTab.getAddress(symb);
                        } else {
                            symTab.addVariable(symb);
                            symbValue = symTab.getAddress(symb);
                        }
                        try {
                            out.write("0" + Integer.toBinaryString(Integer.valueOf(symbValue)));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    break;
                case 2: //C type (dest=comp;jump)
                    comp = code.comp(parser.comp());
                    dest = code.dest(parser.dest());
                    jump = code.jump(parser.jump());

                    try {
                        out.write("111" + comp + dest + jump);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3: //LABEL, should ignore on this loop
                    break;
            }
            parser.advance();            
        }

        parser.closeFile();
    }
}
