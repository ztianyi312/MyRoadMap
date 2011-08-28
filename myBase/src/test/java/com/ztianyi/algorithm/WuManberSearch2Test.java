package com.ztianyi.algorithm;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ztianyi.algorithm.WuManberSearch;

public class WuManberSearch2Test {

 private int circle =100;
 private String[] keywords;
 private WuManberSearch search;
 
 @Before
 public void setUp() throws Exception {
  
  InputStream keyin = new FileInputStream("WuManberSearchTestFile\\illegalkeyword.txt");
  
  
 
  
  List<String> keyList = new ArrayList();
  StreamTokenizer st = new StreamTokenizer(new InputStreamReader(keyin));
  
  String prefix = "";
  while(st.nextToken() != StreamTokenizer.TT_EOF)
  {
   keyList.add(st.sval);
  }
  
  keywords = keyList.toArray(new String[]{});
  
  search = new WuManberSearch(keywords,1);
 }

 @After
 public void tearDown() throws Exception {
 }

 @Test
 public void testReplaceKeyBufferedReaderWriter() throws IOException {
  InputStream fin = new FileInputStream("WuManberSearchTestFile\\in.txt");
  FileOutputStream fout = new FileOutputStream("WuManberSearchTestFile\\out.txt");
  BufferedReader fReader = new BufferedReader(new InputStreamReader(fin));
  OutputStreamWriter fWriter = new OutputStreamWriter(fout);
  
  long start = System.currentTimeMillis();
  search.replaceKey(fReader, fWriter);
  long end = System.currentTimeMillis();
  System.out.println("replace cost:"+(end-start)+"ms");
  
  fReader.close();
  fWriter.close();
  
 }
 

 @Test
 public void testReplaceKeyEveryLine() throws IOException {
	 InputStream fin =  new FileInputStream("WuManberSearchTestFile\\in.txt");
  FileOutputStream fout = new FileOutputStream("WuManberSearchTestFile\\out2.txt");
  BufferedReader fReader = new BufferedReader(new InputStreamReader(fin));
  OutputStreamWriter fWriter = new OutputStreamWriter(fout);
  
  long start = System.currentTimeMillis();
  search.replaceKeyEveryLine(fReader, fWriter);
  long end = System.currentTimeMillis();
  System.out.println("replace cost:"+(end-start)+"ms");
  
  fReader.close();
  fWriter.close();
 }

 @Test
 public void testCopy() throws IOException {
  InputStream fin =  new FileInputStream("WuManberSearchTestFile\\in.txt");
  FileOutputStream fout = new FileOutputStream("WuManberSearchTestFile\\out3.txt");
  BufferedReader fReader = new BufferedReader(new InputStreamReader(fin));
  OutputStreamWriter fWriter = new OutputStreamWriter(fout);
  
  char[] buffer = new char[1<<12];
  int size = 0;
  long start = System.currentTimeMillis();
  while((size = fReader.read(buffer)) != -1)
  {
   fWriter.write(buffer, 0, size);
  }
  fWriter.flush();
  long end = System.currentTimeMillis();
  System.out.println("copy cost:"+(end-start)+"ms");
  
  fReader.close();
  fWriter.close();
 }
}
