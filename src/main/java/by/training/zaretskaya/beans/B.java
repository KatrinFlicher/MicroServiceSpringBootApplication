package by.training.zaretskaya.beans;

import by.training.zaretskaya.annotations.ConvertibleToMap;

@ConvertibleToMap
public class B {
    C c;

    public void setC(C c) {
        this.c = c;
    }
}
