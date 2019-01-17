package by.training.zaretskaya.beans;

public class DummyPerson {
    private int age;
    private DummyMother mother;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public DummyMother getMother() {
        return mother;
    }

    public void setMother(DummyMother mother) {
        this.mother = mother;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyMother that = (DummyMother) o;
        if (getAge() != that.getAge()) return false;
        return getMother() != null ? getMother().equals(that.getMother()) : that.getMother() == null;
    }

    @Override
    public int hashCode() {
        int result = getAge();
        result = 31 * result + (getMother() != null ? getMother().hashCode() : 0);
        return result;
    }
}
