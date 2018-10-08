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

        ArrayList<ElectedOfficial> electedOfficials = new ArrayList<>();
        ElectedOfficial leader = new ElectedOfficial();

        Thread rankThread = new Thread(new RankThread(electedOfficials ,leader));
        rankThread.start();

        synchronized (leader) {
            for (int i = 0; i < numThreads; i++) {
                electedOfficials.add(new ElectedOfficial());
                thread.add(new Thread(new ElectedOfficialThread(rankThread, electedOfficials.get(i), leader)));
                thread.get(i).start();
                Thread.sleep(1000);
                leader.wait();
            }
        }
    }

    private static class RankThread implements Runnable {
        ArrayList<ElectedOfficial> electedOfficials;
        ElectedOfficial leader;

        public RankThread(ArrayList<ElectedOfficial> electedOfficials, ElectedOfficial leader) {
            this.electedOfficials = electedOfficials;
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
                    synchronized (leader) {
                        System.out.println("Changing Leader");
                        changingLeader = true;

                        for (int i = 0; i < electedOfficials.size(); i++) {

                        }
                        leader.notifyAll();
                    }
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
            synchronized (leader) {
                try {
                    elecOff.name = Thread.currentThread().getName();
                    elecOff.rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);

                    if (leader.name == null) {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", elecOff.name, elecOff.rank, Thread.currentThread().getName());
                    } else {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", elecOff.name, elecOff.rank, leader.name);
                    }

                    rankThread.interrupt();
                    leader.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}