import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class SockMatching {
    public static void main(String[] args) throws InterruptedException{
        final int numThreads = 4;

        // Four color sock Threads
        Phaser ph1 = new Phaser(numThreads);
        // One Matching Thread
        Phaser ph2 = new Phaser(1);
        // One Washing Thread
        Phaser ph3 = new Phaser(1);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socks threadSock = socks.get(color);
                threadSock.setTotalSocks(ThreadLocalRandom.current().nextInt(0, 100) + 1);

                for (int i = 0; i < threadSock.getTotalSocks(); i++) {
                    ph1.arrive();

                    threadSock.addNumSocks();

                    matchingSocksThread(ph1, ph2, ph3, socks);

                    // wait for matchingSocksThread to determine pairs
                    ph2.awaitAdvance(ph2.getPhase());
                }
            }
        }).start();
    }
    private void matchingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, ArrayList<Socks> socks) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean matching = true;
                while (matching) {
                    // Wait for socks to be generated
                    ph1.awaitAdvance(ph1.getPhase());

                    for (int i = 0; i < socks.size(); i++) {
                        Socks colorSock = socks.get(i);
                        if (colorSock.getNumSocks() >= 2) {
                            System.out.println("Matching Thread: Send " + colorSock.color + " Socks to Washer");
                            new SockMatching().washingSocksThread(ph1, ph2, ph3, colorSock);
                            ph3.awaitAdvance(ph3.getPhase());
                        }
                    }
                    // Notify matching complete
                    ph2.arrive();
                }
            }
        }).start();
    }

    private void washingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, Socks sock) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sock.removeSockPair();
                // Notify generatingSockThread
                ph3.arrive();
                System.out.println("Washing Thread: Destroyed " + sock.color + " socks");
            }
        }).start();
    }
}
