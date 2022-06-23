public class Worker2 extends Thread {

    private int id;
    private Data2 data;

    public Worker2(int id, Data2 d) {
        this.id = id;
        data = d;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 5; i++) {
            if (id == 1) data.Tic();
            else if (id == 2) data.Tak();
            else data.Toy();
        }
    }

}
