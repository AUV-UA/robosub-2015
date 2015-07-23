package fadecandy.examples;

import fadecandy.opc.Animation;
import fadecandy.opc.OpcClient;
import fadecandy.opc.OpcDevice;
import fadecandy.opc.PixelStrip;


/**
 * Display a moving white pixel with trailing orange/red flames
 * This looks pretty good with a ring of pixels.
 */
public class TestFadeCandy {
	
	public static void main(String[] args) throws Exception {
		String FC_SERVER_HOST = "192.168.1.31";
		int FC_SERVER_PORT = 7890;
		int STRIP1_COUNT = 30;
		
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, STRIP1_COUNT);
		
		strip1.setPixelColor(0, 0x00007F);
		
		Thread.sleep(2000);
		
		server.close();
	}
}
