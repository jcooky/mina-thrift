package com.github.jcooky.mina.thrift;

import com.github.jcooky.mina.thrift.codec.TFrameProtocolCodecFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TIoAcceptorServerTransport extends TServerTransport {
	private static final Logger logger = LoggerFactory.getLogger(TIoAcceptorServerTransport.class);
	
	private IoAcceptor acceptor;
	
	public TIoAcceptorServerTransport(int port) {
		NioSocketAcceptor acceptor ;
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		acceptor.setReuseAddress(true);
		
		acceptor.setDefaultLocalAddress(new InetSocketAddress(port));

//		acceptor.getFilterChain().addLast("logging", new LoggingFilter());
        acceptor.getFilterChain().addLast("thread", new ExecutorFilter(100, 150));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TFrameProtocolCodecFactory()));

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
