package main.java.cc.xpbootcamp.code_smell_kit.$14_lazy_element;

public class Customer {

    private String name;
    private Address address;

    // lazy_element
    private String getAddress() {
        return address.getAddress();
    }
}
