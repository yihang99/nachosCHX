package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public LinkedList<Integer> waitQueue;
    public Condition speaker, listener;
    public Lock lock;
    public int speakerNum, listenerNum;


    public Communicator() {
        lock = new Lock();
        speakerNum = 0;
        listenerNum = 0;
        speaker = new Condition(lock);
        listener = new Condition(lock);
        waitQueue = new LinkedList<Integer>();
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        boolean status1 = Machine.interrupt().disable();
        lock.acquire();
        waitQueue.offer(((Integer)word));
        if(listenerNum>0) {
            //speakerNum = speakerNum + 1;
            listener.wake();
            //listenerNum =  listenerNum - 1;
        }
        else {
            speakerNum = speakerNum + 1;
            speaker.sleep();
            speakerNum = speakerNum - 1;
            //listenerNum =  listenerNum - 1;
        }
        lock.release();
        Machine.interrupt().restore(status1);
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
        lock.acquire();
        boolean status1 = Machine.interrupt().disable();
        int word = 0;
        if(speakerNum>0) {
            //listenerNum =  listenerNum + 1;
            word = waitQueue.poll();
            speaker.wake();
            //speakerNum = speakerNum - 1;
        }
        else {
            listenerNum =  listenerNum + 1;
            listener.sleep();
            word = waitQueue.poll();
            listenerNum =  listenerNum - 1;
            //speakerNum = speakerNum - 1;
        }
        lock.release();
        Machine.interrupt().restore(status1);
	    return word;
    }
}
