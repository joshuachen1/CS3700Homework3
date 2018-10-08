import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection {

    private static class ElectedOfficial {
        String name;
        int rank;
        int ID;

        public ElectedOfficial(String name, int rank) {
            this.name = name;
            this.rank = rank;
        }
        public ElectedOfficial(String name, int rank, int ID) {
            this.name = name;
            this.rank = rank;
            this.ID = ID;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final int numThread = 10;
        ArrayList<Thread> threads = new ArrayList<>();

        ArrayList<ElectedOfficial> electedOfficials = new ArrayList<>();
        ElectedOfficial leader = new ElectedOfficial(Thread.currentThread().getName(), Integer.MIN_VALUE, 0);

        Thread rankThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean changingLeader = true;
                while (changingLeader) {
                    changingLeader = false;
                    synchronized (leader) {
                        try {
                            // Wait for a new elected officials to be created
                            leader.wait(5000);
                        } catch (InterruptedException e) {
                            changingLeader = true;

                            for (int i = leader.ID; i < electedOfficials.size(); i++) {
                                if (electedOfficials.get(i).rank > leader.rank) {
                                    System.out.println("\nChanging leader\n");
                                    updateLeader(leader, electedOfficials.get(i), i);
                                }
                            }
                        }
                    }
                }
            }
        });

        rankThread.start();

        for (int i = 0; i < numThread; i++) {
            electedOfficials.add(new ElectedOfficial(Thread.currentThread().getName(), Integer.MIN_VALUE));

            int index = i;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    electedOfficials.get(index).name = Thread.currentThread().getName();
                    electedOfficials.get(index).rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);

                    if (leader.name == null) {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", electedOfficials.get(index).name, electedOfficials.get(index).rank, electedOfficials.get(index).name);
                    } else {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", electedOfficials.get(index).name, electedOfficials.get(index).rank, leader.name);
                    }

                    // Notify rankThread of new elected official
                    rankThread.interrupt();
                }
            }));

            threads.get(i).start();
            Thread.sleep(1500);
        }
    }

    private static void updateLeader(ElectedOfficial currLeader, ElectedOfficial newLeader, int ID) {
        currLeader.name = newLeader.name;
        currLeader.rank = newLeader.rank;
        currLeader.ID = ID;
    }
}
