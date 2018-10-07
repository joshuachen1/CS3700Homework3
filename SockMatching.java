import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class SockMatching {
    public static void main(String[] args) throws InterruptedException{
        Phaser ph = new Phaser();
        final int numThreads = 4;

        int[] socks = new int[5];

        // Register main thread
        ph.register();

        // Phase 0
        int currentPhase = ph.getPhase();
        System.out.println("Current Phase: " + currentPhase);

        // Generate random number of socks
        for (int i = 0; i < numThreads; i++) {
            new SockMatching().setRandomSocks(ph, socks, i);
        }

        // Give threads time to catch up
        Thread.sleep(500);
        ph.arriveAndDeregister();

        // Phase 1
        currentPhase = ph.getPhase();
        System.out.println("Current Phase: " + currentPhase);
        setTotalSocks(socks);
        showSocks(socks);


    }

    public static void showSocks (int[] socks) {
        for (int i :
                socks) {
            System.out.print(i + " ");
        }
    }

    public static void setTotalSocks (int[] socks) {
        for (int i = 0; i < socks.length - 1; i++) {
            socks[socks.length - 1] += socks[i];
        }
    }

    private void setRandomSocks(Phaser ph, int[] socks, int color) {
        // Thread registers themselves to the Phaser
        ph.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                socks[color] = ThreadLocalRandom.current().nextInt(0, 100) + 1;
                ph.arriveAndAwaitAdvance();

                // Deregister threads from the number of parties
                ph.arriveAndDeregister();
            }
        }).start();
    }
}
