
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FriendFinder {

	protected ClassesDataSource classesDataSource;
	protected StudentsDataSource studentsDataSource;

	public FriendFinder(ClassesDataSource cds, StudentsDataSource sds) {
		classesDataSource = cds;
		studentsDataSource = sds;
	}

	/*
	 * This method takes a String representing the name of a student
	 * and then returns a set containing the names of everyone else
	 * who is taking the same classes as that student.
	 */
	public Set<String> findClassmates(Student theStudent) {

		if (classesDataSource == null || studentsDataSource == null) {
			throw new IllegalStateException("null dependency detected");
		}

		if(theStudent == null) {
			throw new IllegalArgumentException("bad input: student does not exist");
		}

		String name = theStudent.getName();

		if (name == null) {
			throw new IllegalArgumentException("bad input: selected student does not contain name information");
		}

		// find the classes that this student is taking
		List<String> myClasses = classesDataSource.getClasses(name);

		if (myClasses == null) {
			return Collections.emptySet(); // return empty Set if the student isn't taking any classes
		}

		// use the classes to find the names of the students
		Set<String> classmates = new HashSet<String>();

		//for each class that I am taking...
		for (String myClass : myClasses) {

			if (myClass == null) {
				break;
			}

			// list all the students in the class
			List<Student> students = studentsDataSource.getStudents(myClass);

			if (students == null) {
				break; // ignore this class if it does not contain a valid set of students
			}

			//for each student in one of my classes...
			for (Student otherStudent : students) {

				//check that they are properly recorded
				if (otherStudent == null || otherStudent.getName() == null) {
					break;	//if not, ignore them
				}

				// find the other classes that they're taking
				List<String> theirClasses = classesDataSource.getClasses(otherStudent.getName());

				//check that we know what else they are taking
				if (theirClasses != null) {

					// see if all of the classes that they're taking are the same as the ones this student is taking
					boolean allSame = true;

					//now check their class list against mine
					for (String c : myClasses) {
						if (theirClasses.contains(c) == false) {    //ignore them if they are not in my class at any stage (nb ok for them to take additional classes)
							allSame = false;
							break;
						}
					}

					// if they're taking all the same classes, then add to the set of classmates
					if (allSame) {
						if (otherStudent.getName().equals(name) == false) {        //check that I am not looking at myself
							classmates.add(otherStudent.getName());
						}
					}
				}
			}

		}

		return classmates;
	}


}
