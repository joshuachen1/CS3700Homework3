import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection {
    public static void main(String[] args) {
        // N elected official threads
        Phaser ph1 = new Phaser();
        // One rank thread
        Phaser ph2 = new Phaser(1);

        final int numThreads = 10;

        for (int i = 0; i < numThreads; i++) {
            new OrderedLeaderElection().electedOfficialThread(ph1);
        }

        Thread rankThread;

    }

    private void electedOfficialThread(Phaser ph1) {
        ph1.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                int rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
                Thread leader = Thread.currentThread();

                System.out.println("Name: " + name + " Rank: " + rank + " Leader: " + leader.getName());


                ph1.arriveAndDeregister();
            }
        }).start();
    }
}
