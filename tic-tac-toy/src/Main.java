public class Main {
    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();

        Worker w3 = new Worker(3, d);
        Worker w2 = new Worker(2, d);
        Worker w1 = new Worker(1, d);

        Data2 d2 = new Data2();
        w3.join();

        System.out.println("Second approach when locking methods of data object");
        Worker2 w32 = new Worker2(3, d2);
        Worker2 w22 = new Worker2(2, d2);
        Worker2 w12 = new Worker2(1, d2);

        w32.join();
        System.out.println("end of main...");
    }
}
