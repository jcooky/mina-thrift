package com.github.jcooky.mina.thrift;

import com.github.jcooky.mina.thrift.test.rule.TMinaServerTestRule;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.test.gen.CourseService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CourseServiceTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private CourseService.Iface mockCourseService = mock(CourseService.Iface.class);
    public TMinaServerTestRule serverTestRule = new TMinaServerTestRule(new CourseService.Processor<CourseService.Iface>(mockCourseService));

	@Before
	public void setUp() throws Exception {
        logger.info("test log");

		when(mockCourseService.getCourseInventory()).thenReturn(Collections.EMPTY_LIST);
		when(mockCourseService.getCourse(any(String.class))).thenThrow(new TApplicationException("Test"));

	}
	
	@Test
	public void testThriftServer() throws Exception {
		String course = "Windows_301";
		
		final CourseService.Client client = new CourseService.Client(serverTestRule.getClientProtocol());

        //All hooked up, start using the service
        List<String> classInv = client.getCourseInventory();
//			System.out.println("Received " + classInv.size() + " class(es).");
        assertSame(classInv.size(), 0);

        client.deleteCourse(course);

		verify(mockCourseService).deleteCourse(course);
	}
	
	@Test(expected=TApplicationException.class)
	public void testThrowable() throws Exception {
		String course = "Windows_301";
		
		final CourseService.Client client = new CourseService.Client(serverTestRule.getClientProtocol());
        client.getCourse(course);
	}
}
