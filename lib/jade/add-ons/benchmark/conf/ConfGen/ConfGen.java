
import java.io.*;
import java.util.*;

/*
*
*  Simple configuration generator for JADE
*
*/
class ConfGen {


public static void main (String args[]) {

 try {
	
	PrintStream out = new PrintStream( new FileOutputStream("output.conf.txt") );


	
	for (int j=0; j<1000; j++) {

		String i= Integer.toString( j ).trim();
		StringBuffer line = new StringBuffer();		
		
		
		/*
		// --- Single Container ---
		line.append( "r" + i);
		line.append( ":benchmark.roundTripTime.RoundTripReceiver " );
		line.append( "s" + i );
		line.append( ":benchmark.roundTripTime.RoundTripSender(" );
		line.append( "r" + i );
		line.append( " ${iterations} ${ior} ${couples}) \\ ");
		*/


		
		// 1 Host - 2 JVMs   (SENDERS)
		line.append( "s" + i );
		line.append( ":benchmark.roundTripTime.RoundTripSender(");
		line.append( "r" + i );
		line.append( " ${iterations} ${ior} ${couples}) \\ " );
		

/*
		// 1 Host - 2 JVMs   (RECEIVERS)
		line.append( "r" + i);
		line.append( ":benchmark.roundTripTime.RoundTripReceiver \\ " );
*/		
		
		
		
		out.println( line.toString() );

	} // end for
		
} catch (Exception e) { e.printStackTrace(); }
 
 }

}