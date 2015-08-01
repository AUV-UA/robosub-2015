package org.auvua.model.dangerZona.hardware;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import fadecandy.opc.OpcClient;
import fadecandy.opc.OpcDevice;
import fadecandy.opc.PixelStrip;

/**
 * Display a moving white pixel with trailing orange/red flames
 * This looks pretty good with a ring of pixels.
 */
public class LEDStrips implements Runnable {
  
  private Map<LED_STRIP, int[]> map = new HashMap<LED_STRIP, int[]>();
  private Queue<Consumer<LEDStrips>> animationQueue = new LinkedList<Consumer<LEDStrips>>();
  
  OpcClient server;
  PixelStrip backleft;
  PixelStrip backright;
  PixelStrip frontleft;
  PixelStrip frontright;
  PixelStrip front;

  public LEDStrips() {
    String FC_SERVER_HOST = "192.168.1.31";
    int FC_SERVER_PORT = 7890;

    server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
    OpcDevice fadeCandy = server.addDevice();
    backleft = fadeCandy.addPixelStrip(0, 36);
    frontleft = fadeCandy.addPixelStrip(1, 36);
    front = fadeCandy.addPixelStrip(2, 30);
    backright = fadeCandy.addPixelStrip(3, 36);
    frontright = fadeCandy.addPixelStrip(4, 36);
    
    map.put(LED_STRIP.BACKLEFT, new int[36]);
    map.put(LED_STRIP.FRONTLEFT, new int[36]);
    map.put(LED_STRIP.FRONT, new int[30]);
    map.put(LED_STRIP.FRONTRIGHT, new int[36]);
    map.put(LED_STRIP.BACKRIGHT, new int[36]);

    new Thread(this).start();
  }

  public void run() {
    while(true) {
      if (!animationQueue.isEmpty()) {
        Consumer<LEDStrips> consumer = animationQueue.poll();
        System.out.println(consumer);
        consumer.accept(this);
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      sendUpdates();
    }
  }
  
  public void sendUpdates() {
    backleft.clear();
    backright.clear();
    front.clear();
    frontleft.clear();
    frontright.clear();
    
    for(int x = 0; x < 36; x++) {
      backleft.setPixelColor(x, map.get(LED_STRIP.BACKLEFT)[x]);
    }
    for(int x = 0; x < 36; x++) {
      frontleft.setPixelColor(x, map.get(LED_STRIP.FRONTLEFT)[x]);
    }
    for(int x = 0; x < 30; x++) {
      front.setPixelColor(x, map.get(LED_STRIP.FRONT)[x]);
    }
    for(int x = 0; x < 36; x++) {
      frontright.setPixelColor(x, map.get(LED_STRIP.FRONTRIGHT)[x]);
    }
    for(int x = 0; x < 36; x++) {
      backright.setPixelColor(x, map.get(LED_STRIP.BACKRIGHT)[x]);
    }

    server.show();
  }
  
  public void setStripColor(LED_STRIP strip, int color) {
    int[] arr = map.get(strip);
    int length = arr.length;
    for (int i = 0; i < length; i++) {
      arr[i] = color;
    }
    sendUpdates();
  }

  public void setPixel(LED_STRIP strip, int pixel, int color) {
    map.get(strip)[pixel] = color;
    sendUpdates();
  }
  
  public void clearStrip(LED_STRIP strip) {
    int length = map.get(strip).length;
    map.put(strip, new int[length]);
    sendUpdates();
  }
  
  public void clearAllStrips() {
    clearStrip(LED_STRIP.BACKLEFT);
    clearStrip(LED_STRIP.FRONTLEFT);
    clearStrip(LED_STRIP.FRONT);
    clearStrip(LED_STRIP.FRONTRIGHT);
    clearStrip(LED_STRIP.BACKRIGHT);
    sendUpdates();
  }
  
  public void enqueueAnimation(Consumer<LEDStrips> consumer) {
    animationQueue.add(consumer);
  }
  
  public enum LED_STRIP {
    BACKLEFT,
    FRONTLEFT,
    FRONT,
    FRONTRIGHT,
    BACKRIGHT
  };

}
