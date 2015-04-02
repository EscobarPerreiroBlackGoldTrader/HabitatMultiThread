/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hubitat_multi;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iUser
 */
public class Moto extends BaseAI {

    /*Thread t;*/
    
    boolean freezeUP; // переменная необходима для переключения в состояния замри/отомри
    
    public Moto(Habitat parrentObj) {
        super(parrentObj);
        this.p_h = parent.getHeight();
        pic = null;
    }

    private int p_h;
    
    @Override
    public void run(){// переопределённый метод для выполнения в своём потоке
        
        /*int p_h = parent.getHeight();*/
        System.out.println("Moto created");
        
        while(going){
            //System.out.println("Moto going is on");
            try {
                
                //if(Thread.interrupted())throw new InterruptedException();
                if(parent.isPausedMoto()){
                    freezeUP = true;
                    freeze();
                }
                    
                    //else unfreeze();
                if(y < p_h)y += speed; //super.getSpeed();
                if(y >= p_h) y = 0;
                
            
                TimeUnit.MILLISECONDS.sleep(parent.CLOCK_RATE);
            } catch (InterruptedException ex) {
                //moove = false; // поддержка interrupt()
                Logger.getLogger(Moto.class.getName()).log(Level.SEVERE, null, ex);
            }
             Thread.yield();
        }
        System.out.println("Moto OFF and OUT");
    }
    
    private synchronized void freeze() throws InterruptedException{
       while(freezeUP == true){ 
            wait();
       }
    }
    
    public synchronized void unfreeze(){
        freezeUP = false;
        notify();
    }
    
//    private synchronized void unfreeze(){
//        notify();
//    }
    
    
    @Override
    public String Beep() {
       return "Im a Moto"; 
    }
    
    @Override
    public boolean setPic(BufferedImage pic) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if(pic != null){
            this.pic = pic;
            return true;
        }else{
            return false;
        }
    }
    
    private BufferedImage pic;

    @Override
    public BufferedImage getPic() {
        return this.pic;
    }
}
