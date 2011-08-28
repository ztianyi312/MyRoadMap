package com.ztianyi.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>多模式匹配的算法Wu Manber的java实现
 * <p>屏蔽关键字
 * @author tianji_zhou
 * @version 1.0 2011-08-26
 */
public class WuManberSearch {

 private Map<String,List<String>> hashmap = new HashMap<String,List<String>>();
 private Map<String,Integer> shiftmap = new HashMap<String,Integer>();
 
 private int minLength = Integer.MAX_VALUE;
 
 private int maxLength = 0;
 
 private int blockLength = 2;
 
 private int maxshift = 1;
 
 private final String lineSeparator;
 
 public WuManberSearch(String[] keys)
 {
  this(keys,2);
 }
 
 /**
  * 
  * @param keys 要屏蔽的关键字数组
  * @param blockLength 块大小
  */
 public WuManberSearch(String[] keys, int blockLength)
 {
  if(this.blockLength<blockLength)this.blockLength = blockLength;
  
  for(int i=0; i<keys.length; i++)
  {
   if(keys[i].length()<this.blockLength)
   {
    this.blockLength = keys[i].length();
   }
   
   if(keys[i].length()<this.minLength)
   {
    this.minLength = keys[i].length();
   }
   
   if(keys[i].length()>this.maxLength)
   {
    this.maxLength = keys[i].length();
   }
  }
  
  this.maxshift = this.minLength+1-this.blockLength;
  for(int i=0; i<keys.length; i++)
  {
   this.add2hashmap(keys[i]);
   this.add2shiftmap(keys[i]);
  }
  
  lineSeparator = System.getProperty("line.separator");
 }
 
 /**
  * 处理该字符串
  * @param content
  * @return 返回屏蔽关键字后的字符串
  */
 public String replaceKey(String content)
 {
  StringBuilder sb = new StringBuilder(content.length()+1);
  int index = 0;
  for(int i=this.blockLength-1; i<content.length(); i++)
  {
   String block = content.substring(i+1-this.blockLength,i+1);
   int shift = this.getShift(block);
   
   if(shift == 0)
   {
    List<String> list = this.hashmap.get(block);
    
    for(String key:list)
    {
     if(key.length()<=i+1 && key.equals(content.substring(i-key.length()+1, i+1)))
     {
      
      if(index > i-key.length()+1)
      {
       sb.delete(i-key.length()+1, index);
      }
      else sb.append(content.substring(index, i-key.length()+1));
      for(int n =0; n<key.length(); n++)
      {
       sb.append('*');
      }
      index = i+1;
     }
    }
    continue;
   }

   i+=shift-1;

  }
  if(index < content.length())sb.append(content.substring(index, content.length()));
  
  return sb.toString();
 }
 
 
 /**
  * 每次读取长度 >= (this.maxLength-1)*2的长度就行了
  * @param r
  * @param w
  * @throws IOException
  */
 public void replaceKey(BufferedReader r,Writer w) throws IOException
 {
  int readLength = 1<<12;
  
  int off = 0;
  int size = 0;
  int offsize = this.maxLength-1;
  char[] buffer = new char[readLength+offsize];
  int length = buffer.length;
  int minReadLength = offsize<<1;
  
  
  String preTail = null;
  while((size = r.read(buffer, off, length)) != -1)
  {
   int count = off+size;
   while(count<minReadLength)//BufferedReader尽量读取最多的字符，但是在发生阻塞的情况下(ready=false)不保证读满,需要保证最小长度
   {
    size = r.read(buffer, count, buffer.length-count);
    if(size == -1)break;
    
    count+=size;
   }
   
   String result = this.replaceKey(new String(buffer,0,count));
   
   String head = result.substring(0, off);
   if(preTail != null&&!preTail.equalsIgnoreCase(head))
   {
    StringBuilder sb = merge(preTail,head);
    w.write(sb.toString());
    w.write(result, off, size==-1?count:count-offsize-off);
   }else{
    w.write(result,0, size==-1?count:count-offsize);
   }
   
   if(size == -1)
   {
    preTail = null;
    break;
   }else{
    preTail = result.substring(count-offsize, count);
    System.arraycopy(buffer, count-offsize, buffer, 0, offsize);
   }
   
   off = offsize;
   length = readLength;
  }
  if(preTail!=null)w.write(preTail);
  w.flush();
 }
 
 /**
  * 合并替换后的差异数据
  * @param s1
  * @param s2
  * @return
  */
 public static StringBuilder merge(String s1,String s2)
 {
  if(s1.length() != s2.length())return null;
  
  StringBuilder sb = new StringBuilder(s1.length());
  for(int i=0; i<s1.length(); i++)
  {
   if(s1.charAt(i) == s2.charAt(i))
   {
    sb.append(s1.charAt(i));
   }else sb.append('*');
  }
  
  return sb;
 }
 /**
  * 每次处理一行数据
  * @param r
  * @param w
  * @throws IOException
  */
 public void replaceKeyEveryLine(BufferedReader r,Writer w) throws IOException
 {
  String content = null;
  String n = null;
  while((content=r.readLine())!=null)
  {
   if(n != null)w.write(n);
   w.write(this.replaceKey(content));
   n = lineSeparator;
  }
  w.flush();
 }
 
 /**
  * 构建hash表
  * @param key
  */
 private void add2hashmap(String key)
 {
  String suffixBlock = key.substring(key.length()-this.blockLength, key.length());
  
  List list = this.hashmap.get(suffixBlock);
  if(list == null)
  {
   list = new ArrayList();
   this.hashmap.put(suffixBlock, list);
  }
  
  list.add(key);
 }
 
 /**
  * 返回位移值
  * @param block
  * @return
  */
 private int getShift(String block)
 {
  Integer minShift = this.shiftmap.get(block);
  
  return minShift==null?this.maxshift:minShift;
  
  
 }
 
 /**
  * 跳过符号反而速度更慢
  * @param ch
  * @return
  */
  private boolean isValidChar(char ch) {  
    if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')  
      || (ch >= 'a' && ch <= 'z'))  
     return true;  
    if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))  
     return true;//简体中文汉字编码  
    return false;  
 }
 
  /**
   * 构建shift表
   * @param key
   */
 private void add2shiftmap(String key)
 {
  int length = this.maxshift;
  for(int i=key.length()-length; i<key.length(); i++)
  {
   int shift = key.length()-i-1;
   String block = key.substring(i-this.blockLength+1, i+1);
   Integer minShift = this.shiftmap.get(block);
   
   if(minShift==null || minShift>shift)
   {
    this.shiftmap.put(block,shift);
   }
  }
 }
}
