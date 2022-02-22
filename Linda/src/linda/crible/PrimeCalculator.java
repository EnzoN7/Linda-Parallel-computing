package linda.crible;

public class PrimeCalculator {
    static final Integer bound = 1000; // recherche des nombres premiers jusqu'� "bound"

    public static void main(String[] args) {
//        Crible crible = new SeqCrible(bound); // Version s�quentielle
//        CribleNoLinda crible = new CribleNoLinda(bound); // Version sans Linda
        Crible crible = new CribleV1(bound); // Version Avec Linda
        long start = System.nanoTime();
        System.out.println("Les nombre premiers jusqu'� " + bound + " sont : " + crible.getPrimes());
        long finish = System.nanoTime();
        System.out.println("Temps: " + (finish - start));
    }
}
