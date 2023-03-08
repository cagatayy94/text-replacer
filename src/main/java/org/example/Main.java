package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    //Replace YOUR_API_KEY with your actual API key
    String apiKey = "35f9736be042660d1010";
    static String pathOfFilesToTranslate = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/original/messages_tr.properties";
    static String pathOfTranslatedFiles = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/html-translated";

    //Set the source and target languages
    String srcLang = "tr";
    String tgtLang = "en";

    //Set the API endpoint and headers
    static String endpoint = "http://api.mymemory.translated.net/get";
    String headers = "Content-Type': 'application/x-www-form-urlencoded";

    //Extract the text from the elements you want to translate
    //all_soup_tags = soup.find_all(tags)
    //texts = [element.text for element in all_soup_tags]




    public static void main(String[] args) {
        replaceTextByWordDoc();
    }

    public static void replaceTextByWordDoc(){
        String messagesTrPath = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/original/messages_tr.properties";
        String messagesRuPath = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/original/messages_ru.properties";
        String excelFilePath = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/original/Translations.xlsx";

        Path pathOfmessagesTr = Paths.get(messagesTrPath);
        Path pathOfmessagesRu = Paths.get(messagesRuPath);
        Path pathOfWordFile = Paths.get(excelFilePath);


        HashMap<String, String> trRuMap = extracted(pathOfmessagesTr, pathOfmessagesRu, pathOfWordFile);



    }

    private static HashMap<String, String> extracted(Path pathOfmessagesTr, Path pathOfmessagesRu, Path pathOfWordFile) {
        try {
            String messagesTr = Files.readString(pathOfmessagesTr, StandardCharsets.UTF_8);
            System.out.println(messagesTr);

            String messagesRu = Files.readString(pathOfmessagesRu, StandardCharsets.UTF_8);
            System.out.println(messagesRu);

            FileInputStream file = new FileInputStream(pathOfWordFile.toFile());

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            HashMap<String, String> trRuMap = new HashMap<>();
            List<String> cols = new ArrayList<>();

            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                int colOrder = 0;
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    cols.add(colOrder, cell.getStringCellValue() + "\t");
                    colOrder++;
                }

                trRuMap.put(cols.get(0), cols.get(1));
            }

            System.out.println(trRuMap.get("deneme"));
            file.close();

            return trRuMap;




        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public static void getFilenamesRecursively(File folder, JSONObject cache){
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles){

            if (file.isDirectory()){
                getFilenamesRecursively(file, cache);
                continue;
            }else {
                if (file.isFile()){
                    try {
                        Document document = Jsoup.parse(file);

                        // Filter tags and crappy elements
                        for(Element e: document.getAllElements()){
                            if (e.hasText()){
                                if (!e.html().contains("<") &&
                                    !e.html().contains("?") &&
                                    !e.html().contains(">")
                                ){
                                    String textToTranslate = e.html();

                                    // ##TODO 1. check the text in cache if its there get translate from there
                                    // ##TODO 2. make api call for translation
                                    // ##TODO 3. save result to cache

                                    String translation = null;
                                    if (cache.has(textToTranslate.toLowerCase())){
                                        translation = cache.get(textToTranslate.toLowerCase()).toString();
                                    }

                                    if (translation == null || translation.isEmpty() || translation.isBlank()){
                                        translation = translate(textToTranslate);
                                    }

                                    e.html(translation);

                                    System.out.println("successfully translated :"+ textToTranslate + ":"+translation);
                                }
                            }
                        }

                        File translatedFile = new File(pathOfTranslatedFiles+"/"+ file.getPath().substring(71));
                        translatedFile.getParentFile().mkdirs();
                        if (translatedFile.createNewFile()) {
                            PrintWriter writer = new PrintWriter(translatedFile, StandardCharsets.UTF_8);
                            writer.println(document.html());
                            writer.close();
                        }else{
                            throw new Exception("File cannot created");
                        }

                    }catch (Exception e){
                        System.err.println(e.getMessage());
                    }
                }
            }

        }
    }

    public static String translate(String target){
        URL url = null;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Map<String, String> parameters = new HashMap<>();
            parameters.put("langpair", "tr|en");
            parameters.put("mt", "0");
            parameters.put("q", target.toLowerCase());
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));

            BufferedReader br = null;
            if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String responseBody = br.lines().collect(Collectors.joining());
            int responsecode = conn.getResponseCode();

            out.flush();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "asd";
    }
}



