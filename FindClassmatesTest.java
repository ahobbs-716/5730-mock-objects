import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


/*
 * This class includes test cases for the basic/normal functionality of the 
 * FriendFinder.findClassmates method, but does not check for any error handling.
 */

public class FindClassmatesTest {
	
	protected FriendFinder ff;
		
	protected ClassesDataSource defaultClassesDataSource = new ClassesDataSource() {

		@Override
		public List<String> getClasses(String studentName) {

			if (studentName.equals("A")) {
				return List.of("1", "2", "3");
			}
			else if (studentName.equals("B")) {
				return List.of("1", "2", "3");
			}
			else if (studentName.equals("C")) {
				return List.of("2", "4");
			}
			else return null;			
		
		}
		
	};
	
	protected StudentsDataSource defaultStudentsDataSource = new StudentsDataSource() {

		@Override
		public List<Student> getStudents(String className) {
			
			Student a = new Student("A", 101);
			Student b = new Student("B", 102);
			Student c = new Student("C", 103);

			if (className.equals("1")) {
				return List.of(a, b);
			}
			else if (className.equals("2")) {
				return List.of(a, b, c);
			}
			else if (className.equals("3")) {
				return List.of(a, b);
			}
			else if (className.equals("4")) {
				return List.of(c);
			}
			else return null;
		}
		
	};


	//tests for basic functionality
	@Test
	public void testFindOneFriend() { 
		
		ff = new FriendFinder(defaultClassesDataSource, defaultStudentsDataSource);
		Set<String> response = ff.findClassmates(new Student("A", 101));
		assertNotNull(response);
		assertEquals(1, response.size());
		assertTrue(response.contains("B"));

	}
	@Test
	public void testFindNoFriends() { 
		
		ff = new FriendFinder(defaultClassesDataSource, defaultStudentsDataSource);
		Set<String> response = ff.findClassmates(new Student("C", 103));
		assertNotNull(response);
		assertTrue(response.isEmpty());

	}


	//test inputs to the method
	@Test (expected = IllegalArgumentException.class)
	public void testInputStudentIsNull() {

		ff = new FriendFinder(defaultClassesDataSource, defaultStudentsDataSource);
		ff.findClassmates(null);

	}
	@Test (expected = IllegalArgumentException.class)
	public void testInputStudentHasNoName() {

		ff = new FriendFinder(defaultClassesDataSource, defaultStudentsDataSource);
		Set<String> response = ff.findClassmates(new Student(null, 101));

	}


	//test objects upon which the method depends
	@Test (expected = IllegalStateException.class)
	public void testNoStudentDataSourceObject() {

		ff = new FriendFinder(defaultClassesDataSource, null);
		Set<String> response = ff.findClassmates(new Student("A", 101));

	}
	@Test (expected = IllegalStateException.class)
	public void testNoClassesDataSourceObject() {

		ff = new FriendFinder(null, defaultStudentsDataSource);
		assertEquals(Collections.EMPTY_SET, ff.findClassmates(new Student("test student", 1032)));

	}


	//test faulty objects are returned from invoked methods
	@Test
	public void testClassesDataSourceReturnsNullForInputStudent() {

		ff = new FriendFinder(defaultClassesDataSource, defaultStudentsDataSource);
		Set<String> response = ff.findClassmates(new Student("D", 104));
		assertNotNull(response);
		assertTrue(response.isEmpty());

	}
	@Test
	public void testMyClassHasNullStudents() {

		StudentsDataSource faultyStudentsReturn = new StudentsDataSource() {

			@Override
			public List<Student> getStudents(String className) {

				Student a = new Student("A", 101);
				Student b = new Student("B", 102);
				Student c = new Student("C", 103);

				if (className.equals("1")) {
					return null;
				}
				else if (className.equals("2")) {
					return List.of(a, b, c);
				}
				else if (className.equals("3")) {
					return List.of(a, b);
				}
				else if (className.equals("4")) {
					return List.of(c);
				}
				else return null;
			}

		};

		ff = new FriendFinder(defaultClassesDataSource, faultyStudentsReturn);
		Set<String> response = ff.findClassmates(new Student("A", 101));

		//ignores faulty input
		//therefore returns null (as only 1 class, which is faulty)
		assertNotNull(response);
		assertTrue(response.isEmpty());

	}
	@Test
	public void testMyColleagueHasNullName() {

		StudentsDataSource unnamedStudents = new StudentsDataSource() {
			@Override
			public List<Student> getStudents(String className) {

				List<Student> students = new ArrayList<>();
				students.add(new Student(null, 2042));

				return students;
			}
		};
		ff = new FriendFinder(defaultClassesDataSource, unnamedStudents);
		Set<String> response = ff.findClassmates(new Student("A", 101));

		//runs method ignoring faulty input
		assertNotNull(response);
		assertEquals(0, response.size());
	}
	@Test
	public void testMyColleagueHasNullClasses() {

		ClassesDataSource nullClasses = new ClassesDataSource() {

			@Override
			public List<String> getClasses(String studentName) {

				if (studentName.equals("A")) {
					return List.of("1", "2", "3");
				}
				else if (studentName.equals("B")) {
					return null;
				}
				else if (studentName.equals("C")) {
					return List.of("1", "2", "3");
				}
				else return null;

			}

		};
		ff = new FriendFinder(nullClasses, defaultStudentsDataSource);
		Set<String> response = ff.findClassmates(new Student("A", 101));

		//runs method ignoring faulty input
		assertNotNull(response);
		assertEquals(1, response.size());
		assertTrue(response.contains("C"));

	}
	@Test
	public void testMyClassesContainNullListing() {

		ClassesDataSource nullClasses = new ClassesDataSource() {
			@Override
			public List<String> getClasses(String studentName) {

				List<String> list = new ArrayList<>();
				list.add(null);
				list.add(null);

				return list;
			}
		};

		ff = new FriendFinder(nullClasses, defaultStudentsDataSource);
		Set<String> response = ff.findClassmates(new Student("A", 101));
		assertNotNull(response);
		assertTrue(response.isEmpty());

	};
	@Test
	public void testStudentListContainsNulls() {

		StudentsDataSource studentsWithNulls = new StudentsDataSource() {
			@Override
			public List<Student> getStudents(String className) {

				List<Student> students = new ArrayList<>();
				students.add(null);
				students.add(null);

				return students;
			}
		};
		ff = new FriendFinder(defaultClassesDataSource, studentsWithNulls);
		Set<String> response = ff.findClassmates(new Student("A", 101));

		assertNotNull(response);
		assertTrue(response.isEmpty());

	}

}
