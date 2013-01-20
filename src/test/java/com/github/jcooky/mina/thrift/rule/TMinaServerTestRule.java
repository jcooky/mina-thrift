package com.github.jcooky.mina.thrift.rule;

import com.github.jcooky.mina.thrift.TIoAcceptorServerTransport;
import com.github.jcooky.mina.thrift.TIoSessionTransport;
import com.github.jcooky.mina.thrift.TMinaServer;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMinaServerTestRule implements TestRule {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private int PORT = 9091;
	private int SOCKET_TIMEOUT = 10 * 1000;
    private TProcessor processor;

	private TServer server;
	private TServerTransport socket ;
    private TSocket clientSocket;
    private TProtocol clientProtocol;

    public TMinaServerTestRule(TProcessor processor) {
        this.processor = processor;
    }

    public TProtocol getClientProtocol() {
        return clientProtocol;
    }

    @Override
    public Statement apply(final Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                init();
                try {
                    statement.evaluate();
                } finally {
                    close();
                }
            }
        };
    }

	public void init() throws Exception {
        logger.info("test log");

		socket = new TIoAcceptorServerTransport(PORT);
		server = new TMinaServer(new TMinaServer.Args(socket)
												.processor(processor)
												.protocolFactory(new TBinaryProtocol.Factory())
												.inputTransportFactory(new TIoSessionTransport.InputTransportFactory())
												.outputTransportFactory(new TTransportFactory()));

//		new Thread() {
//			public void run() {
				server.serve();
//			}
//		}.start();
        Thread.sleep(100);

        clientSocket = new TSocket("localhost", PORT, SOCKET_TIMEOUT);
        clientProtocol = new TBinaryProtocol(clientSocket);
        clientSocket.open();
	}
	
	public void close() throws Exception {
        clientSocket.close();
		server.stop();
		socket.close();
	}

}
