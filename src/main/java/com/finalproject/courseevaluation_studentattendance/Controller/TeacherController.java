package com.finalproject.courseevaluation_studentattendance.Controller;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.finalproject.courseevaluation_studentattendance.Model.*;
import com.finalproject.courseevaluation_studentattendance.Repositories.*;
import com.finalproject.courseevaluation_studentattendance.Services.CourseService;
import com.finalproject.courseevaluation_studentattendance.Services.EvaluationService;
import com.finalproject.courseevaluation_studentattendance.Services.PersonService;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.EmailAttachment;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmailAttachment;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import com.finalproject.courseevaluation_studentattendance.Model.Course;
import com.finalproject.courseevaluation_studentattendance.Model.Person;
import com.finalproject.courseevaluation_studentattendance.Repositories.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.logging.SimpleFormatter;


import javax.websocket.server.PathParam;

@Controller
@RequestMapping("/teacher")
public class TeacherController {


    @Autowired
    PersonRepository personRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    PersonRepository personRepo;

    @Autowired
    PersonService personService;

    @Autowired
    CourseService courseService;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    EvaluationService evaluationService;

    @RequestMapping("/home")
//    public String teacherHome(Principal p, Model model)
//    {
//        model.addAttribute("instructor", personRepository.findByUsername(p.getName()));
//        return "teacherpages/teacherhome";
//    }

    //just for testing until security/login option is added
    public String teacherHometest(Model model)
    {
        model.addAttribute("instructor", personRepository.findByUsername("teacher"));
        return "teacherpages/teacherhome";
    }

    //this route can be combine with the teacherhome page later
    @GetMapping("/listallcourses")
//    public String listCourse(Principal p, Model model)
    public String listCourse(Model model)
    {
        Person instructor = personRepository.findByUsername("teacher");

        Iterable<Course> allCoursesofAInstructor = instructor.getCourseInstructor();

        model.addAttribute("allcoursesofaInstructor", allCoursesofAInstructor);

        return "teacherpages/listallcourses";
    }


    //list course info and all students/mark attendance
    @GetMapping("/detailsofacourse/{id}")
    public String detailsofcourse(@PathVariable("id") Long courseId, Model model)
    {

        Course currentCourse = courseRepository.findOne(courseId);
        Iterable<Person> studentsofACourse = currentCourse.getStudent();
        model.addAttribute("courseInstructor", currentCourse.getInstructor());


        //move it to new route so it can stamp the time of the time actually submitted
        //add a new attendance and set date (for a course)
//       Date now= new Date();

//        Attendance oneAttendancecourse = new Attendance();
//        Attendance oneAttendance = new Attendance();
//
//        oneAttendancecourse.setDate(now);
//        oneAttendance.setDate(now);

//        currentCourse.addAttendance(oneAttendancecourse);
//
//                for (Person student : studentsofACourse) {
//            student.addAttendance(oneAttendance);
//        }


        model.addAttribute("course", currentCourse);
        model.addAttribute("studentsofACourse", studentsofACourse);

        return "teacherpages/detailsofacourse";
    }

    @GetMapping("/markattendance/{courseId}")
    public String listAttendanceofaCourse(@PathVariable("courseId") Long courseId, Model model)
    {

        Course currentCourse = courseRepository.findOne(courseId);
        Iterable<Person> studentsofACourse = currentCourse.getStudent();

        Date now= new Date();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        String nowdate= df.format(now);

        model.addAttribute("nowdate", nowdate);
        model.addAttribute("course", currentCourse);
        model.addAttribute("studentsofACourse", studentsofACourse);

        return "teacherpages/attendanceofacourseform";
    }

    //display the attendance for a course of all students



    @PostMapping("/markattendancepo/{courseId}")
    public String postattendance(@PathVariable("courseId") Long courseId, @RequestParam("attdate") String attdate,
                                 @RequestParam(value = "attendanceStatus") String[] attendanceStatus,Model model)
    {

        Course currentCourse = courseRepository.findOne(courseId);
        Iterable<Person> studentsofACourse = currentCourse.getStudent();

        int i=0;

//        Date now= new Date();
//
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//
//        String nowdate= df.format(now);
//
//        System.out.println(nowdate);
//
        for (Person student: studentsofACourse) {

            if (attendanceRepository.findAllByAttendanceCourseEqualsAndDateEqualsAndPersonAttendanceEquals(currentCourse, attdate, student) != null) {
                Attendance attdel = attendanceRepository.findAllByAttendanceCourseEqualsAndDateEqualsAndPersonAttendanceEquals(currentCourse, attdate, student);
                System.out.println(attdel.toString());
                student.removeAttendance(attdel);
                System.out.println("here1111111");
                attendanceRepository.delete(attdel);
//                System.out.println("here2222211");
            }

                Attendance attnew = new Attendance();
                attnew.setDate(attdate);
                System.out.println("printing status" + attendanceStatus[i]);
                attnew.setStatus(attendanceStatus[i]);
                System.out.println("set stautus doone----");
                i += 1;
                attnew.setPersonAttendance(student);
                student.addAttendance(attnew);
                System.out.println("!!!!!!-----add att to student");
                attnew.setAttendanceCourse(currentCourse);
                attendanceRepository.save(attnew);

                System.out.println("newset-------");

                // problem is here is empty
                System.out.println("!!!!!!!!"+student.getAttendances().toString());

        }


        System.out.println("end loop------");

        model.addAttribute("attdate", attdate);
        model.addAttribute("course", currentCourse);
        model.addAttribute("studentsofACourse", studentsofACourse);


        return "teacherpages/displyattforstudentsofacourse";
    }


    //for testing: display attendance of in a table rather than list
    @GetMapping("/attforonecourse/{id}")
    public String attforonecourse(@PathVariable("id") Long courseId, Model model)
    {

        Course currentCourse = courseRepository.findOne(courseId);
        Iterable<Person> studentsofACourse = currentCourse.getStudent();

        Person onestu = studentsofACourse.iterator().next();

        model.addAttribute("onestu", onestu);
        model.addAttribute("course", currentCourse);
        model.addAttribute("studentofacourse", studentsofACourse);

        return "teacherpages/tableattofonecourse";
    }


    //for delete or update M number for the student
    @GetMapping("/listallstudents/{courseId}")
    public String updateMnumber(@PathVariable("courseId") Long courseId, Model model) {


        Course currentCourse = courseRepository.findOne(courseId);
        Iterable<Person> studentsofACourse = currentCourse.getStudent();
        ArrayList<Person> unvalidatedstudent= new ArrayList<>();
        ArrayList<Person> validatedstudent= new ArrayList<>();


        //for student that M number is null put them in a unvalidated list
        for (Person student:studentsofACourse)
        {
            if(student.getmNumber().isEmpty())
            {
                unvalidatedstudent.add(student);
            }

            if(!student.getmNumber().isEmpty())
            {
                System.out.println("not null====" + student.getmNumber().toString());
                validatedstudent.add(student);
            }

        }

        //add an empty search student results
//        ArrayList<Person> searchStudent= new ArrayList<>();
//
//        model.addAttribute("searchstudent", searchStudent);

        model.addAttribute("course", currentCourse);
        model.addAttribute("unvalidatedstudent", unvalidatedstudent);
        model.addAttribute("validatedstudent", validatedstudent);

        return "teacherpages/liststudentsofacourse";

    }


    @GetMapping("/update/{courseId}/{studentId}")
    public String updateMnumber(@PathVariable("courseId") Long courseId, @PathVariable("studentId") Long studentId, Model model) {

        Course currentCourse = courseRepository.findOne(courseId);
        Person currentStudent= personRepository.findOne(studentId);
        model.addAttribute("student", currentStudent);
        model.addAttribute("course", currentCourse);

        return "teacherpages/updateMform";
    }


    @PostMapping("/update/{courseId}/{studentId}")
    public String updateMnumberordeletestudent(@PathVariable("courseId") Long courseId,
                                               @PathVariable("studentId") Long studentId,
                                               @RequestParam(value="newMId") String newMId,
                                               Model model) {

        Course currentCourse = courseRepository.findOne(courseId);
        Person currentStudent= personRepository.findOne(studentId);
        currentStudent.setmNumber(newMId);
        personRepository.save(currentStudent);
        model.addAttribute("course", currentCourse);

        return "redirect:/teacher/listallstudents/{courseId}";
    }


//    @GetMapping("/displayoneeval/{id}")
//    public String displayoneEval(@PathVariable("id") long evalId, Model model) {
//
//
//        model.addAttribute("neweval", evaluationRepository.findOne(evalId));
//
//        return "teacherpages/displayoneeval";
//    }


    @GetMapping("/displayallevalsofonecourse/{courseId}")
    public String displayallEvalofonecourse(@PathVariable("courseId") long courseId, Model model) {

        Course currentCourse = courseRepository.findOne(courseId);

        Iterable<Evaluation> allevalofonecourse= currentCourse.getEvaluations();

        model.addAttribute("neweval", allevalofonecourse);

        return "teacherpages/displayallevalofacourse";
    }


    @RequestMapping("/delete/{courseId}/{studentId}")
    public String deletestudentwithnoMnumber(@PathVariable("courseId") Long courseId,@PathVariable("studentId") Long studentId, Model model) {

        Course currentCourse = courseRepository.findOne(courseId);
        Person currentStudent= personRepository.findOne(studentId);
        currentCourse.removeStudent(currentStudent);
        personRepository.delete(currentStudent);

        model.addAttribute("course", currentCourse);

        return "redirect:/teacher/listallstudents/{courseId}";
    }


    @RequestMapping("/searchstudent/{courseId}")
    public String findstudents(@PathVariable("courseId") Long courseId, @RequestParam(value = "searchBy") String searchBy, @RequestParam(value ="fname", required=false) String fname,
                    @RequestParam(value ="lname", required=false) String lname, @RequestParam(value ="email", required=false) String email,
                    Model model)
    {


        Course currentCourse = courseRepository.findOne(courseId);
        Iterable<Person> studentsofACourse = currentCourse.getStudent();
        ArrayList<Person> unvalidatedstudent= new ArrayList<>();
        ArrayList<Person> validatedstudent= new ArrayList<>();


        //tried iframe and some other things to display(redirect) search result on the same page with student info without creating a new HTML, it didn't work,
        // so now we are just creating a new HTML of the search result and put student info together within the result page, need to find a better solution later!!!!
        for (Person student:studentsofACourse)
        {
            if(student.getmNumber().isEmpty())
            {
                unvalidatedstudent.add(student);
            }

            if(!student.getmNumber().isEmpty())
            {
                System.out.println("not null====" + student.getmNumber().toString());
                validatedstudent.add(student);
            }

        }

        //add an empty search student results
//        ArrayList<Person> searchStudent= new ArrayList<>();
//
//        model.addAttribute("searchstudent", searchStudent);

        model.addAttribute("course", currentCourse);
        model.addAttribute("unvalidatedstudent", unvalidatedstudent);
        model.addAttribute("validatedstudent", validatedstudent);

        if (searchBy.equalsIgnoreCase("all"))
        {
            System.out.println("!!!!!!!!");
            Iterable<Person> searchstudent = personRepository.findAllByFirstNameLikeAndLastNameLikeAndEmailLike(fname,lname,email);
            model.addAttribute("searchstudent", searchstudent );
            System.out.println("added to model !!");
            return "teacherpages/studentsearchresult";
//            return "redirect:/teacher/listallstudents/{courseId}";
        }

        if (searchBy.equalsIgnoreCase("first"))
        {
            model.addAttribute("searchstudent", personRepository.findAllByFirstNameLike(fname) );
//            return "redirect:/teacher/listallstudents/{courseId}";
            return "teacherpages/studentsearchresult";
        }


        if (searchBy.equalsIgnoreCase("last"))
        {
            model.addAttribute("searchstudent", personRepository.findAllByLastNameLike(lname) );
//            return "redirect:/teacher/listallstudents/{courseId}";
            return "teacherpages/studentsearchresult";
        }

        if (searchBy.equalsIgnoreCase("email"))
        {
            model.addAttribute("searchstudent", personRepository.findAllByEmailLike(email) );
//            return "redirect:/teacher/listallstudents/{courseId}";
            return "teacherpages/studentsearchresult";
        }

        if (searchBy.equalsIgnoreCase("fandl"))
        {
            model.addAttribute("searchstudent", personRepository.findAllByFirstNameLikeAndLastNameLike(fname, lname) );
//            return "redirect:/teacher/listallstudents/{courseId}";
            return "teacherpages/studentsearchresult";
        }

        else {

            model.addAttribute("searchstudent", new ArrayList<Person>());
            model.addAttribute("message", "Error with the search, try again!");
//            return "redirect:/teacher/listallstudents/{courseId}";
            return "teacherpages/studentsearchresult";
        }
    }

   @GetMapping("/addstudentstocourse/{id}")
   public String getCourse(@PathVariable("id")Long id, Model model)
   {

       Date now= new Date();

       Person student = new Person();

       student.setStartDate(now);

       System.out.println(student.getStartDate());

       model.addAttribute("course", courseRepository.findOne(id));

       model.addAttribute("newstudent", student);

       return "teacherpages/addstudent";
   }


   @PostMapping("/addstudent/{id}")
    public String postCourse(@Valid @PathVariable("id") Long id, @ModelAttribute("newstudent") Person student, BindingResult bindingResult,Model model)
   {
        if(bindingResult.hasErrors())
        {
            return "teacherpages/addstudent";
        }
       Course c =  courseRepository.findOne(id);
       student.setCourseStudent(c);
       personRepository.save(student);
//       personService.addStudentToCourse(student,c);
       model.addAttribute("course", c);
       model.addAttribute("newstudent", student);
//       personService.create(student);
//       personRepo.save(person);
       return "teacherpages/confirmstudent";
   }

    //the method to send email
    //it sends email need to make the body

    @GetMapping("/courseend/{id}")
    public String emailAtCourseEnd(@PathVariable("id") long id, Model model) throws UnsupportedEncodingException {
        Course course=courseRepository.findOne(id);
        Date date= new Date();
//        DateFormat df=new SimpleDateFormat("MM/dd/yyyy");
        //sets the course end date with the current date when they click here
        course.setEndDate(date);
        courseRepository.save(course);
        System.out.println("test after save End date");
        attachmentContent(course);
        return "redirect:/teacher/listallcourses/";

    }

    private String attachmentContent(Course course) throws UnsupportedEncodingException {

        String head="StudentName,Date,Status";
        Iterable<Person> students=course.getStudent();
        System.out.println(course.getCourseName());
        System.out.println("students in attachment method");

        sendEmailWithoutTemplating(course);

          return head;

    }
    @Autowired
    public EmailService emailService;
    public void sendEmailWithoutTemplating(Course course) throws UnsupportedEncodingException {
        System.out.println("test before email");
        System.out.println(course.getCourseName());
        final Email email= DefaultEmail.builder()
                .from(new InternetAddress("mahifentaye@gmail.com", "Attendance INFO"))
                .to(Lists.newArrayList(new InternetAddress("mymahder@gmail.com","admin")))
                .subject("Testing Email")
                .body("Course Closed.  Attendance for the class has been attached.")
                .attachment(getCsvForecastAttachment("Attendance",course))
                .encoding("UTF-8").build();
//		modelObject.put("recipent", recipent);
        System.out.println("test it");
        emailService.send(email);
    }
    private EmailAttachment getCsvForecastAttachment(String filename,Course course) {



        String testData = "Course CRN: " + course.getCrn() + "," + "Course Name: "+ course.getCourseName() + "\n";

        testData += "Instructor" + course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName() +"\n";

        testData += "\n";

        testData +="Record Number,Student Name,M_Number\n";
        Iterable<Person> students = course.getStudent();
        Person onestu = students.iterator().next();

        testData += " " + "," + " " + "," + " " + ",";

        for (Attendance att : onestu.getAttendances())
        {

           testData += att.getDate().toString() + ",";
        }


        testData += " "+"\n";



        for (Person std : students) {
            String studName= std.getFirstName() +" "+ std.getLastName();
            String studId = String.valueOf(std.getId());
            String mnum = String.valueOf(std.getmNumber());
            Iterable<Attendance> attendances=std.getAttendances();
            testData += studId+","+ studName+","+mnum+",";
            for (Attendance att: attendances) {
                String dates=String.valueOf(att.getDate());
                String status=att.getStatus();
                testData += status+ ",";

            }
            testData += "\n";
        }

         DefaultEmailAttachment attachment = DefaultEmailAttachment.builder()
                .attachmentName(filename + ".csv")
                .attachmentData(testData.getBytes(Charset.forName("UTF-8")))
                .mediaType(MediaType.TEXT_PLAIN).build();

        return attachment;
    }




}
