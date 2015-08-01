import zmq
import inputMessage_pb2
import outputMessage_pb2

ctx = zmq.Context.instance()
socket = ctx.socket(zmq.REP)
socket.bind("tcp://127.0.0.1:5560")

while(1):
    inputs = socket.recv()
    input_message = inputMessage_pb2.InputMessage()
    input_message.ParseFromString(inputs)
    print input_message
    socket.send(inputs)
