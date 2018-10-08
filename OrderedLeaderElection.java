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
                                    System.out.println("Changing leader from " + leader.name + " to " + electedOfficials.get(i).name);
                                    updateLeader(leader, electedOfficials.get(i), i);
                                    leader.notifyAll();
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
                    String name = Thread.currentThread().getName();
                    electedOfficials.get(index).name = name;
                    int rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
                    electedOfficials.get(index).rank = rank;

                    synchronized (leader) {
                        try {
                            System.out.println("\nCreating " + name);
                            // Notify rankThread of new elected official
                            rankThread.interrupt();
                            leader.wait(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (leader.name == null) {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", name, rank, name);
                    } else {
                        System.out.printf("Name: %10s \tRank: %12d \tLeader: %10s\n", name, rank, leader.name);
                    }
                }
            }));

            threads.get(i).start();
            Thread.sleep(500);
        }
    }

    private static void updateLeader(ElectedOfficial currLeader, ElectedOfficial newLeader, int ID) {
        currLeader.name = newLeader.name;
        currLeader.rank = newLeader.rank;
        currLeader.ID = ID;
    }
}
