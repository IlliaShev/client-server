public class Data2 {

    private int state = 1;

    public int getState() {
        return state;
    }

    public synchronized void Tic() {
        try {
            while (state != 1) {
                wait();
            }
            System.out.print("Tic-");
            state = 2;
            notifyAll();
        } catch (InterruptedException ex) {

        }
    }

    public synchronized void Tak() {
        try {
            while (state != 2) {
                wait();
            }
            System.out.print("Tak-");
            state = 3;
            notifyAll();
        } catch (InterruptedException ex) {

        }
    }

    public synchronized void Toy() {
        try {
            while (state != 3) {
                wait();
            }
            System.out.println("Toy");
            state = 1;
            notifyAll();
        } catch (InterruptedException ex) {

        }
    }

}
