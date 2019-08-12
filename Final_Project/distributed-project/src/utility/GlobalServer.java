package utility;

import sequencer.Sequencer;
import servers.rm1.ImplementRemoteInterface.RepManager1;
import servers.rm2.ImplementRemoteInterface.RepManager2;
import servers.rm3.server.RepManager3;

public class GlobalServer {

	public static void main(String args[])
	{
		Sequencer.run();	
		RepManager1.run();
		RepManager2.run();
		RepManager3.run();
	}
}
