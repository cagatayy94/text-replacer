package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    //Replace YOUR_API_KEY with your actual API key
    String apiKey = "35f9736be042660d1010";
    List<Translations> translations = new ArrayList<>();
    List<String> fileNames = new ArrayList<>();

    // Walk through the directory tree and get the names of all files
    String path = "files";

    //Tags to find
    List<String> tags = Arrays.asList(
            "h1",
            "li",
            "h3",
            "label",
            "option",
            "th",
            "button",
            "small");

    //Set the source and target languages
    String srcLang = "tr";
    String tgt_lang = "en";

    //Set the API endpoint and headers
    String endpoint = "https://api.mymemory.translated.net/get";
    String headers = "Content-Type': 'application/x-www-form-urlencoded";

    //Extract the text from the elements you want to translate
    //all_soup_tags = soup.find_all(tags)
    //texts = [element.text for element in all_soup_tags]




    public static void main(String[] args) {
        //jSoupParse();
        jSoupParseFromHtmlText();
    }

    public static void jSoupParse() {

        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/").get();

            //log(doc.title());
            Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                System.out.println(headline.attr("title"));
                System.out.println(headline.absUrl("href"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void jSoupParseFromHtmlText(){
        File file = new File("/Users/cyilmaz/Projects/text-replacer/src/main/java/org/example/index.html");

        try {
            Document doc = Jsoup.parse(file);
            for (Element sentence : doc.getAllElements()){
                if (sentence.children().isEmpty()){
                    System.out.println(sentence.text());
                }
            }
        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}


