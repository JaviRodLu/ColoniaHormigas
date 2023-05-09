/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author javi
 */
public class HormigaSoldado extends Hormiga {

    public HormigaSoldado(int num, Colonia colonia, Paso paso) {
        super(num, colonia, paso);
        if (num < 10) {
            this.setIdentificador("HS000" + num);
        } else if (num < 100) {
            this.setIdentificador("HS00" + num);
        } else if (num < 1000) {
            this.setIdentificador("HS0" + num);
        } else {
            this.setIdentificador("HS" + num);
        }
    }
    
    public void run() {
        
    }
    
}
