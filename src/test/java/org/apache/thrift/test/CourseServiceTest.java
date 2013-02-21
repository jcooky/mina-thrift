package org.apache.thrift.test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.apache.thrift.test.gen.CourseService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jcooky.mina.thrift.test.rule.ThriftServerTestRule;

public class CourseServiceTest {
	private @Mock
	CourseService.Iface mockCourseService;

	public ThriftServerTestRule thriftServerTestRule;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(mockCourseService.getCourseInventory()).thenReturn(Collections.EMPTY_LIST);

		thriftServerTestRule = new ThriftServerTestRule(new CourseService.Processor(mockCourseService));
		thriftServerTestRule.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		thriftServerTestRule.tearDown();
	}

	@Test
	public void testThriftServer() throws Exception {
		String course = "Windows_301";

		// Setup the transport and protocol
		final CourseService.Client client = new CourseService.Client(this.thriftServerTestRule.getClientProtocol());

		// All hooked up, start using the service
		List<String> classInv = client.getCourseInventory();
		// System.out.println("Received " + classInv.size() + " class(es).");
		assertSame(classInv.size(), 0);

		client.deleteCourse(course);

		verify(mockCourseService).deleteCourse(course);
	}
}
