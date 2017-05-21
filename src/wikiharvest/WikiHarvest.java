/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikiharvest;

/**
 *
 * @author Grzes
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Grzes
 */
public class WikiHarvest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {
        String source = "";
        ArrayList links = new ArrayList();
        ArrayList splinks = new ArrayList();
        ArrayList nazwy = new ArrayList();
        ArrayList powiazania = new ArrayList();
        ArrayList wszystkielinki = new ArrayList();
        try {
            source = getUrlSource("https://pl.wikipedia.org/wiki/Halloween");
        } catch (IOException ex) {
            Logger.getLogger(WikiHarvest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println(source);
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        Scanner in = new Scanner(System.in);
        String a = in.nextLine();
        url = new URL("https://en.wikipedia.org/wiki/" + a);
        for (int i = 0; i < 60; i++) {

            // String ab = in.nextLine();
            try {

                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));

                while ((line = br.readLine()) != null) {
                    //  System.out.println(line);
                    /*&&line.contains("Objawy+")&&line.contains("http")*/

                    if (line.contains("href=") && line.contains("title=") && !line.contains("http")) {
                        if (!nazwy.contains(removeTillWord(line, "title=").split('"' + "")[1])) {
                            // System.out.println(removeTillWord(line, "title=").split('"' + "")[1]);
                            String temp = removeTillWord(line, "title=").split('"' + "")[1];
                            nazwy.add(temp);
                            links.add(removeTillWord(line, "href=").split('"' + "")[1]);
                            wszystkielinki.add("https://en.wikipedia.org" + removeTillWord(line, "href=").split('"' + "")[1]);
                            if (!removeTillWord(line, "href=").split('"' + "")[1].contains(":") && !removeTillWord(line, "href=").split('"' + "")[1].replace("/wiki/", "").contains("/") && !removeTillWord(line, "href=").split('"' + "")[1].replace("/wiki/", "").contains("#")) {
                                powiazania.add("https://en.wikipedia.org" + removeTillWord(line, "href=").split('"' + "")[1]);

                                System.out.println("https://en.wikipedia.org" + removeTillWord(line, "href=").split('"' + "")[1]);
                            }
// System.out.println(removeTillWord(line, "href=").split('"' + "")[1]);
                        }
                    }
                }
                ///System.out.println(line.trim());

            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ioe) {
                    //exception
                }
            }
            int l = 0;
            int numbertemp = links.size() / 4 + l;
            while (links.get(links.size() / 4 + l).toString().contains(".") || links.get(links.size() / 4 + l).toString().contains("ujednoznacznienie") || links.get(links.size() / 4 + l).toString().contains(":") || splinks.contains("--- " + nazwy.get(nazwy.size() / 4 + l) + " (" + links.get(links.size() / 4 + l) + ")")) {
                l++;
                if (links.size() / 4 + l >= links.size()) {
                    l = -1;
                }
            }
            String Wybranylink = (String) links.get(links.size() / 4 + l);
            System.out.println("Wybrany link: https://pl.wikipedia.org" + Wybranylink);
            splinks.add("--- " + nazwy.get(nazwy.size() / 4 + l) + " (" + links.get(links.size() / 4 + l) + ")");
            try {
                url = new URL("https://en.wikipedia.org" + Wybranylink);
            } catch (MalformedURLException ex) {
                Logger.getLogger(WikiHarvest.class.getName()).log(Level.SEVERE, null, ex);
            }
            links.clear();
            nazwy.clear();
        }
        for (int i = 0; i < splinks.size(); i++) {
            String get = (String) splinks.get(i);
            System.out.println(get);

        }
        for (int i = 0; i < powiazania.size(); i++) {
            String get = (String) powiazania.get(i);
            System.out.println("Saving to:Gregs_memory.txt: " + (get));
            try {
                Files.write(Paths.get("C:\\Users\\Grzechu\\Data\\Similarities_Link.txt"), (get + '\n').getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
//        for (int i = 0; i < wszystkielinki.size(); i++) {
//            String get = (String) wszystkielinki.get(i);
//
//            if (get.contains("http")) {
//                try {
//                    String filename = "D:/Data/Links.txt";
//                    FileWriter fw = new FileWriter(filename, true); //the true will append the new data
//                    fw.write(get + "\n");//appends the string to the file
//                    fw.close();
//                } catch (IOException ioe) {
//                    System.err.println("IOException: " + ioe.getMessage());
//                }
//            }
//        }
    }

    private static String getUrlSource(String url) throws IOException {
        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {

            a.append(inputLine + "\n");
        }
        in.close();

        return a.toString();
    }

    public static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word));
    }

//removeTillWord("I need this words removed taken please", "taken");
}
