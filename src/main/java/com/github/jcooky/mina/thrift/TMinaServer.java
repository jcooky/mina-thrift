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

public class TMinaServer extends TServer {
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

	private TMinaThriftHandler handler;

	protected TMinaServer(Args args) {
		super(args);

		IoSessionConfig config = getTransport().getAcceptor().getSessionConfig();
		config.setReadBufferSize(args.readBufferSize);
		config.setBothIdleTime(args.idleTime);
		config.setWriteTimeout(args.writeTimout);

		handler = new TMinaThriftHandler(super.processorFactory_, super.inputTransportFactory_,
				super.outputTransportFactory_, super.inputProtocolFactory_,
				super.outputProtocolFactory_);
	}

	public void serve() {
		try {
			getTransport().setHandler(this.handler);
			getTransport().listen();
		} catch (TTransportException e) {
			throw new RuntimeException(e);
		}
	}

	private TIoAcceptorServerTransport getTransport() {
		return (TIoAcceptorServerTransport) super.serverTransport_;
	}

	@Override
	public void stop() {
		super.stop();
		getTransport().close();
	}

}
