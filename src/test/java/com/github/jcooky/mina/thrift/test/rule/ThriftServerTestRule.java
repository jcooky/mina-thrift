package com.github.jcooky.mina.thrift.test.rule;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.THsHaServer.Args;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ThriftServerTestRule {
	public static final int PORT = 9090;
	public static final int SOCKET_TIMEOUT = 1000;

    private TProcessor processor;
	private TServer server;
	private TNonblockingServerSocket serverSocket ;
    private TTransport clientTransport;

    private TProtocol clientProtocol;

    public ThriftServerTestRule(TProcessor processor) {
        this.processor = processor;
    }

    public TProtocol getClientProtocol() {
        return clientProtocol;
    }

	public void setUp() throws Exception {
		serverSocket = new TNonblockingServerSocket(PORT);
		server = new THsHaServer(new Args(serverSocket)
												.processor(processor)
												.protocolFactory(new TCompactProtocol.Factory())
												.transportFactory(new TFramedTransport.Factory()));

		new Thread() {
			public void run() {
				server.serve();
			}
		}.start();

        //Setup the transport and protocol
        TSocket clientSocket = new TSocket("127.0.0.1", PORT, SOCKET_TIMEOUT);
        clientTransport = new TFramedTransport(clientSocket);
        clientProtocol = new TCompactProtocol(clientTransport);

        //The transport must be opened before you can begin using
        clientTransport.open();
	}

	public void tearDown() {
        clientTransport.close();
		server.stop();
		serverSocket.close();
	}
}
