import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import com.data.Config;
import com.data.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

public class Common {
    public Config readConfig( String json ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Config.class);
        } catch( IOException ex ) {
            throw ex;
        }
    }
    
    public ResponseData parseResult( String json ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, ResponseData.class);
        } catch( IOException ex ) {
            throw ex;
        }
    }
    
    public String readFileBody( String fileName ) throws IOException {
        ArrayList<String> retList = new ArrayList<String>();
        try( BufferedReader br = new BufferedReader(new FileReader(fileName)) ) {
            System.out.println("read file - " + fileName);
            String line = "";
            while( (line = br.readLine()) != null ) {
                retList.add(line);
            }
        } catch ( IOException ex ) {
            throw ex;
        }
        return String.join("", retList);
    }
    
    public HashMap<String, ArrayList<String>> readCsv(String fileName) throws Exception {
        HashMap<String, ArrayList<String>> retMap = new HashMap<String, ArrayList<String>>();
        try {
            CSVReader reader = new CSVReader(new FileReader(fileName));
            String[] line;
            Boolean isHeader = true;
            HashMap<Integer, String> colOrder = new HashMap<Integer, String>();
            System.out.println("read csv file - " + fileName);
            while ( (line = reader.readNext()) != null ) {
                for( int cnt = 0; cnt < line.length; cnt++ ) {
                    if( isHeader ) {
                        colOrder.put(cnt, line[cnt]);
                    } else {
                        String head = colOrder.get(cnt);
                        if( !retMap.containsKey(head) ) {
                            retMap.put(head, new ArrayList<String>());
                        }
                        ArrayList<String> tmp = retMap.get(head);
                        tmp.add(line[cnt]);
                    }
                }
                isHeader = false;
            }
            reader.close();
        } catch( FileNotFoundException fnex ) {
            System.out.println("file not found ： " + fileName);
            throw fnex;
        }catch( IOException ex ) {
            throw ex;
        }
        return retMap;
    }
    
    public String morphologic( String str, Config conf ) {
        Tokenizer tokenizer = Tokenizer.builder().build();
        List<Token> tokens = tokenizer.tokenize(str);
        String ret = "";
        for( Token tk : tokens) {
            String partOfSp = tk.getPartOfSpeech();
            partOfSp = partOfSp.split(",")[0];
            if( !conf.StopWords.contains(partOfSp) ) {
                ret += " " + tk.getSurfaceForm();
            }
        }
        return ret;
    }

    public String createAzureBody( Config conf, String csvFileName ) throws Exception {
        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<String> dataList = new ArrayList<String>();
        try {
            System.out.println("create http request body");
            Common com = new Common();
            HashMap<String, ArrayList<String>> lineMap = com.readCsv(csvFileName);
            for( String name : lineMap.keySet() ) {
                nameList.add("\"" + name + "\"");
            }
            Collections.sort(nameList);
            ArrayList<String> tmpList = lineMap.get(nameList.get(0).replaceAll("\"", ""));
            int lineSize = tmpList.size();
            for( int cnt = 0; cnt < lineSize; cnt++ ) {
                ArrayList<String> valList = new ArrayList<String>();
                for( String key : nameList ) {
                    String nm = key.replaceAll("\"", "");
                    String val = lineMap.get(nm).get(cnt);
                    if( conf.MorphoLogical.contains(nm) ) {
                        val = com.morphologic(val, conf);
                    }
                    valList.add("\"" + val + "\"");
                }
                dataList.add("[" + String.join(",", valList) + "]");
            }
        } catch( IOException ioex ) {
            throw ioex;
        } catch( Exception ex ) {
            throw ex;
        }
        ArrayList<String> retList = new ArrayList<String>();
        retList.add("{");
        retList.add("\"Inputs\": {");
        retList.add("\"input1\":{");
        retList.add("\"ColmunNames\":[" + String.join(",", nameList) + "],");
        retList.add("\"Values\":[" + String.join(",", dataList) + "]");
        retList.add("},");
        retList.add("},");
        retList.add("\"GlobalParameters\": {");
        retList.add("}");
        retList.add("}");
        return String.join("", retList);
    }
}
