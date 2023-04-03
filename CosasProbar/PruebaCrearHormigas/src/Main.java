public class Main {
    public static void main(String[] args) {
        Colonia c = new Colonia();
        int numObrera = 1;
        int numSoldado = 1;
        int numCria = 1;
        int numHormigas = 0;
                
        for (int i = 0; i < 10000; i++) {
            if (i % 3 == 0) {
                if (numSoldado <= 2000) {
                    HormigaSoldado hs = new HormigaSoldado(numSoldado, c);
                    hs.start();
                    numSoldado++;
                    numHormigas++;
                }
                if (numCria <= 2000) {
                    HormigaCria hc = new HormigaCria(numCria, c);
                    hc.start();
                    numCria++;
                    numHormigas++;
                }
            } else {
                if (numObrera <= 6000) {
                    HormigaObrera ho = new HormigaObrera(numObrera, c);
                    ho.start();
                    numObrera++;
                    numHormigas++;
                }
            }
        }
                
        //System.out.println("Se han creado " + numHormigas + " hormigas");
        System.out.println("Se han creado " + (numSoldado+numCria+numObrera) + " hormigas");
    }
}
