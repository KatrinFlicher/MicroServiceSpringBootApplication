package by.training.zaretskaya.beans;

import by.training.zaretskaya.annotations.ConvertibleToMap;

@ConvertibleToMap
public class Pet {
    private int height;
    private int weight;

    public Pet(int height, int weight) {
        this.height = height;
        this.weight = weight;
    }

    public Pet() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pet pet = (Pet) o;

        if (height != pet.height) return false;
        return weight == pet.weight;
    }

    @Override
    public int hashCode() {
        int result = height;
        result = 31 * result + weight;
        return result;
    }
}
