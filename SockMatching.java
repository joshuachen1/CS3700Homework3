import javax.naming.ldap.SortKey;
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
        // Washing Thread
        Phaser ph3 = new Phaser();

        ArrayList<Socks> socks = new ArrayList<>(Arrays.asList(
                new Socks("Red"),
                new Socks("Green"),
                new Socks("Blue"),
                new Socks("Orange")
        ));

        ArrayList<Socks> sockQueue = new ArrayList<>();

        new SockMatching().matchingSocksThread(ph1, ph2, ph3, socks, sockQueue);

        // Generate random number of socks
        for (int i = 0; i < numThreads; i++) {
            new SockMatching().generatingSocksThread(ph1, ph2, ph3, socks, i);
        }

        Thread.sleep(30000);
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
                        // notify to check pairs
                        ph1.arrive();

                        // Give matchingSocksThread time
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ph3.awaitAdvance(ph3.getPhase());
                    }
                }
                ph1.arriveAndDeregister();
            }
        }).start();
    }
    private void matchingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, ArrayList<Socks> socks, ArrayList<Socks> sockQueue) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean matching = true;
                while (matching) {
                    matching = false;

                    // generatingSocksThread has a pair
                    ph1.awaitAdvance(ph1.getPhase());

                    for (int i = 0; i < socks.size(); i++) {
                        Socks currentSock = socks.get(i);

                        if (currentSock.numSocks >= 2) {
                            matching = true;

                            sockQueue.add(currentSock);
                            System.out.println("Send " + currentSock.color + " Socks to Washer. Total inside queue " + sockQueue.size());
                        }

                        if (sockQueue.size() > 0) {
                            new SockMatching().washingSocksThread(ph1, ph2, ph3, sockQueue.remove(0));
                            // Give matchingSocksThread time
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            ph3.awaitAdvance(ph3.getPhase());
                        }
                    }
                }
            }
        }).start();
    }

    private void washingSocksThread(Phaser ph1, Phaser ph2, Phaser ph3, Socks sock) {
        ph3.register();

        new Thread(new Runnable() {
            @Override
            public void run() {

                sock.removeSockPair();
                System.out.println("Washing Thread: Destroyed " + sock.color + " socks");

                // Notify finished destroying
                ph3.arriveAndDeregister();
            }
        }).start();
    }
}
