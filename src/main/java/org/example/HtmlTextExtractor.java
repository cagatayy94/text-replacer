package org.example;

import java.io.IOException;
import java.io.InputStream;

import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

public class HtmlTextExtractor {
    public static String extract(InputStream is) throws IOException {
        Source source = new Source(is);
        source.fullSequentialParse();

        TextExtractor extractor = source.getFirstElement(HTMLElementName.BODY).getTextExtractor();
        extractor.setConvertNonBreakingSpaces(true);
        extractor.setExcludeNonHTMLElements(false);
        extractor.setIncludeAttributes(false);
        return extractor.toString();
    }

}