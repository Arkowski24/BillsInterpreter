import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentSystem {

    public BillDocument readDocument(String filepath) throws IOException {
        List<String> documentLines = new ArrayList<>();

        try (BufferedReader documentReader = new BufferedReader(new FileReader (filepath))){

            String line = documentReader.readLine();
            while (line != null) {
                documentLines.add(line);
                line = documentReader.readLine();
            }
        }
        catch (IOException e){
            throw new IOException("Document could not be read.", e);
        }

        return new BillDocument(documentLines);
    }
}
