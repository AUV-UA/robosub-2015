import zmq
import inputMessage_pb2
import outputMessage_pb2
import google.protobuf

socket = ZMQConnection()
socket.connect("127.0.0.1:5560")

while(1):
    if(socket.request_waiting()):
        response = socket.receive()
        print ParseFromString(response)
        socket.send(response)
