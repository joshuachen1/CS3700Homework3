import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class SockMatching {
    public static void main(String[] args) throws InterruptedException{
        Phaser ph1 = new Phaser();
        Phaser ph2 = new Phaser();
        Phaser ph3 = new Phaser();
        final int numThreads = 4;

        ArrayList<Socks> socks = new ArrayList<>(Arrays.asList(
                new Socks("Red"),
                new Socks("Green"),
                new Socks("Blue"),
                new Socks("Orange")
        ));

        // Phase 0
        int currentPhase = ph1.getPhase();
        System.out.println("Current Phase: " + currentPhase);

        // Generate random number of socks
        for (int i = 0; i < numThreads; i++) {
            new SockMatching().generatingSocksThread(ph1, ph2, ph3, socks, i);
        }
    }

    private void generatingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, ArrayList<Socks> socks, int color) {
        // Thread registers themselves to the Phaser
        ph1.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socks threadSock = socks.get(color);
                threadSock.setTotalSocks(ThreadLocalRandom.current().nextInt(0, 100) + 1);

                for (int i = 0; i < threadSock.getTotalSocks(); i++) {
                    ph1.arrive();

                    threadSock.addNumSocks();

                    ph3.awaitAdvance(ph3.getPhase());
                }
                ph1.arriveAndDeregister();
            }
        }).start();
    }
    private void matchingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, ArrayList<Socks> socks) {
        ph2.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean matching = true;
                while (matching) {
                    ph1.awaitAdvance(ph1.getPhase());
                    Socks redSock = socks.get(0);
                    Socks greenSock = socks.get(1);
                    Socks blueSock = socks.get(2);;
                    Socks orangeSock = socks.get(3);

                    if (redSock.getNumSocks() >= 2) {
                        redSock.removeSockPair();
                        ph2.arrive();
                    }

                }
            }
        }).start();
    }
}
