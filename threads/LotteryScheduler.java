package nachos.threads;

import nachos.machine.*;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;
import java.lang.Math;

/**
 * A scheduler that chooses threads using a lottery.
 *
 * <p>
 * A lottery scheduler associates a number of tickets with each thread. When a
 * thread needs to be dequeued, a random lottery is held, among all the tickets
 * of all the threads waiting to be dequeued. The thread that holds the winning
 * ticket is chosen.
 *
 * <p>
 * Note that a lottery scheduler must be able to handle a lot of tickets
 * (sometimes billions), so it is not acceptable to maintain state for every
 * ticket.
 *
 * <p>
 * A lottery scheduler must partially solve the priority inversion problem; in
 * particular, tickets must be transferred through locks, and through joins.
 * Unlike a priority scheduler, these tickets add (as opposed to just taking
 * the maximum).
 */
public class LotteryScheduler extends PriorityScheduler {
    /**
     * Allocate a new lottery scheduler.
     */
    public LotteryScheduler() {
    }

    public boolean increasePriority() {
		boolean intStatus = Machine.interrupt().disable();
				
		KThread thread = KThread.currentThread();

		int priority = getPriority(thread);

		setPriority(thread, priority+1);

		Machine.interrupt().restore(intStatus);
		return true;
    }

    public boolean decreasePriority() {
		boolean intStatus = Machine.interrupt().disable();
				
		KThread thread = KThread.currentThread();

		int priority = getPriority(thread);
		if (priority == 1)
			return false;

		setPriority(thread, priority-1);

		Machine.interrupt().restore(intStatus);
		return true;
    }

    protected class LotteryQueue extends PriorityQueue {
        public LotteryQueue(boolean transferPriority) {
            super(transferPriority);
        }
        protected ThreadState pickNextThread() {
            if(waitQueue.size()==0)
				return null;
			int totalLottery = 0;
            int index = -10;
			int size = waitQueue.size();
			for (int i = 0; i < size; i++) {
				int pri = getThreadState(waitQueue.get(i)).getEffectivePriority();
				totalLottery = totalLottery + pri;
			}
            int t = Lib.random(totalLottery+1);
            int sum = 0;
            for (int i = 0; i < size; i++) {
				int pri = getThreadState(waitQueue.get(i)).getEffectivePriority();
				sum  = sum + pri;
                if(sum>t) {
                    index = i;
                    break;
                }
			}
            if(index==-10) {
                return null;
            }
            else {
                return getThreadState(waitQueue.get(index));
            }
		}
    }

    protected class LotteryThreadState extends ThreadState {
        public LotteryThreadState(KThread t) {
            super(t);
        }
        public int getEffectivePriority() {
        			 //Lib.assertTrue(Machine.interrupt().disabled());
            ef = priority;
            for(Iterator i = holdQueue.iterator(); i.hasNext();) {
					for(Iterator j = ((PriorityQueue)i.next()).waitQueue.iterator();j.hasNext();) {
						int p = getThreadState((KThread)j.next()).priority;
					    ef= p+ef;
					}
			}
			return ef;
        }
    }
    
    /**
     * Allocate a new lottery thread queue.
     *
     * @param	transferPriority	<tt>true</tt> if this queue should
     *					transfer tickets from waiting threads
     *					to the owning thread.
     * @return	a new lottery thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
	// implement me
	return new LotteryQueue(transferPriority);
    }

}
