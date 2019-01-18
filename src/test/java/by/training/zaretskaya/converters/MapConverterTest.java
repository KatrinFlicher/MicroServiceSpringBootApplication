package by.training.zaretskaya.converters;

import by.training.zaretskaya.beans.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapConverterTest {

    @Test
    public void testCheckWorkingMapConverter() throws Exception {
        Person person = new Person();
        person.setAge(22);
        person.setPet(new Pet(30, 3));
        assertEquals(person, MapConverter.fromMap(MapConverter.toMap(person)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInfiniteRecursionIfFieldIsInheritorOfClassOfObject() throws IllegalAccessException {
        DummyPerson dummyPerson = new DummyPerson();
        dummyPerson.setAge(10);
        dummyPerson.setMother(new DummyMother());
        MapConverter.toMap(dummyPerson);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInfiniteRecursionIfСompositionIsWrong() throws IllegalAccessException {
        A a = new A();
        B b = new B();
        C c = new C();
        a.setB(b);
        b.setC(c);
        c.setA(a);
        MapConverter.toMap(a);
    }
}