package DocumentSystem;

import Cleaner.*;
import Parser.*;

import java.io.IOException;

public class ConsumersBillDocumentSystem extends PolishDocumentSystem {
    public ConsumersBillDocumentSystem(String filepath) throws IOException {
        super();
        readDocument(filepath);
        fillCleanerRules();
        fillConsumersParser();
        cleaner.clearDocument(billDocument);
        parser.parseDocument(billDocument);
    }

    private void fillCleanerRules(){
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("©Kancelaria Sejmu", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[a-zA-Z]$", CleanerRuleType.DeleteLineWithPhrase));
        cleaner.addNewCleanRule(new CleanerRule("^[0-9]$", CleanerRuleType.DeleteLineWithPhrase));
    }

    private void fillConsumersParser(){
        ParserRule litera = new ParserRule("((?m)^[0-9a-z]{3}\\))|((?m)^[0-9a-z]{2}\\))|((?m)^[0-9a-z]{1}\\))", ParserRuleType.Unlimited);
        ParserRule ustep = new ParserRule("((?m)^[0-9]{3}[a-z]{1}\\.)|((?m)^[0-9]{3}\\.)" +
                "|((?m)^[0-9]{2}[a-z]{1}\\.)|((?m)^[0-9]{2}\\.)" +
                "|((?m)^[0-9]{1}[a-z]{1}\\.)|((?m)^[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule artykul = new ParserRule("(Art.\\s[0-9]{3}[a-z]{1}\\.)|(Art.\\s[0-9]{3}\\.)" +
                "|(Art.\\s[0-9]{2}[a-z]{1}\\.)|(Art.\\s[0-9]{2}\\.)" +
                "|(Art.\\s[0-9]{1}[a-z]{1}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule artykul2 = new ParserRule("(Art.\\s[0-9]{3}[a-z]{1}\\.)|(Art.\\s[0-9]{3}\\.)" +
                "|(Art.\\s[0-9]{2}[a-z]{1}\\.)|(Art.\\s[0-9]{2}\\.)" +
                "|(Art.\\s[0-9]{1}[a-z]{1}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.NoMatch);
        ParserRule rozdzial = new ParserRule("(Rozdział [0-9]{4})|(Rozdział [0-9]{3})|(Rozdział [0-9]{2})|(Rozdział [0-9])", ParserRuleType.Unlimited);
        ParserRule dzial = new ParserRule("(DZIAŁ [LCDMIVX]{4})|(DZIAŁ [LCDMIVX]{3})|(DZIAŁ [LCDMIVX]{2})|(DZIAŁ [LCDMIVX])", ParserRuleType.Unlimited);
        ustep.addSubRule(litera);
        artykul.addSubRule(ustep);
        artykul2.addSubRule(ustep);
        rozdzial.addSubRule(artykul);
        dzial.addSubRule(rozdzial);
        dzial.addSubRule(artykul2);

        parser.addParserRule(dzial);
    }

}
