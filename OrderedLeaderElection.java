import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection {
    public static void main(String[] args) throws InterruptedException {
        final int numThreads = 10;
        ElectedOfficial[] elecOff = new ElectedOfficial[numThreads];
        Thread[] electedOfficialThread = new Thread[numThreads];
        Thread rankThread = new Thread(new RankThread(electedOfficialThread, elecOff));

        rankThread.start();

        for (int i = 0; i < numThreads; i++) {
            electedOfficialThread[i] = new Thread(new ElectedOfficialThread(rankThread, elecOff, i));
            electedOfficialThread[i].start();
            Thread.sleep(1000);
        }
    }

    private static class RankThread implements Runnable {
        Thread[] electedOfficialThread;
        Thread currentLeader;
        int highestRank;
        ElectedOfficial[] elecOff;

        public RankThread(Thread[] electedOfficialThread, ElectedOfficial[] elecOff) {
            this.electedOfficialThread = electedOfficialThread;
            this.currentLeader = Thread.currentThread();
            int highestRank = Integer.MIN_VALUE;
            this.elecOff = elecOff;
        }

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
        Thread rankThread;
        ElectedOfficial elecOff;

        public ElectedOfficialThread(Thread rankThread, ElectedOfficial[] elecOff, int ID) {
            this.rankThread = rankThread;
            elecOff[ID] = new ElectedOfficial();
            this.elecOff = elecOff[ID];
        }

        @Override
        public void run() {
            elecOff.name = Thread.currentThread().getName();
            elecOff.rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            elecOff.leader = Thread.currentThread();

            synchronized (this) {
                    try {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", elecOff.name, elecOff.rank, elecOff.leader.getName());

                        // notify the rank thread that a new elected official has been created
                        rankThread.interrupt();
                        wait();
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", elecOff.name, elecOff.rank, elecOff.leader.getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}