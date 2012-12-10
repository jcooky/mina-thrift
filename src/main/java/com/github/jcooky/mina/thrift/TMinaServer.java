package com.github.jcooky.mina.thrift;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class TMinaServer extends TServer implements IoHandler {
	public static class Args extends AbstractServerArgs<Args> {
		int readBufferSize = 2048, writeTimout = 10 * 1000, idleTime = 10;
		
		public Args(TServerTransport arg0) {
			super(arg0);
		}
		
		public Args readBufferSize(int readBufferSize) {
			this.readBufferSize = readBufferSize;
			return this;
		}
		
		public Args writeTimout(int writeTimout) {
			this.writeTimout = writeTimout;
			return this;
		}
		
		public Args idleTime(int idleTime) {
			this.idleTime = idleTime;
			return this;
		}
	}
	
	protected TMinaServer(Args args) {
		super(args);
		
		IoSessionConfig config = getTransport().getAcceptor().getSessionConfig();
		config.setReadBufferSize(args.readBufferSize);
		config.setBothIdleTime(args.idleTime);
		config.setWriteTimeout(args.writeTimout);
	}

	public void serve() {
		try {
			getTransport().setHandler(this);
			getTransport().listen();
		} catch (TTransportException e) {
			throw new RuntimeException(e);
		}
	}
	
	private TIoAcceptorServerTransport getTransport() {
		return (TIoAcceptorServerTransport)super.serverTransport_;
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

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		TIoSessionTransport transport = (TIoSessionTransport)session.getAttribute(Constants.TRANSPORT);
		
		session.setAttribute(Constants.BUFFER, (IoBuffer)message);
		
		TProcessor processor = super.processorFactory_.getProcessor(transport);
		if (processor != null) {
			TTransport inputTransport = super.inputTransportFactory_.getTransport(transport);
			TTransport outputTransport = super.outputTransportFactory_.getTransport(transport);
			processor.process(super.inputProtocolFactory_.getProtocol(inputTransport), super.outputProtocolFactory_.getProtocol(outputTransport));
		} else {
			throw new TTransportException("processor is null");
		}
	}

	public void messageSent(IoSession session, Object message) throws Exception {
	}

}