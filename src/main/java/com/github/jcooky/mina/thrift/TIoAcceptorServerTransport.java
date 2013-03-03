package com.github.jcooky.mina.thrift;

import java.io.IOException;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import com.github.jcooky.mina.thrift.codec.TFrameProtocolCodecFactory;

public class TIoAcceptorServerTransport extends TServerTransport {
	public final static String CODEC_NAME = "mina-thrift-codec";
	private IoAcceptor acceptor;

	public TIoAcceptorServerTransport(IoAcceptor acceptor) {
		if (!acceptor.getFilterChain().contains(CODEC_NAME))
			acceptor.getFilterChain().addLast(CODEC_NAME, new ProtocolCodecFilter(new TFrameProtocolCodecFactory()));
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
		} catch (IOException e) {
			throw new TTransportException(e);
		}
	}

	public void close() {
		acceptor.dispose(false);
	}

	protected TIoSessionTransport acceptImpl() throws TTransportException {
		throw new UnsupportedOperationException();
	}

}
