public class Code {

    /**
     * 
     * @param dest String version of dest field
     * @return Binary version of dest field
     */
    public String dest (String dest) {
        //ADM
        String bin = "";
        if (dest.contains("A")) bin += '1';
        else bin += '0';
        if (dest.contains("D"))  bin += '1';
        else bin += '0';
        if (dest.contains("M"))  bin += '1';
        else bin += '0';
        return bin;
    }

    /**
     * 
     * @param comp String version of the comp field
     * @return Binary version of the comp field
     */
    public String comp (String comp) {
        char a = 0;  //if M instead of A
        if (comp.contains("M")) a = '1';
        else a = '0';
        //now convert M to A for conveinence
        comp.replace("M", "A");
        switch (comp) {
            case "0":
                return a + "101010";
            case "1":
                return a + "111111";
            case "-1":
                return a + "111010";
            case "D":
                return a + "001100";
            case "A":
                return a + "110000";
            case "!D":
                return a + "001101";
            case "!A":
                return a + "110001";
            case "-D":
                return a + "001111";
            case "-A":
                return a + "110011";
            case "D+1": case "1+D":
                return a + "011111";
            case "A+1": case "1+A":
                return a + "110111";
            case "D-1":
                return a + "001110";
            case "A-1":
                return a + "110010";
            case "D+A": case "A+D":
                return a + "000010";
            case "D-A":
                return a + "010011";
            case "A-D":
                return a + "000111";
            case "D&A": case "A&D":
                return a + "000000";
            case "D|A": case "A|D":
                return a + "010101";
            default:
                return "000000";
        }
        
    }

    /**
     * 
     * @param jump String version of the jump field
     * @return Binary version of the jump field
     */
    public String jump (String jump) {
        switch (jump) {
            case "null":
                return "000";
            case "JGT":
                return "001";
            case "JEQ":
                return "010";
            case "JGE":
                return "011";
            case "JLT":
                return "100";
            case "JNE":
                return "101";
            case "JLE":
                return "110";
            case "JMP":
                return "111";
            default:
                return "000";
        }
    }
}
