package org.example;

import com.google.common.base.Utf8;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    //Replace YOUR_API_KEY with your actual API key
    String apiKey = "35f9736be042660d1010";
    static String pathOfFilesToTranslate = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/original";
    static String pathOfTranslatedFiles = "/Users/cyilmaz/Projects/text-replacer/src/main/resources/translated";

    //Set the source and target languages
    String srcLang = "tr";
    String tgtLang = "en";

    //Set the API endpoint and headers
    static String endpoint = "http://api.mymemory.translated.net/get";
    static String fullEndpoint = "http://api.mymemory.translated.net/get?langpair=tr|en&mt=1&q=Yönetici Detayı";
    String headers = "Content-Type': 'application/x-www-form-urlencoded";

    //Extract the text from the elements you want to translate
    //all_soup_tags = soup.find_all(tags)
    //texts = [element.text for element in all_soup_tags]

    public static void main(String[] args) {
        JSONObject cache = getCache();
        List<String> files = new ArrayList<>();
        List<String> filenames = getFilenamesRecursively(pathOfFilesToTranslate, files);

        cleanDirectory();
        filenames.forEach(filename -> {
            translateNewOne(new File(filename), cache);
        });

        saveOfflineCache(cache);
    }

    private static void cleanDirectory() {
        try {
            FileUtils.cleanDirectory(new File(pathOfTranslatedFiles + "/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject getCache() {
        String cacheFile = null;
        try {
            cacheFile = Files.readString(Path.of("/Users/cyilmaz/Projects/text-replacer/src/main/resources/cache/translations.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JSONObject(cacheFile);
    }

    private static void saveOfflineCache(JSONObject cacheFile) {
        try {
            Files.writeString(Path.of("/Users/cyilmaz/Projects/text-replacer/src/main/resources/cache/translations.json"), cacheFile.toString());
        } catch (IOException e) {
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

    public static List<String> getFilenamesRecursively(String pathOfFilesToTranslate, List<String> files){
        File mainFolder = new File(pathOfFilesToTranslate);
        File[] subdirs = mainFolder.listFiles();

        for (File file : subdirs){

            if (file.isDirectory()){
                getFilenamesRecursively(file.getAbsolutePath(), files);
            }else {
                //
                if (file.isFile()){
                    files.add(file.getAbsolutePath());
                    //translateNewOne(cache, file);
                }
            }
        }
        return files;
    }

    private static void translateNewOneNewLibrary(File file, JSONObject cache) {

    }

    private static void translateNewOne(File file, JSONObject cache) {
        try {
            Document document = Jsoup.parse(file + "UTF-8");

            // Filter tags and crappy elements
            for(Element e: document.getAllElements()){
                if (e.hasText()){
                    if (!e.html().contains("<") &&
                        !e.html().contains("?") &&
                        !e.html().contains("...") &&
                        !e.html().contains(">")
                    ){
                        String textToTranslate = e.html();

                        String translation = null;
                        if (cache.has(textToTranslate)){
                            translation = cache.get(textToTranslate).toString();
                        }

                        // ##TODO 2. make api call for translation
                        if (translation == null || translation.isEmpty() || translation.isBlank()){
                            translation = apiCall(textToTranslate);//translate(textToTranslate);
                            cache.put(textToTranslate, translation);
                        }

                        e.html(translation);

                        System.out.println("successfully translated :"+ textToTranslate + ":"+translation);
                    }
                }
            }


            File translatedFile = new File(file.getPath().substring(0, 56)+"/translated/"+file.getPath().substring(66));
            translatedFile.getParentFile().mkdirs();
            if (translatedFile.createNewFile()) {
                PrintWriter writer = new PrintWriter(translatedFile, StandardCharsets.UTF_8);
                writer.println(document.toString());
                writer.close();
            }else{
                throw new Exception("File cannot created");
            }

        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public static String apiCall(String query) {
        try{
            String url = "https://api.mymemory.translated.net/get?langpair=tr|en&mt=1&q="+ URLEncoder.encode(query, StandardCharsets.UTF_8);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print in String
            System.out.println(response.toString());
            //Read JSON response and print
            JSONObject myResponse = new JSONObject(response.toString());
            JSONObject myResponseData = new JSONObject(myResponse.get("responseData").toString());

            if (Objects.equals(myResponse.get("responseStatus"), 200)){
                return myResponseData.getString("translatedText");
            }

            System.out.println("Api call error");

            return "true";
        }catch (IOException e){
            System.out.println(e.getMessage());
            return "false";
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

