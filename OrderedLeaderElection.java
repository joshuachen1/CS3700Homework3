import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection {
    public static void main(String[] args) throws InterruptedException {
        final int numThreads = 10;
        Thread[] electedOfficialThread = new Thread[numThreads];
        Thread rankThread = new Thread(new RankThread(electedOfficialThread));

        rankThread.start();

        for (int i = 0; i < numThreads; i++) {
            electedOfficialThread[i] = new Thread(new ElectedOfficialThread(rankThread));
            electedOfficialThread[i].start();
            Thread.sleep(1000);
        }
    }

    private static class RankThread implements Runnable {
        Thread[] electedOfficialThread;

        public RankThread(Thread[] electedOfficialThread) {
            this.electedOfficialThread = electedOfficialThread;
        }

        @Override
        public void run() {
            try {
                // Wait for a new elected officials to be created
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Hello");
            }
        }
    }

    private static class ElectedOfficialThread implements Runnable {
        Thread rankThread;

        public ElectedOfficialThread(Thread rankThread) {
            this.rankThread = rankThread;
        }
        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            int rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            Thread leader = Thread.currentThread();

            synchronized (this) {
                try {
                    System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", name, rank, leader.getName());

                    // notify the rank thread that a new elected official has been created
                    rankThread.interrupt();
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}