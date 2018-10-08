public class Socks {
    String color;
    int numSocks;
    int currentNumSocks;
    int totalSocks;

    public Socks (String color) {
        this.color = color;
        numSocks = 0;
    }

    public void setTotalSocks(int totalSocks) {
        this.totalSocks = totalSocks;
    }

    public int getTotalSocks() {
        return totalSocks;
    }

    public void addNumSocks() {
        numSocks += 1;
        currentNumSocks += 1;
        System.out.println(color + " Sock: Produced " + currentNumSocks + " of " + totalSocks + " " + color + " Socks");
    }

    public int getNumSocks() {
        return numSocks;
    }

    public void removeSockPair() {
        numSocks -= 2;
    }
}
