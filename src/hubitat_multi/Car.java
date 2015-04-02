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
public class Car extends BaseAI {

    /*Thread t;*/
    private int p_w; // parent width
    boolean freezeUP; // переменная необходима для переключения в состояния замри/отомри
    
    
    public Car(Habitat obj){
        super(obj);
        this.p_w = parent.getWidth();
        pic = null;
        speed = 3;
    }
    
    
    @Override
    public void run(){ // переопределённый метод для 
                       // выполнения кода в отдельном потоке
        //super.parent.lst.
        System.out.println("Car created");
        while(going){
            //System.out.println("Car going is on");
            try {
            
                if(parent.isPausedCar()){
                    freezeUP = true;
                    freeze();
                }
                
                
            if(x < p_w)x += speed;//super.getSpeed();
            if(x >= p_w) x = 0;
            
                TimeUnit.MILLISECONDS.sleep(parent.CLOCK_RATE);
            } catch (InterruptedException ex) {
                Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
            }
            Thread.yield();
        }
        System.out.println("Car OFF and OUT");
        
    }
    
    private synchronized void freeze() throws InterruptedException{
       while(freezeUP)wait();
    }
    
    public synchronized void unfreeze(){
        freezeUP = false;
        notify();
    }
    
    @Override
    public String Beep() {
      return "Im a Car";
    }

    @Override
    public boolean setPic(BufferedImage pic) {
        if(pic != null){
            this.pic = pic;
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * хранит ссылку на картинку 
     */
    private BufferedImage pic;

    @Override
    public BufferedImage getPic() {
        return this.pic;
    }
}
