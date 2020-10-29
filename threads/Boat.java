package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
	static Lock lock;
	static Condition oc;
	static Condition mc;
	static Condition oa;
	static int ocnum;
	static int oanum;
	static boolean boatAtO;
	static boolean done;
	static boolean isSecondChild;
	static boolean isAdult;

	public Boat() {
		
	}
    
    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();
	
	//System.out.println("\n ***Testing Boats with only 2 children***");
	//begin(0, 2, b);

	//System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
  	//begin(1, 2, b);

  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
  	begin(3, 3, b);
    }

    public static void begin( int adults, int children, BoatGrader b )
    {
	// Store the externally generated autograder in a class
	// variable to be accessible by children.
	bg = b;

	// Instantiate global variables here
	
	// Create threads here. See section 3.4 of the Nachos for Java
	// Walkthrough linked from the projects page.

	/*Runnable r = new Runnable() {
	    public void run() {
                SampleItinerary();
            }
        };
        KThread t = new KThread(r);
        t.setName("Sample Boat Thread");
        t.fork();*/

		ocnum = children;
		oanum = adults;
		lock = new Lock();
		oc = new Condition(lock);
		mc = new Condition(lock);
		oa = new Condition(lock);
		boatAtO = true;
		done = false;
		isSecondChild = false;
		isAdult = false;

		for(int i=0;i<adults;i++) {
			new KThread(new Runnable(){
				public void run() {
					AdultItinerary();
				}
			}).fork();
		}
		for(int i=0;i<children;i++) {
			new KThread(new Runnable(){
				public void run() {
					ChildItinerary();
				}
			}).fork();
		}

    }

    static void AdultItinerary()
    {
	bg.initializeAdult(); //Required for autograder interface. Must be the first thing called.
	//DO NOT PUT ANYTHING ABOVE THIS LINE. 

	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/


	lock.acquire();
	while(!(isAdult&&boatAtO)) {
		oa.sleep();
	}
	oanum = oanum - 1;
	bg.AdultRowToMolokai();
	mc.wake();
	isAdult = false;
	boatAtO = false;
	lock.release();

    }

    static void ChildItinerary()
    {
	bg.initializeChild(); //Required for autograder interface. Must be the first thing called.
	//DO NOT PUT ANYTHING ABOVE THIS LINE. 

	lock.acquire();
	boolean isO = true;//isO==true means the child is at O. Otherwise it is at M.
	while(!done) {
		if (isO) {//wake up at O
			if (isAdult) {
				oa.wake();
				oc.sleep();
			}
			else if (boatAtO){
				ocnum = ocnum - 1;
				if (!isSecondChild) {
					oc.wake();
					isSecondChild = true;
					bg.ChildRowToMolokai();
				}
				else {
					bg.ChildRideToMolokai();
					boatAtO = false;
					if  (oanum==0&&ocnum==0){
						//System.out.println("Done!");
						done = true;
						oc.sleep();
					}
					mc.wake();
					isSecondChild = false;
					if (oanum>0&&ocnum==0) {
						isAdult = true;
					}
				}
				isO = false;
				mc.sleep();
			}
			else {
				oa.wake();
				oc.sleep();
			}
		}
		else {//wake up at M
			bg.ChildRowToOahu();
			ocnum = ocnum + 1;
			boatAtO = true;
			if(isAdult) {
				oa.wake();
			}
			else {
				oc.wake();
			}
			isO = true;
			oc.sleep();
		}
	}



	lock.release();


    }

    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();
    }
    
}
