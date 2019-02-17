package org.apache.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class StandardAnalyzerTest {

    public static final String CONTENTS = "contents";

    Analyzer analyzer = new StandardAnalyzer();
    
    @Test
    public void displayTokenUsingStandardAnalyzer() throws IOException {
        String text = "Lucene is simple yet powerful java based search library.";
        TokenStream tokenStream = analyzer.tokenStream(CONTENTS, new StringReader(text));
        CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.print("[" + term.toString() + "] ");
        }
    }

}
