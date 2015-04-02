/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hubitat_multi;

/**
 *
 * @author iUser
 */
public abstract class BaseAI implements IBehaviour, Runnable {
    int x;
    int y; 
    
    //boolean paused = false;
    
    Thread t;
    /*private*/ int speed;
    
    boolean going = true;
    Habitat parent;
    
    public BaseAI(Habitat parrentObj){ // конструктор
        this.speed = 1;
        this.parent = parrentObj;
    }
    
    public BaseAI(Habitat parrentObj, int speed){
        this.speed = speed;
        this.parent = parrentObj;
    }
    
    /*public void setSpeed(int speed){this.speed = speed;}
    public int getSpeed(){return this.speed;}*/
    
    @Override
    public abstract void run(); // переопределить для реализации потока
    
    
    public int mooveX(int speed){
        this.x += speed;
        return x;
    }
    
    public int mooveX(){
        this.x += this.speed;
        return x;
    }
    
    public int mooveY(int speed){
        this.y += speed;
        return y;
    }
    
    public int mooveY(){
        this.y += this.speed;
        return y;
    }
    
//    public synchronized void setPaused() 
//            throws InterruptedException{
//        paused = true;
//        /*while(paused)*/ Thread.sleep(2000);
//    }
//    
}
