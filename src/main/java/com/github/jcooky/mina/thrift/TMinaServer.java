package com.github.jcooky.mina.thrift;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class TMinaServer extends TServer {
	public static final AttributeKey TRANSPORT = new AttributeKey(TMinaServer.class, "transport");
	public static final AttributeKey MESSAGE = new AttributeKey(TMinaServer.class, "buffer");
	
	public static class Args extends AbstractServerArgs<Args> {
		int readBufferSize = 2048;
		int writeTimout = 60;
		int writeIdleTime;
		int readIdleTime;
		int maxReadBufferSize = 65536;
		int minReadBufferSize = 64;
		int throughputCalculationInterval = 3;
		boolean useReadOperation;

		public Args(TServerTransport arg0) {
			super(arg0);
		}

		public Args minReadBufferSize(int minReadBufferSize) {
			this.minReadBufferSize = minReadBufferSize;
			return this;
		}

		public Args maxReadBufferSize(int maxReadBufferSize) {
			this.maxReadBufferSize = maxReadBufferSize;
			return this;
		}

		public Args readBufferSize(int readBufferSize) {
			this.readBufferSize = readBufferSize;
			return this;
		}

		public Args writeTimout(int writeTimout) {
			this.writeTimout = writeTimout;
			return this;
		}

		public Args writeIdleTime(int writeIdleTime) {
			this.writeIdleTime = writeIdleTime;
			return this;
		}

		public Args readIdleTime(int readIdleTime) {
			this.readIdleTime = readIdleTime;
			return this;
		}

		public Args bothIdleTime(int bothIdleTime) {
			this.readIdleTime = bothIdleTime;
			this.writeIdleTime = bothIdleTime;
			return this;
		}

		public Args throughputCalculationInterval(int throughputCalculationInterval) {
			this.throughputCalculationInterval = throughputCalculationInterval;
			return this;
		}

		public Args useReadOperation(boolean useReadOperation) {
			this.useReadOperation = useReadOperation;
			return this;
		}
	}

	private TMinaThriftHandler handler;

	public TMinaServer(Args args) {
		super(args);

		IoSessionConfig config = getTransport().getAcceptor().getSessionConfig();
		config.setMinReadBufferSize(args.minReadBufferSize);
		config.setMaxReadBufferSize(args.maxReadBufferSize);
		config.setReadBufferSize(args.readBufferSize);
		config.setWriterIdleTime(args.readIdleTime);
		config.setReaderIdleTime(args.writeIdleTime);
		config.setWriteTimeout(args.writeTimout);
		config.setThroughputCalculationInterval(args.throughputCalculationInterval);
		config.setUseReadOperation(args.useReadOperation);

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
	}
}
