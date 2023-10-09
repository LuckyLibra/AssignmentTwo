public class LamportClock {
    public int time;

    public LamportClock() { //lamport clock class which handles updates
        this.time = 0; //initialise time as 0 to begin with 
    }

    public synchronized int getCurrentTime() { //synchronized to prevent race conditions
        return time;
    }

    public synchronized void increaseTime () { //Increases the time by one, used in cases such as sending/receiving 
        time++;
    }



}
