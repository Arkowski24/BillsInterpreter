public class ConsumersBillDocumentSystem extends DocumentSystem {


    private void fillBillParser(){
        ParserRule parserRule = new ParserRule("(^[0-9]{3}\\.)|(^[0-9]{2}\\.)|(^[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule parserRule1 = new ParserRule("(^Art.\\s[0-9]{3}\\.)|(Art.\\s[0-9]{2}\\.)|(Art.\\s[0-9]{1}\\.)", ParserRuleType.Unlimited);
        ParserRule parserRule2 = new ParserRule("(Rozdział [LCDMIVX]{4})|(Rozdział [LCDMIVX]{3})|(Rozdział [LCDMIVX]{2})|(Rozdział [LCDMIVX])", ParserRuleType.Unlimited);
        parserRule1.subRules.add(parserRule);
        parserRule2.subRules.add(parserRule1);
    }
}
