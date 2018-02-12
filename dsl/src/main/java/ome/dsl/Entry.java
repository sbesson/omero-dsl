package ome.dsl;

import ome.dsl.velocity.JavaGenerator;

public class Entry {
    public static void main(String args[]) {
        JavaGenerator gen = new JavaGenerator();
        gen.run();
    }
}
