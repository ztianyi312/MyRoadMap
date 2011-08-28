package com.ztianyi.algorithm;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ztianyi.algorithm.WuManberSearch;

public class WuManberSearchTest {

 private int circle =100;
 
 private boolean isPrint;
 
 private String content;
 private String[] keywords;
 private StringBuilder keySb;
 @Before
 public void setUp() throws Exception {
  circle =100;
  isPrint = true;

  InputStream fin = new FileInputStream("WuManberSearchTestFile\\article.txt");
  InputStream keyin = new FileInputStream("WuManberSearchTestFile\\illegalkeyword.txt");
  
  BufferedReader fReader = new BufferedReader(new InputStreamReader(fin,"UTF-8"));
  
  StringBuilder sb = new StringBuilder();
  String str = null;
  while((str = fReader.readLine()) != null)
  {
   sb.append(str);
  }
  
  List<String> keyList = new ArrayList();
  StreamTokenizer st = new StreamTokenizer(new InputStreamReader(keyin,"UTF-8"));
  keySb = new StringBuilder();
  
  String prefix = "";
  while(st.nextToken() != StreamTokenizer.TT_EOF)
  {
   keyList.add(st.sval);
   keySb.append(prefix);
   keySb.append(st.sval);
   prefix="|";
  }
  
  content = sb.toString();
  keywords = keyList.toArray(new String[]{});
  
 }

 @After
 public void tearDown() throws Exception {
 }

 @Test
 public void testWuManber() throws IOException {
 
  WuManberSearch search = new WuManberSearch(keywords,3);
  long start = System.currentTimeMillis();
  int count =0;
  String result = null;
  while(count++<circle)
  {
   result = search.replaceKey(content);
  }
     long end = System.currentTimeMillis();
     
     System.out.println("wu manber cost:"+(end-start)+"ms");
  
  if(this.isPrint)System.out.println(result);
  
 }

 @Test
 public void testRegex() throws IOException {
 
  
  Pattern pattern = Pattern.compile(keySb.toString());
  String result = null;
  long start = System.currentTimeMillis();
  int count =0;
  while(count++<circle)
  {
  Matcher m = pattern.matcher(content);
  StringBuffer regexResult = new StringBuffer();
  while (m.find()) {
   String key = m.group();
   StringBuilder tsb = new StringBuilder(key.length()+1);
   for(int n =0; n<key.length(); n++)
   {
    tsb.append('*');
   }
   m.appendReplacement(regexResult, tsb.toString());
  }
  m.appendTail(regexResult);
  
  result = regexResult.toString();
  }
  long end = System.currentTimeMillis();
  
  
  System.out.println("regex cost:"+(end-start)+"ms");
  if(this.isPrint)System.out.println(result);
 }
 
 @Test
 public void testReplaceAll() throws IOException {
 
  
  long start = System.currentTimeMillis();
  int count =0;
  String result = content;
  while(count++<circle)
  {
   for(int i=0; i<keywords.length; i++)
   {
    StringBuilder tsb = new StringBuilder(keywords[i].length()+1);
    for(int n =0; n<keywords[i].length(); n++)
    {
     tsb.append('*');
    }
    result = result.replaceAll(keywords[i], tsb.toString());
   }
  }
  long end = System.currentTimeMillis();
  
  
  System.out.println("replace cost:"+(end-start)+"ms");
  if(this.isPrint)System.out.println(result);
 }
}
