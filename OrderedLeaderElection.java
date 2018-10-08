import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection {

    private static class ElectedOfficial {
        String name;
        int rank;
    }

    public static void main(String[] args) throws InterruptedException {

        final int numThreads = 10;
        ArrayList<Thread> thread = new ArrayList<>();

        ElectedOfficial[] electedOfficials = new ElectedOfficial[numThreads];
        ElectedOfficial leader = new ElectedOfficial();

        Thread rankThread = new Thread(new RankThread(leader));
        rankThread.start();

        for (int i = 0; i < numThreads; i++) {
            electedOfficials[i] = new ElectedOfficial();
        }

        for (int i = 0; i < numThreads; i++) {
            thread.add(new Thread(new ElectedOfficialThread(rankThread, electedOfficials[i], leader)));
            thread.get(i).start();
            Thread.sleep(1000);
        }
    }

    private static class RankThread implements Runnable {

        ElectedOfficial leader;

        public RankThread(ElectedOfficial leader) {
            this.leader = leader;
            this.leader.name = null;
            this.leader.rank = Integer.MIN_VALUE;
        }

        @Override
        public void run() {
            boolean changingLeader = true;
            while (changingLeader) {
                changingLeader = false;
                try {
                    // Wait for a new elected officials to be created
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Changing Leader");
                    changingLeader = true;
                }
            }
        }
    }

    private static class ElectedOfficialThread implements Runnable {
        Thread rankThread;
        ElectedOfficial elecOff;
        ElectedOfficial leader;

        public ElectedOfficialThread(Thread rankThread, ElectedOfficial electedOfficials, ElectedOfficial leader) {
            this.rankThread = rankThread;
            this.elecOff = electedOfficials;
            this.leader = leader;
        }

        @Override
        public void run() {
            elecOff.name = Thread.currentThread().getName();
            elecOff.rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (leader.name == null) {
                System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", elecOff.name, elecOff.rank, Thread.currentThread().getName());
            } else {
                System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", elecOff.name, elecOff.rank, leader.name);
            }

            rankThread.interrupt();
        }
    }
}