
import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Colonia c = new Colonia();
        Random r = new Random();
        int numObrera = 1;
        int numSoldado = 1;
        int numCria = 1;
        int numHormigas = 0;
        
        while (numHormigas < 10) {
            try {
                HormigaObrera ho1 = new HormigaObrera(numObrera, c);
                ho1.start();
                numObrera++;
                sleep(r.nextInt(800, 3501));
                HormigaObrera ho2 = new HormigaObrera(numObrera, c);
                ho2.start();
                numObrera++;
                sleep(r.nextInt(800, 3501));
                HormigaObrera ho3 = new HormigaObrera(numObrera, c);
                ho3.start();
                numObrera++;
                sleep(r.nextInt(800, 3501));
                HormigaSoldado hs = new HormigaSoldado(numSoldado, c);
                hs.start();
                numSoldado++;
                sleep(r.nextInt(800, 3501));
                HormigaCria hc = new HormigaCria(numCria, c);
                hc.start();
                numCria++;
                sleep(r.nextInt(800, 3501));
                numHormigas += 5;
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
                
        System.out.println("Se han creado " + numHormigas + " hormigas");
    }
}
