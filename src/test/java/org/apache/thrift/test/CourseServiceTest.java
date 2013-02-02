package org.apache.thrift.test;

import com.github.jcooky.mina.thrift.test.rule.ThriftServerTestRule;
import org.apache.thrift.test.gen.CourseService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CourseServiceTest {
    private CourseService.Iface mockCourseService = mock(CourseService.Iface.class);

    @Rule
    public ThriftServerTestRule thriftServerTestRule = new ThriftServerTestRule(new CourseService.Processor(mockCourseService));
	
	@Before
	public void setUp() throws Exception {
		
		when(mockCourseService.getCourseInventory()).thenReturn(Collections.EMPTY_LIST);
	}
	
	@Test
	public void testThriftServer() throws Exception {
		String course = "Windows_301";
		
		//Setup the transport and protocol
		final CourseService.Client client = new CourseService.Client(this.thriftServerTestRule.getClientProtocol());

        //All hooked up, start using the service
        List<String> classInv = client.getCourseInventory();
//			System.out.println("Received " + classInv.size() + " class(es).");
        assertSame(classInv.size(), 0);

        client.deleteCourse(course);

		verify(mockCourseService).deleteCourse(course);
	}
}
