package fadecandy.examples;

import org.auvua.model.dangerZona.hardware.LEDStrips;

public class TestLedStrips {
  
  public static void main(String[] args) {
    LEDStrips strips = new LEDStrips();
    
    while(true) {
      for(int x = 2; x < 36; x++) {
        strips.clearStrip(LEDStrips.LED_STRIP.BACKRIGHT);
        strips.clearStrip(LEDStrips.LED_STRIP.BACKLEFT);
        strips.setPixel(LEDStrips.LED_STRIP.BACKRIGHT, 35 - x, 0xffffff);
        strips.setPixel(LEDStrips.LED_STRIP.BACKRIGHT, 35 - (x-1), 0xbbbbbb);
        strips.setPixel(LEDStrips.LED_STRIP.BACKRIGHT, 35 - (x-2), 0x777777);
        strips.setPixel(LEDStrips.LED_STRIP.BACKLEFT, x, 0xffffff);
        strips.setPixel(LEDStrips.LED_STRIP.BACKLEFT, x-1, 0xbbbbbb);
        strips.setPixel(LEDStrips.LED_STRIP.BACKLEFT, x-2, 0x777777);
        try {
          Thread.sleep(20);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
      for(int x = 35; x > 2; x--) {
        strips.clearStrip(LEDStrips.LED_STRIP.BACKRIGHT);
        strips.clearStrip(LEDStrips.LED_STRIP.BACKLEFT);
        strips.setPixel(LEDStrips.LED_STRIP.BACKRIGHT, 35 - x, 0x777777);
        strips.setPixel(LEDStrips.LED_STRIP.BACKRIGHT, 35 - (x-1), 0xbbbbbb);
        strips.setPixel(LEDStrips.LED_STRIP.BACKRIGHT, 35 - (x-2), 0xffffff);
        strips.setPixel(LEDStrips.LED_STRIP.BACKLEFT, x, 0x777777);
        strips.setPixel(LEDStrips.LED_STRIP.BACKLEFT, x-1, 0xbbbbbb);
        strips.setPixel(LEDStrips.LED_STRIP.BACKLEFT, x-2, 0xffffff);
        try {
          Thread.sleep(20);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
}
