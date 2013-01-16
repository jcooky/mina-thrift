package com.github.jcooky.mina.thrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.test.gen.CourseService;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TMinaServerTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private int PORT = 9091;
	private int SOCKET_TIMEOUT = 10 * 1000;
	private @Mock CourseService.Iface mockCourseService;
	private TServer server;
	private TServerTransport socket ;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(mockCourseService.getCourseInventory()).thenReturn(Collections.EMPTY_LIST);
		when(mockCourseService.getCourse(any(String.class))).thenThrow(new TApplicationException("Test"));
		
		socket = new TIoAcceptorServerTransport(PORT);
		CourseService.Processor<?> processor = new CourseService.Processor<CourseService.Iface>(mockCourseService);
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
	}
	
	@After
	public void tearDown() throws Exception {
		server.stop();
		socket.close();
	}
	
	@Test
	public void testThriftServer() throws Exception {
		String course = "Windows_301";
		
		//Setup the transport and protocol
		final TSocket socket = new TSocket("localhost", PORT, SOCKET_TIMEOUT);
		final TProtocol protocol = new TBinaryProtocol(socket);
		final CourseService.Client client = new CourseService.Client(protocol);

		//The transport must be opened before you can begin using
		socket.open();

		try {
			//All hooked up, start using the service
			List<String> classInv = client.getCourseInventory();
//			System.out.println("Received " + classInv.size() + " class(es).");
			assertSame(classInv.size(), 0);
	
			client.deleteCourse(course);
		} finally {
			socket.close();
		}
		
		verify(mockCourseService).deleteCourse(course);
	}
	
	@Test(expected=TApplicationException.class)
	public void testThrowable() throws Exception {
		String course = "Windows_301";
		
		//Setup the transport and protocol
		final TSocket socket = new TSocket("localhost", PORT, SOCKET_TIMEOUT);
		final TProtocol protocol = new TBinaryProtocol(socket);
		final CourseService.Client client = new CourseService.Client(protocol);
	
		//The transport must be opened before you can begin using
		socket.open();
	
		try {
			client.getCourse(course);
		} finally {
			socket.close();
		}
	}
}
