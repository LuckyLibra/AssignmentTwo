public class LamportClock {
    private static int time = 0;

    public static int getCurrentTime() { //synchronized to prevent race conditions
        return time;
    }

    public static void increaseTime () { //Increases the time by one, used in cases such as sending/receiving 
        time++;
    }

    public static void updateTime(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
    }

}
