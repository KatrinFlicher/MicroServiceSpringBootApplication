package by.training.zaretskaya.beans;

import by.training.zaretskaya.annotations.ConvertibleToMap;

@ConvertibleToMap
public class A {
    B b;

    public void setB(B b) {
        this.b = b;
    }
}
