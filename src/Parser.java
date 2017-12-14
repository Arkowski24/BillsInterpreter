import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    Scanner fileScanner;

    Parser(String pathToFile) throws IOException {
        try {
            fileScanner = new Scanner(new BufferedReader(new FileReader (pathToFile)));

        }
        catch (IOException e){

        }
        finally {

        }
    }
}
