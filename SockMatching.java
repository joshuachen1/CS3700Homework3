import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class SockMatching {
    public static void main(String[] args) throws InterruptedException{
        final int numThreads = 4;

        // Four color sock Threads
        Phaser ph1 = new Phaser();
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
        new SockMatching().matchingSocksThread(ph1, ph2, ph3, socks);

        // Generate random number of socks
        for (int i = 0; i < numThreads; i++) {
            new SockMatching().generatingSocksThread(ph1, ph2, ph3, socks, i);
        }

        Thread.sleep(10000);
        for (Socks s :
                socks) {
            System.out.println(s.color + " " + s.getNumSocks() + " remaining");
        }

    }

    private void generatingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, ArrayList<Socks> socks, int color) {
        ph1.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socks threadSock = socks.get(color);
                threadSock.setTotalSocks(ThreadLocalRandom.current().nextInt(0, 100) + 1);

                for (int i = 0; i < threadSock.getTotalSocks(); i++) {
                    threadSock.addNumSocks();

                    if (threadSock.getNumSocks() >= 2) {
                        // Barrier and notify to check pairs
                        int currentPhase = ph1.arrive();
                        ph1.awaitAdvance(currentPhase);

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ph1.arriveAndDeregister();
            }
        }).start();
    }
    private void matchingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, ArrayList<Socks> socks) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean matching = true;
                while (matching) {
                    matching = false;

                    ph1.awaitAdvance(ph1.getPhase());

                    for (int i = 0; i < socks.size(); i++) {
                        Socks currentSock = socks.get(i);

                        if (currentSock.numSocks >= 2) {
                            matching = true;
                            System.out.println("Matching " + currentSock.color + " Socks");
                            currentSock.removeSockPair();
                        }
                    }
                }
            }
        }).start();
    }

    private void washingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, Socks sock) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sock.removeSockPair();
                System.out.println("Washing Thread: Destroyed " + sock.color + " socks");

            }
        }).start();
    }
}
