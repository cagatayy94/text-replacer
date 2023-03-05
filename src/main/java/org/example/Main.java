package org.example;

import com.google.common.io.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    //Replace YOUR_API_KEY with your actual API key
    String apiKey = "35f9736be042660d1010";
    static String pathOfFilesToTranslate = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/html-original";

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
        //jSoupParseFromHtmlText();
        ;
        getFilenamesRecursively(new File(pathOfFilesToTranslate));
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

    public static void executeTranslations(){
        //1. get all file names with absolute path recursively
        //2. loop straight all off them find text and translate text update texts and save as file
    }

    public static void getFilenamesRecursively(File folder){
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles){
            if (file.isDirectory()){
                getFilenamesRecursively(file);
                continue;
            }else {
                if (file.isFile()){
                    try {
                        Document document = Jsoup.parse(file);


                        CharSequence charSequence = "asdsadadsa";



                        List<Element> elements = document.getAllElements().stream()
                                .filter( element -> element.hasText())
                                .filter( element -> element.text().contains(charSequence))
                                .collect(Collectors.toList());
                        break;

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                    //  ##TODO translate
                    System.out.println(file.getAbsolutePath());
                }
            }
            break;

        }
    return;
    }
}


