package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private StudentService underTest;
//    private AutoCloseable autoCloseable;
    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new StudentService(studentRepository);
    }

//    @AfterEach
//    void tearDown() throws Exception {
//        autoCloseable.close();
//    }

    @Test
    void canGetAllStudents() {

        // when
        underTest.getAllStudents();

        // then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);


    }

    @Test
    void deleteStudent() {

        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }


    @Test
    void willThrowWhenEmailIsTaken(){
        // given
        Student student = new Student(
                1L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );
        given(studentRepository.selectExistsEmail(student.getEmail())).willReturn(true);
        // when
//        underTest.addStudent(student);

        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void willThrowWhenDeleteStudentNotFound (){
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);

        // when
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        // then
        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void canUpdateStudent () {
        //given
        Student student = new Student(
                10L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );
        given(studentRepository.existsById(student.getId()))
                .willReturn(true);
        given(studentRepository.getOne(student.getId())).willReturn(student);
        //when
        underTest.updateStudent(student.getId(), "npham4533@gmail.com");

        verify(studentRepository).save(any());
    }

    @Test
    void willThrowWhenUpdateStudentNotFound () {
        Student student = new Student(
                10L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );
        given(studentRepository.existsById(student.getId()))
                .willReturn(false);

        // when
        assertThatThrownBy(() -> underTest.updateStudent(student.getId(), "npham4533@gmail"))
                .isInstanceOf(StudentNotFoundException.class)
                        .hasMessageContaining("Student with id " + student.getId() + " does not exists");

        // then
        verify(studentRepository, never()).save(any());
    }

}