/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hubitat_multi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import java.net.URL;
import java.awt.image.BufferedImage;
import static java.awt.image.ImageObserver.ALLBITS;
//import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList; // !!
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.event.MouseInputAdapter;
//==============================================================================
/**
 *
 * @author iUser
 */
public class Habitat extends /*JApplet*/JPanel {
    private Timer m_timer = new Timer(); //private javax.swing.Timer swTimer ;//= new javax.swing.Timer();
    //private Timer gUpd_timer = new Timer(); // �������� ���������� "������"
    private Updater m_updater; 
    private gUpdater gUpd_updater; 
    //int counter=0;
    
    ExecutorService execCars; 
    ExecutorService execMoto;
    
    ThreadGroup groupMoto;
    ThreadGroup groupCar;
    
    private boolean pausedMoto = false;
    private boolean pausedCar = false;

    public void setPausedMoto(boolean pausedMoto) {
        this.pausedMoto = pausedMoto;
    }

    public void setPausedCar(boolean pausedCar) {
        this.pausedCar = pausedCar;
    }

    public synchronized boolean isPausedMoto() {
        return pausedMoto;
    }

    public synchronized boolean isPausedCar() {
        return pausedCar;
    }
    
    
    
    private boolean m_runViaFrame = false; 
    private double m_time = 0;
    private double p1 = 0.20; // ����������� ��������� ���������
    private double p2 = 0.30; // ����������� ��������� ������
    private int period = 100; // ������� ���������� �������
    private boolean emul_progress = false; // ������������� �������� T
    private boolean showtime = false; // ������������� �������� B
    
    private long startTime = 0, updaterPauseShift = 0, updaterPauseBeg =0;//, currentTime = 0;
    
    int vel_count = 0;
    int vel_shown = 0;
    
    int car_count = 0;
    int moto_count = 0;
    
    final int CLOCK_RATE = 42; // 1/������� ����������� ������ (�������� � ������������)
    
    //double totalTime = 0.0;
    
    // ������������� ��� ��������� ���� � ���������
//    private final String moto_path = "images/motopic.png";  
//    private final String car_path = "images/carpic.png";
//    
//    private final URL motoURL = Habitat.class.getResource(moto_path);
//    private final URL carURL = Habitat.class.getResource(car_path);
//    
//    private final ImageIcon motoico = new ImageIcon(motoURL);
//    private final ImageIcon carico = new ImageIcon(carURL);
    
    //BufferedImage mot = null;
    BufferedImage motopic = null;
    BufferedImage carpic = null; 
    Image offScreenImage = null;
    
    /*volatile ArrayList*/CopyOnWriteArrayList<BaseAI> lst; //������ �� ������ ��������
    //private boolean firstRUN = true; // ����� ������� ���������� (���������� ��� ���������� ��������)
    private boolean picLoaded=false; // �������� �������� �����������
    private String m_FileName1 = "motopic.png";
    private String m_FileName2 = "carpic.png";
    //private String PARAM_string_1 = "fileName";
    
    
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>   
//==============================================================================
    void setMotoPriority(int prior) {
        for(Iterator<BaseAI> it = lst.iterator();it.hasNext();){
            BaseAI next = it.next();
            if(next instanceof Moto){
                ((Moto)next).t.setPriority(prior);
            }
        }
    }
//==============================================================================
    void setCarPriority(int prior) {
        
    } 
//==============================================================================
    private class Updater extends TimerTask {
        private Habitat m_aplet = null;
        private boolean m_firstRun = true; // ������ �� ������ ������ run()?
        private long m_startTime = 0; // ����� ������ 
        private long m_lastTime = 0;  // ����� ���������� ����������
        //private boolean force_seted_Time = false;
        private long pauseShift = 0;
        private long pauseBeg = 0;
        private boolean paused = false;
        
        public Updater(Habitat applet){
            m_aplet = applet;
        }
        
        public void setPauseShift(long pauseShift){
            this.pauseShift = pauseShift;
        }
        
        public long getPauseShift(){
            return this.pauseShift;
        }
        
        public void setPauseBeg(long pauseBeg){
            this.pauseBeg = pauseBeg;
        }
        
        public long getPauseBeg(){
            return this.pauseBeg;
        }
        
        
        public void go(){
            m_firstRun = true;
            m_lastTime = 0;
            m_startTime = 0;
            pauseShift = 0;
            pauseBeg = 0;
        }
        
        public void finish(){
            m_firstRun = true;
            m_lastTime = 0;
            m_startTime = 0;
            pauseShift = 0;
            pauseBeg = 0;
        }
        
        public void drop(){
            m_firstRun = true;
            m_lastTime = 0;
            m_startTime = 0;
            pauseShift = 0;
            pauseBeg = 0;
        }
        
        public void set_m_startTime(long atTime){
            this.m_startTime = atTime;
            m_lastTime = m_startTime;
            m_firstRun = false; // ������������� ������ �������
            //force_seted_Time = true;
        }
        
        public void pauseBeg(){
            pauseBeg = System.currentTimeMillis();
            paused = true;
        }
        
        public void pauseEnd(){
            pauseShift += System.currentTimeMillis() - pauseBeg;
            paused = false;
            
        }
        
        public long get_m_startTime(){return m_startTime;}
        //public long get_currentTime(){return (m_lastTime - m_startTime);}
        @Override
        public void run(){
            
            if(!paused){// ���� �� �� �����
                if(m_firstRun){
                    m_startTime = System.currentTimeMillis();
                    m_lastTime = m_startTime;
                    m_firstRun = false;
                }
                 
                 long currentTime  = System.currentTimeMillis();
                 
                 //����� ��������� �� ������, � ��������.
                 double elapsed = ((currentTime - m_startTime)- pauseShift) / 1000.0; // pauseShift dont work properly
                 
//                 System.out.println( "pauseShift = " + pauseShift);
//                 System.out.println( "��������� = " + ((currentTime - m_startTime) - pauseShift) );
//                 System.out.println( "��� ������ = " + ((currentTime - m_startTime)) );
                 
                 //����� ��������� �� ���������� ����������, � ��������.
                 //double frameTime = (m_lastTime - m_startTime) / 1000.0;
             
                 //�������� ����������
                 m_aplet.Update(elapsed/*, frameTime*/);
                 
                 m_lastTime = currentTime; //�����
            
            }// endif    
                 
        }//end run()
    }
//==============================================================================
    public int getPeriod(){return this.period;}
//==============================================================================   
    public Habitat(){
                
        System.out.println("����������� Habitat �������");
        initComponents();
        
        execCars = Executors.newCachedThreadPool();
        execMoto = Executors.newCachedThreadPool();
        //execMoto = Executors.newScheduledThreadPool(5000);
        groupCar = new ThreadGroup("Cars");
        groupMoto = new ThreadGroup("Motos");
        
//        try{
//            mot = ImageIO.read(new File("./motopic.png"));
//        }catch(IOException e){
//            e.printStackTrace();
//        }
          try{
              motopic = ImageIO.read(getClass().getResource("motopic.png"));
          }catch(IOException e){
              //e.printStackTrace();
          }
    
          try{
              carpic = ImageIO.read(getClass().getResource("carpic.png"));
          }catch(IOException e){
              
          }
          
        
        //motopic = getImage(getCodeBase(), "images/motopic.png");
        //if(carpic == null)carpic = getImage(getDocumentBase(), "carpic.png");
        
          lst = new /*ArrayList<>*/CopyOnWriteArrayList<>(); // �������� ����� �������
        
        //---------------------------------
        // ���������� ������� �� ���� 
        //(�������� ����� � ������ �����. 
        // ����� ����� ������� ���� ����� �������� �����, 
        // ����� �� ����� �������������� ������� �� ����������.)
        
      MouseInputAdapter pm;  
      pm = new MouseInputAdapter() { 
       @Override
       public void mousePressed(MouseEvent e) { 
//             x=e.getX(); y=e.getY(); 
//             System.out.println(x); 
//             repaint(); 
       }}; 
       this.addMouseListener(pm);
        
        //---------------------------------
        KeyAdapter pk;
        
        pk = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e){
            System.out.println(e);
            int keycode = e.getKeyCode();
            
            switch(keycode){
                case KeyEvent.VK_B: // ��������� ���������
                    /*System.out.println("B is pressed");*/
                    /*emul_progress = true;*/
                    /*repaint();*/
                    start_sim();
                    break;
                case KeyEvent.VK_E: //���������� ���������
                    /*System.out.println("E is pressed");
                    emul_progress = false;
                    vel_shown = 0;
                    vel_count = 0;
                    lst.clear();
                    repaint();*/
                    stop_sim();
                    break;
                case KeyEvent.VK_T:
                    /*System.out.println("T is pressed");
                    showtime = !showtime;
                    repaint();*/
                    trig_timer();
                    break;
            }
            
        } 
    };
        this.addKeyListener(pk);
        Init();
        
    }
//==============================================================================
    // ������ ����� ����������� ��������� ���������
    public void setP_moto(String s){
        String buf = new String();
        switch(s){
            case "0%":
                System.out.println("������ ������ 0%: " + s);
                buf = s.substring(0, 1);
                break;
            case "100%":
                System.out.println("������ ������ 100%: " + s);
                buf = s.substring(0, 3);
                break;
            default:
                System.out.println("������ ������ ������ : " + s);
                buf = s.substring(0, 2);
                break;
        }
        
        this.p1 = (Double.parseDouble(buf))/100;
        System.out.println(p1);
    }
//==============================================================================
    // ������ ����� ����������� ��������� ����������
    public void setP_car(String s){
        String buf = new String();
        switch(s){
            case "0%":
                System.out.println("���� ������ 0%: " + s);
                buf = s.substring(0, 1);
                break;
            case "100%":
                System.out.println("���� ������ 100%: " + s);
                buf = s.substring(0, 3);
                break;
            default:
                System.out.println("���� ������ ������ : " + s);
                buf = s.substring(0, 2);
                break;
        }
        
        this.p2 = (Double.parseDouble(buf))/100;
        System.out.println(p2);
    }
//==============================================================================
    public String getStatistic(){
        String s = "����� ���������:\n" +
                       "����� " + car_count +
                       "\n���������� " + moto_count +
                       "\n����� ������������ ������� " + vel_count +
                       "\n�� ����� " + m_time + " ������";
        return s;
    }
//==============================================================================
    public void start_sim(){ // ��������� ���������
        System.out.println("B is pressed");
        emul_progress = true;
        repaint();
        
        m_updater.go();
    }
//==============================================================================
    public void stop_sim(){ // ���������� ���������
        System.out.println("E is pressed");
        emul_progress = false;
        vel_shown = 0;
        vel_count = 0;
        car_count = 0;
        moto_count = 0;
        
        //ListIterator<BaseAI> l_iter = lst.listIterator(/*vel_shown*/0);
        
        int i = 1;
        for(BaseAI ob: lst){
            System.out.println("Stopped thread �" + i);
            ob.going = false;
            ++i;
        }
        
        lst.clear();
        m_updater.finish();
        //****************************//
        repaint();
    }
//==============================================================================
    public boolean pause_sim(){
        if(emul_progress){//�����
            //currentTime = m_updater.get_currentTime();
            //startTime = m_updater.get_m_startTime();
            m_updater.pauseBeg();
        }else{// �����������
            m_updater.pauseEnd();
            //m_updater.drop();
            //m_updater.set_m_startTime(startTime);
        }
        
        return (emul_progress = !emul_progress); // ������� ��������� ��������
    }
//==============================================================================
    public void hold_sim(){
        emul_progress = false;
    }
//==============================================================================
    public void unhold_sim(){
        emul_progress = true;
    }
//==============================================================================
    public void set_period(int val){ //*********************************//
        this.period = val;
          startTime = m_updater.get_m_startTime(); // ��������� ����� ������ ������� ����� ��������� 
          updaterPauseShift = m_updater.getPauseShift();// ���������
          updaterPauseBeg = m_updater.getPauseBeg();
          
          m_updater.cancel();
          m_timer.purge();// no ex
          m_timer.cancel();// no ex
          
        //swTimer.
       
        m_timer = new Timer();
        m_updater = new Updater(this);
        m_updater.set_m_startTime(startTime);
        m_updater.setPauseBeg(updaterPauseBeg);
        m_updater.setPauseShift(updaterPauseShift);
        m_timer.schedule(m_updater, 0, period); //!!!!!this ex!!!!
    }
//==============================================================================
    public boolean trig_timer(){ // ��������/������ ������
        System.out.println("T is pressed");
        showtime = !showtime;
        repaint();
        return showtime;
    }
//==============================================================================
    public void set_timer_show(boolean state){
        showtime = state;
        repaint();
    }
//==============================================================================
    public boolean timerValueState(){
        return showtime;
        
    }
//==============================================================================   
synchronized void unfreezeMoto(){
        for(Iterator<BaseAI> it = lst.iterator();it.hasNext();){
            BaseAI next = it.next();
            if(next instanceof Moto){
                ((Moto)next).unfreeze();
            }
        }
}
//==============================================================================   
synchronized void unfreezeCar(){
        for(Iterator<BaseAI> it = lst.iterator();it.hasNext();){
            BaseAI next = it.next();
            if(next instanceof Car){
                ((Car)next).unfreeze();
            }
        }
}
//==============================================================================    
    @Override
    public boolean imageUpdate(Image img, int infoflags,int x, int y,int w, int h){
        
        if(infoflags == ALLBITS){
            picLoaded = true;
            repaint();
            return false; // ������ ����� update() �� ��������
        }else{
            return true;
        }
    }
//==============================================================================    
    private void Init(){
        // ������ ����� ���������� ������ 100��
        m_updater = new Updater(this);
        
        m_timer.schedule(m_updater, 0, period/*100*/);
     
        gUpd_updater = new gUpdater(this);
        Thread tr = new Thread(gUpd_updater);
        tr.setDaemon(true);
        tr.start();
        //gUpd_timer.schedule(gUpd_updater, 10);
        
        //String param = getParameter(PARAM_string_1);
        
    }
//==============================================================================    
    public void Update(double elapsedTime/*, double frameTime*/){
       if(emul_progress){
           m_time = elapsedTime;
       }else{
//           m_timer.
           //m_time = 0; // 
           //m_updater.drop();
       }
       
       if(emul_progress){
           double p0 = Math.random();
           
           if(p0 <= p1){ // �������� ��������
               
               Moto m = new Moto(this);
               if(!m.setPic(motopic)) System.err.println("motoico missed");
               m.x = (int)((getWidth()/1.2) * Math.random());
               m.y = (int)((getHeight()/1.2) * Math.random()); 
               m.t = new Thread(groupMoto,m); // �������� ������ � ���������� ��� � ������ Motos
               //m.t.start();
               
               lst.add(m);
               execMoto.execute(m);
               ++vel_count;
               ++moto_count;
               //repaint();
           }
           
           if(p0 <= p2){ // ��������� ������
               
               Car c = new Car(this);
               if(!c.setPic(carpic)) System.err.println("carico missed");
               c.x = (int)((getWidth()/1.2) * Math.random());
               c.y = (int)((getHeight()/1.2) *Math.random());
               c.t = new Thread(groupCar,c); // �������� ������ � ���������� ��� � ������ Cars
               //c.t.start();// ������ � ����� ������
               
               lst.add(c);
               execCars.execute(c);
               ++vel_count;
               ++car_count;
               //repaint();
           }    
       }
       
       //this.repaint(); //!!! ����������� ������ ������ � ������ gUpdater
    }
    
//==============================================================================    
    @Override
    public void paint(Graphics g){
        
        
        // �������� ������������ ������
        int width = getSize().width,heigth = getSize().height;    
        offScreenImage = createImage(width, heigth);
        // ��������� ��������� ������������ ������
        Graphics offScreenGraphics = offScreenImage.getGraphics();
        
        //������� ������      
            offScreenGraphics.setColor(Color.white);
            offScreenGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            offScreenGraphics.setColor(Color.GREEN);
            offScreenGraphics.setFont(new Font("Tahoma",Font.BOLD,12));
            //firstRUN = false;

        String str = "Time = " + Double.toString(m_time); //��������� ������� �� �������
        
        if(showtime)offScreenGraphics.drawString(str, 395, getHeight()-10); // ����������� �������        
        
        //if(vel_count > vel_shown){ //���� �������������� ����� ���������
            //synchronized(lst){ // ������������� ��������� � ����� (���������� ��������� ���������)        
                //Iterator<Velocity> iterator = lst.iterator();
                ListIterator<BaseAI> l_it = lst.listIterator(/*vel_shown*/0);

                for(int i=0/*vel_shown*/;l_it.hasNext();++i,l_it.next() ,vel_shown = i){
                    //Class<? extends Velocity> s = lst.get(i).getClass();      
                    //g.drawString(s.getTypeName(), 50 + 10*i, 50);
                    //g.drawString("step {i}", 200 + 10*1, 50 + 20*i);
            
                  //  g.drawString(lst.get(i).Beep(),75,50+ i*10);
                    offScreenGraphics.drawImage(lst.get(i).getPic(),
                            /*(int)((getWidth()/1.2) * Math.random())*/ lst.get(i).x,
                            /*(int)((getHeight()/1.2) *Math.random()) */lst.get(i).y,
                            this);
                    //
                }
            //}
        //}        
        //offScreenGraphics.drawString("showtime = " + showtime, 100, 100);
        //offScreenGraphics.drawString("emul_progress = " + emul_progress, 120, 120);
        offScreenGraphics.setColor(Color.red);
        offScreenGraphics.setFont(new Font("Arial",Font.BOLD,14));
        offScreenGraphics.drawString("vel_count = " + vel_count, 10, getHeight()-10);
        offScreenGraphics.drawString("car_count = " + car_count, 135, getHeight()-10);
        offScreenGraphics.drawString("moto_count = " + moto_count, 255, getHeight()-10);
        //offScreenGraphics.drawString("vel_shown = " + vel_shown, 120, 140);
        
        
        //--------
        if(/*picLoaded*/true){
           g.drawImage(offScreenImage, 0, 0, null); 
        }else{
            //showStatus("Loading image");
        }
        
        //--------
        
    }
//==============================================================================    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        
//        JFrame frame = new JFrame();
//        
//        
//        Habitat app = new Habitat();
//        //app.init(); 
//        app.start();
//        
//        
//        frame.getContentPane().add(app);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800,600);
//        frame.setVisible(true);
//    
//    }
//    
//    
    
}// end
//==============================================================================
