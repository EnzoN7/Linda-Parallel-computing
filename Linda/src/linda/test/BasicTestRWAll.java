package linda.test;

import linda.*;

import java.util.Collection;

public class BasicTestRWAll {

    public static void main(String[] a) {
        final Linda linda = new linda.shm.CentralizedLinda();
        //              final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        new Thread() {
            public void run() {
                Tuple t3 = new Tuple(4, "foo");
                for (int i = 0; i < 3; i++) {
                    linda.write(t3);
                }

                Tuple motif = new Tuple(Integer.class, String.class);
                try {
                    Collection<Tuple> res = linda.readAll(motif);
                    System.out.println("Resultat:");
                    res.forEach(System.out::print);
                    linda.debug("()");
                } catch (NullPointerException e) {
                    System.out.println("Resultat introuvable");
                }
            }
        }.start();
    }

}
