package dbc.examples.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import java.io.StringReader;
import java.io.IOException;
import java.util.Properties;

public class AnalysisVisualizer {

    private Analyzer[] analyzers;

    public AnalysisVisualizer(){
        
        analyzers = new Analyzer[]{
            new WhitespaceAnalyzer(),
            new SimpleAnalyzer(),
            new StopAnalyzer(),
            new StandardAnalyzer(),
            new SnowballAnalyzer("Danish", StopAnalyzer.ENGLISH_STOP_WORDS),
            new SnowballAnalyzer("English", StopAnalyzer.ENGLISH_STOP_WORDS),
        };
    }

    public static void main(String[] args) throws IOException {

        String[] strings = {
            "Jeg er en lille allehåndekrydderisholderhylde",
            "The a of an A"
        };

        AnalysisVisualizer av = new AnalysisVisualizer();

        for (int i = 0; i < strings.length; i++) {
            av.analyze(av, strings[i]);
        }
    }

    private static void analyze(AnalysisVisualizer av, String text) throws IOException {
        System.out.println("Analyzing \"" + text + "\"");
        for (int i = 0; i < av.analyzers.length; i++) {
            System.out.printf( "nr. %s af %s", i+1, av.analyzers.length );
            Analyzer analyzer = av.analyzers[i];

            System.out.println("\t" + analyzer.getClass().getName() + ":");
            System.out.print("\t\t");
            TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
            while (true) {
                Token token = stream.next();
                if (token == null) break;

                System.out.print("[" + token.termText() + "] ");
            }
            System.out.println("\n");
        }
    }

}