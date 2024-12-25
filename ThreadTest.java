class Shared {
    private int value = 0;

    public synchronized void add() {
        value++;
    }

    public synchronized void print() {
        System.out.println(value);
    }

    public synchronized void reset() {
        value = 0;
    }
}

class Loop implements Runnable {

    private Shared shared;

    public Loop(Shared shared) {
        this.shared = shared;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            shared.add();
        }
    }

}

public class ThreadTest {
    public static void main(String[] args) {
        Shared shared = new Shared();
        for (int j = 0; j < 100; j++) {
            Thread one = new Thread(new Loop(shared));
            Thread two = new Thread(new Loop(shared));
            Thread three = new Thread(new Loop(shared));
            long start = System.currentTimeMillis();
            one.start();
            two.start();
            three.start();
            try {
                one.join();
                two.join();
                three.join();
            } catch (Exception e) {

            }
            // for (int i = 0; i < 1000000; i++) {
            // shared.add();
            // }
            // for (int b = 0; b < 1000000; b++) {
            // shared.add();
            // }
            // for (int k = 0; k < 1000000; k++) {
            // shared.add();
            // }
            System.out.println(System.currentTimeMillis() - start + ", ");
            shared.print();
            shared.reset();
        }
    }
}
