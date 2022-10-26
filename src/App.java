import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws Exception {
        Assembler asm = new Assembler();
        String p = "C:\\Users\\Nickk\\OneDrive\\Desktop\\nand2tetris\\projects\\06\\assembler\\src\\files\\";
        asm.assembleFile(p + "add\\Add.asm");
        asm.assembleFile(p + "max\\Max.asm");
        asm.assembleFile(p + "max\\MaxL.asm");
        asm.assembleFile(p + "pong\\Pong.asm");
        asm.assembleFile(p + "pong\\PongL.asm");
        asm.assembleFile(p + "rect\\Rect.asm");
        asm.assembleFile(p + "rect\\RectL.asm");
    }
}
