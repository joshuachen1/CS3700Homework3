import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection {
    public static void main(String[] args) {
        final int numThreads = 10;
        Thread[] electedOfficialThread = new Thread[numThreads];
        Thread rankThread = new Thread(new RankThread());


        for (int i = 0; i < numThreads; i++) {
            electedOfficialThread[i] = new Thread(new ElectedOfficialThread());
            electedOfficialThread[i].start();
        }
    }

    private static class RankThread implements Runnable {
        @Override
        public void run() {
            try {
                // Wait for a new elected officials to be created
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    private static class ElectedOfficialThread implements Runnable {
        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            int rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            Thread leader = Thread.currentThread();

            System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", name, rank, leader.getName());

            // notify the rank thread that a new elected official has been created
            Thread.currentThread().interrupt();
        }
    }
}
