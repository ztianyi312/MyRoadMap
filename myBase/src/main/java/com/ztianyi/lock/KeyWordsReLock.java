package com.ztianyi.lock;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;
/**
 * <p>一个值锁,相对其他的对象锁而言，即满足equas的对象共一个锁
 * <p>可重入锁
 * <p>
 *
 * @author tianji_zhou
 * @see java.util.concurrent.locks.LockSupport LockSupport
 */

public class KeyWordsReLock<K,O> {
 private ConcurrentMap<K,UnitData<O>> map;
 public KeyWordsReLock(int level) {
  map = new ConcurrentHashMap<K,UnitData<O>>(level);
 }
 public KeyWordsReLock() {
  map = new ConcurrentHashMap<K,UnitData<O>>();
 }
 /**
  *
  * @param key
  * @param owner 满足quals时可重入
  * @throws InterruptedException
  */
 public void getLock(K key,O owner) throws InterruptedException {
  UnitData<O> unit = new UnitData<O>(owner);
  
  UnitData<O> currentUnit;
  while((currentUnit=map.putIfAbsent(key, unit))!=null
   && !owner.equals(currentUnit.getOwner()))
  {
   synchronized(currentUnit){
    if(map.get(key) == null)continue;
   //currentUnit.getWaiters().add(Thread.currentThread());
   currentUnit.wait();
   }
   //LockSupport.park();
  }
 }
 public boolean tryLock(K key,O owner) throws InterruptedException {
  UnitData<O> unit = new UnitData<O>(owner);
  
  UnitData<O> currentUnit;
  if((currentUnit=map.putIfAbsent(key, unit))!=null
    && !owner.equals(currentUnit.getOwner()))
  {
   return false;
  }
  
  return true;
 }
 /**
  * 目前不能全部唤醒
  * @param key
  */
 public void releaseLock(K key) {
  UnitData<O> currentUnit = map.get(key);
  if(currentUnit == null)return;
  synchronized(currentUnit){
   
   map.remove(key);
   currentUnit.notifyAll();
  }
  
  
  //for(Thread thread:currentUnit.getWaiters())
  //LockSupport.unpark(thread);
  
 }
}
class UnitData<O>
{
 private O owner;
 private List<Thread> waiters = new CopyOnWriteArrayList<Thread>();
 
 protected UnitData(O owner)
 {
  this.owner = owner;
 }
 
 public O getOwner()
 {
  return this.owner;
 }
 
 public List<Thread> getWaiters()
 {
  return this.waiters;
 }
}
