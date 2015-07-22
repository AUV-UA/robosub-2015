package org.auvua.model.dangerZona.hardware;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class DzHardwareSocket {
  private final String SOCKET_ADDRESS = "tcp://127.0.0.1:5560";
  private Context ctx;
  private Socket req;
  
  public DzHardwareSocket() {
    ctx = ZMQ.context(1);
    req = ctx.socket(ZMQ.REQ);
    req.connect(SOCKET_ADDRESS);
  }
  
  public byte[] sendData(byte[] message) {
    req.send(message, 0);
    return req.recv();
  }
}
