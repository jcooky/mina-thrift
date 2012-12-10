package com.github.jcooky.mina.thrift;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TIoAcceptorServerTransport extends TServerTransport {
	private static final Logger logger = LoggerFactory.getLogger(TIoAcceptorServerTransport.class);
	
	private IoAcceptor acceptor;
	
	public TIoAcceptorServerTransport(int port) {
		NioSocketAcceptor acceptor ;
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		acceptor.setReuseAddress(true);
		
		acceptor.setDefaultLocalAddress(new InetSocketAddress(port));
		
		acceptor.getFilterChain().addLast("logging", new LoggingFilter());
		
		this.acceptor = acceptor;
	}
	
	public IoAcceptor getAcceptor() {
		return acceptor;
	}
	
	public void setHandler(IoHandler handler) {
		acceptor.setHandler(handler);
	}

	public void listen() throws TTransportException {
		try {
			acceptor.bind();
		} catch(IOException e) {
			e.printStackTrace();
			throw new TTransportException(e);
		}
	}

	public void close() {
		if (!acceptor.isDisposed() && !acceptor.isDisposing()) {
			acceptor.dispose(true);
		}
	}

	protected TIoSessionTransport acceptImpl() throws TTransportException {
		throw new UnsupportedOperationException();
	}

}
