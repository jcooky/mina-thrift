package com.github.jcooky.mina.thrift.test.rule;

import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.TIoAcceptorServerTransport;
import com.github.jcooky.mina.thrift.TMinaServer;
import com.github.jcooky.mina.thrift.codec.TFrameProtocolCodecFactory;

public class TMinaServerTestRule {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private int PORT = 9091;
	private int SOCKET_TIMEOUT = 10 * 1000;
	private TProcessor processor;

	private TServer server;
	private TTransport clientSocket;
	private TProtocol clientProtocol;

	public TMinaServerTestRule(TProcessor processor) {
		this.processor = processor;
	}

	public TProtocol getClientProtocol() {
		return clientProtocol;
	}

	public void starting() throws Exception {
		logger.info("test log");

		NioSocketAcceptor acceptor;
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		acceptor.setReuseAddress(true);

		acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));

		acceptor.getFilterChain().addLast(TIoAcceptorServerTransport.CODEC_NAME, new ProtocolCodecFilter(new TFrameProtocolCodecFactory()));

		server = new TMinaServer(new TMinaServer.Args(new TIoAcceptorServerTransport(acceptor)).processor(processor)
				.protocolFactory(new TCompactProtocol.Factory()).transportFactory(new TTransportFactory()));

		// new Thread() {
		// public void run() {
		server.serve();
		// }
		// }.start();

		clientSocket = new TSocket("localhost", PORT, SOCKET_TIMEOUT);
		clientSocket = new TFramedTransport(clientSocket);
		clientProtocol = new TCompactProtocol(clientSocket);
		clientSocket.open();
	}

	public void finished() throws Exception {
		clientSocket.close();
		server.stop();
	}

}
