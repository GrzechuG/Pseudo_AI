
package keywordsfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author GrzechuG
 * 
 */
public class KeyWordsFinder {

    
    public static void main(String[] args) {
        Menu();
       
    }

    private static void Menu() {
        try {
            Scanner in = new Scanner(System.in);
            //List of words that is common in text but meaningless. For example "the" is a stopword.
            //You can find mine in here:
            System.out.println("Enter stopword list location:");
            String inNextLine=in.nextLine();
             ArrayList stopwords=new ArrayList();
            if(!inNextLine.equals("")){
             stopwords = ReadFile(inNextLine);
             System.out.println(stopwords);
                
            }else{
               
               stopwords = ReadFile("Stopwords_EN.txt"); 
            }
            
            System.out.println("Enter article URL or a sentence:");
            String url = in.nextLine();

            String text = "";
            if (url.contains("http")) {
                text = getPlainText(getUrlSource(url));
            } else {
                text = url;
            }

            String words[] = text.split(" ");
            ArrayList temp = new ArrayList();
            for (int i = 0; i < words.length - 1; i++) {
                String word = words[i];
                temp.add(words[i] + " " + words[i + 1]);
                temp.add(words[i]);
            }
            List<String> list = SortByOccurance((String[]) temp.toArray(new String[0]));

            for (int i = 0; i < list.size(); i++) {
                String an = list.get(i);

                if (!stopwords.contains(an.toLowerCase())
                        && an.length() > 2
                        && isAlpha(an.replace(" ", "").replace("'", ""))
                        && !stopwords.contains(an.toLowerCase().split(" ")[0])) {
                    if (an.contains(" ")&&!an.endsWith(" ")) {
                        if (!stopwords.contains(an.toLowerCase().split(" ")[1])) {
                            System.out.println(an);
                        }
                    } else {
                         System.out.println(an);
                    }
                    if (i > 300) {
                        break;
                    }
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(KeyWordsFinder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean isAlpha(String name) {
        return name.matches("[0-9a-zA-Z]+");
    }

    private static List<String> SortByOccurance(String stringArray[]) {
        //   String[] stringArray = {"x", "y", "z", "x", "x", "y", "a"};

        final Map<String, Integer> counter = new HashMap<String, Integer>();
        for (String str : stringArray) {
            counter.put(str, 1 + (counter.containsKey(str) ? counter.get(str) : 0));
        }

        List<String> list = new ArrayList<String>(counter.keySet());
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String x, String y) {
                return counter.get(y) - counter.get(x);
            }
        });
        return list;
    }

    private static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances                                                       
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0                                 
        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

        // dynamically computing the array of distances                                  
        // transformation cost for each letter in s1                                    
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1                             
            newcost[0] = j;

            // transformation cost for each letter in s0                                
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings                             
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation                               
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost                                                    
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays                                                 
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings        
        return cost[len0 - 1];
    }

    private static String getPlainText(String htmlText) {
        String strippedText = htmlText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        return strippedText;
    }

    private static String getUrlSource(String url) throws IOException {
        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            a.append(inputLine);
        }
        in.close();

        return a.toString();
    }

    public static String OpenFile_FileInputStream2(String path) {
        StringBuilder sb = new StringBuilder();
        File file = new File(path);

        try (FileInputStream fis = new FileInputStream(file)) {

            int content;
            while ((content = fis.read()) != -1) {
                // convert to char and display it System.out.print((char) content);
                sb.append((char) content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static ArrayList ReadFile(String FilePath) {
        ArrayList stoppers = new ArrayList();
        try {
            File fileDir = new File(FilePath);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String str;

            while ((str = in.readLine()) != null) {
                //  System.out.println(str);
                stoppers.add(str);
            }

            in.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return stoppers;
    }
}
