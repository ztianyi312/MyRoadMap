package com.ztianyi.lock;

import static org.junit.Assert.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class KeyWordsLockTest {
 
 KeyWordsReLock<StringObject,Thread> lock = new KeyWordsReLock<StringObject,Thread>();
 @Before
 public void setUp() throws Exception {
 }
 @After
 public void tearDown() throws Exception {
 }
 @Test
 public void testGetLock() {
 Runnable task1 = new Runnable()
  {
  AtomicInteger i =new AtomicInteger(100);
   public void run() {
    StringObject o = new StringObject(i.incrementAndGet(),"asdadsa3423424");
    try {
     //String o = "asdad";
     lock.getLock(o,Thread.currentThread());
     
     //lock.getLock(o,Thread.currentThread());
     System.out.println(o+":lock");
     //Thread.sleep(1000);
     
    } catch (Exception e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     System.out.println("error");
    }finally{
     System.out.println(o+":unlock");
     try{
     lock.releaseLock(o);
     }catch (Exception e) {System.out.println("error");};
    }
   }
   
  };
  
  Runnable task2 = new Runnable()
  {
   AtomicInteger i =new AtomicInteger(200);
   public void run() {
    StringObject o = new StringObject(i.incrementAndGet(),"3453534");
    try {
     //String o = "asdad";
     lock.getLock(o,Thread.currentThread());
     
     //lock.getLock(o,Thread.currentThread());
     System.out.println(o+":lock");
     //Thread.sleep(1000);
     
    } catch (Exception e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     System.out.println("error");
    }finally{
     System.out.println(o+":unlock");
     try{
     lock.releaseLock(o);
     }catch (Exception e) {System.out.println("error");};
    }
   }
   
  };
  
  ExecutorService exec = Executors.newFixedThreadPool(40);
  for(int i=0;i<20;i++)
  {
   exec.submit(task1);
   exec.submit(task2);
  }
  exec.shutdown();
 }
 
 
 public static void main(String[] args)
 {
  KeyWordsLockTest test = new KeyWordsLockTest();
  test.testGetLock();
  
  int a = 1;
  
 }
}

 class StringObject{
 private String s;
 
 int i =0;
 
 public StringObject(int i,String s)
 {
  this.s = s;
  this.i = i;
 }
 
 @Override
 public String toString()
 {
  return super.toString()+"#"+i;
 }
 
 @Override
 public int hashCode()
 {
  return s.hashCode();
 }
 
 @Override
 public boolean equals(Object o)
 {
  if(this == o)return true;
  
  if(o instanceof StringObject)
  return s.equals(((StringObject)o).s);
  
  return false;
 }
 
 
}
