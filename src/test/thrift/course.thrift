namespace java org.apache.thrift.test.gen
namespace py com.ociweb.jnb.thrift

senum PhoneType {
  "HOME",
  "WORK",
  "MOBILE"
  "OTHER"
}

struct Phone {
  1: i32    id,
  2: string number,
  3: PhoneType type
}

struct Person {
  1: i32    id,
  2: string firstName,
  3: string lastName,
  4: string email,
  5: list<Phone> phones
}

struct Course {
  1: i32    id,
  2: string number,
  3: string name,
  4: Person instructor,
  5: string roomNumber,
  6: list<Person> students
}

exception CourseNotFound {
  1: string message
}

exception UnacceptableCourse {
  1: string message
}

service CourseService {
  list<string> getCourseInventory(),
  Course getCourse(1:string courseNumber) throws (1: CourseNotFound cnf),
  void addCourse(1:Course course) throws (1: UnacceptableCourse uc)
  void deleteCourse(1:string courseNumber) throws (1: CourseNotFound cnf)
}