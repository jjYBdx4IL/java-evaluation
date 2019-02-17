package org.apache.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class EnglishAnalyzerTest {

    public static final String CONTENTS = "contents";

    private final Analyzer analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
    private TokenStream tokenStream;

    @Test // https://tartarus.org/martin/PorterStemmer/java.txt
    public void displayTokenUsingEnglishAnalyzer() throws IOException {
        String text = "Lucene is simple yet powerful java based search library.";
        tokenStream = analyzer.tokenStream(CONTENTS, new StringReader(text));
        CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.print("[" + term.toString() + "] ");
        }
        System.out.println(analyzer.normalize(CONTENTS, "lucene").utf8ToString());
    }

}
