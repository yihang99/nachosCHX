package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    public class KThreadAlarm {
        public KThread thread;
        public long time;

        public KThreadAlarm(KThread thread_, long time_) {
            thread = thread_;
            time = time_;
        }

        public KThread getThread() {
            return thread;
        }

        public long getTime() {
            return time;
        }
    }

    //waitQueue is ordered by the wakeTime
    public LinkedList<KThreadAlarm> waitQueue = new LinkedList<KThreadAlarm>();

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    boolean status1 = Machine.interrupt().disable();
    long currentTime = Machine.timer().getTime();
    int size = waitQueue.size();
    for(int i=0;i<size;i++) {
        if(waitQueue.get(i).getTime()<=currentTime) {
            KThread t = waitQueue.get(i).getThread();
            t.ready();
            //System.out.println("Wakeup Time = "+currentTime);
            waitQueue.remove(i);
            i=0;
            size = size -1;
            //currentTime = Machine.timer().getTime();
        }
    }
	//KThread.currentThread().yield();
    Machine.interrupt().restore(status1);
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
    boolean status1 = Machine.interrupt().disable();
    long wakeTime = Machine.timer().getTime() + x;
    //System.out.println("Wakeup Time in theory = "+ wakeTime);
    KThreadAlarm ka = new KThreadAlarm(KThread.currentThread(),wakeTime);
    int size = waitQueue.size();
    for(int i=0;i<=size;i++) {
        if((i==size) || (waitQueue.get(i).getTime()>wakeTime)) {
            if(i==0) 
                waitQueue.add(ka);
            else 
                waitQueue.add(i,ka);
            break;
        }
    }
    KThread.currentThread().sleep();

    Machine.interrupt().restore(status1);
	/*while (wakeTime > Machine.timer().getTime())
	    KThread.yield();*/
    }
}
