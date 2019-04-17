package net.bloople.manga;

import java.util.HashMap;

class Library {
    private String root;
    private HashMap<Long, Book> books = new HashMap<>();
    private String name;

    Library(String root, String name) {
        this.root = root;
        this.name = name;
    }

    String root() {
        return root;
    }

    String mangos() {
        return root + "/.mangos";
    }

    HashMap<Long, Book> books() {
        return books;
    }

    String name() {
        return name;
    }
}
