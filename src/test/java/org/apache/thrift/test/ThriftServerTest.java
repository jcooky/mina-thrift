package org.apache.thrift.test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.THsHaServer.Args;
import org.apache.thrift.server.TServer;
import org.apache.thrift.test.gen.CourseService;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ThriftServerTest {
	private int PORT = 9090;
	private int SOCKET_TIMEOUT = 1000;
	private @Mock CourseService.Iface mockCourseService;
	private TServer server;
	private TNonblockingServerSocket socket ;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(mockCourseService.getCourseInventory()).thenReturn(Collections.EMPTY_LIST);
		
		socket = new TNonblockingServerSocket(PORT);
		final CourseService.Processor<?> processor = new CourseService.Processor<CourseService.Iface>(mockCourseService);
		server = new THsHaServer(new Args(socket)
												.processor(processor)
												.protocolFactory(new TCompactProtocol.Factory())
												.transportFactory(new TFramedTransport.Factory()));

		new Thread() {
			public void run() {
				server.serve();
			}
		}.start();
	}
	
	@After
	public void tearDown() {
		server.stop();
		socket.close();
	}
	
	@Test
	public void testThriftServer() throws Exception {
		String course = "Windows_301";
		
		//Setup the transport and protocol
		final TSocket socket = new TSocket("127.0.0.1", PORT, SOCKET_TIMEOUT);
		final TTransport transport = new TFramedTransport(socket);
		final TProtocol protocol = new TCompactProtocol(transport);
		final CourseService.Client client = new CourseService.Client(protocol);

		//The transport must be opened before you can begin using
		transport.open();

		try {
			//All hooked up, start using the service
			List<String> classInv = client.getCourseInventory();
//			System.out.println("Received " + classInv.size() + " class(es).");
			assertSame(classInv.size(), 0);
	
			client.deleteCourse(course);
		} finally {
			transport.close();
		}
		
		verify(mockCourseService).deleteCourse(course);
	}
}
