public class Worker extends Thread {

    private int id;
    private Data data;

    public Worker(int id, Data d) {
        this.id = id;
        data = d;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        try {
            for (int i = 0; i < 5; i++) {
                synchronized (data) {
                    while (id != data.getState()) {
                        data.wait();
                    }
                    if (id == 1) data.Tic();
                    else if(id == 2) data.Tak();
                    else data.Toy();
					data.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
