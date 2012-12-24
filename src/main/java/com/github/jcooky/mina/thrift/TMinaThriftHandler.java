package com.github.jcooky.mina.thrift;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

public class TMinaThriftHandler extends IoHandlerAdapter {
	private TProcessorFactory processorFactory;
	private TTransportFactory inputTransportFactory, outputTransportFactory;
	private TProtocolFactory inputProtocolFactory, outputProtocolFactory;
	
	public TMinaThriftHandler(TProcessorFactory processorFactory,
			TTransportFactory inputTransportFactory, TTransportFactory outputTransportFactory,
			TProtocolFactory inputProtocolFactory, TProtocolFactory outputProtocolFactory) {
		super();
		this.processorFactory = processorFactory;
		this.inputTransportFactory = inputTransportFactory;
		this.outputTransportFactory = outputTransportFactory;
		this.inputProtocolFactory = inputProtocolFactory;
		this.outputProtocolFactory = outputProtocolFactory;
	}

	public void sessionCreated(IoSession session) throws Exception {

	}

	public void sessionOpened(IoSession session) throws Exception {
		TIoSessionTransport trans = new TIoSessionTransport(session);

		session.setAttribute(Constants.TRANSPORT, trans);
		session.setAttribute(Constants.BUFFER, null);
	}

	public void sessionClosed(IoSession session) throws Exception {
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	}

	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		TIoSessionTransport transport = (TIoSessionTransport) session
				.getAttribute(Constants.TRANSPORT);

		session.setAttribute(Constants.BUFFER, (IoBuffer) message);

		TProcessor processor = this.processorFactory.getProcessor(transport);
		if (processor != null) {
			TTransport inputTransport = inputTransportFactory.getTransport(transport);
			TTransport outputTransport = outputTransportFactory.getTransport(transport);
			processor.process(inputProtocolFactory.getProtocol(inputTransport),
					outputProtocolFactory.getProtocol(outputTransport));
		} else {
			throw new TTransportException("processor is null");
		}
	}

	public void messageSent(IoSession session, Object message) throws Exception {
	}
}