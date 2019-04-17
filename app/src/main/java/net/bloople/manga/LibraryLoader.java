package net.bloople.manga;

import com.dslplatform.json.DslJson;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

class LibraryLoader {
    private static final String DATA_JSON_PATH = "/data.json";
    private Library library;

    LibraryLoader(Library library) {
        this.library = library;
    }

    Library library() {
        return library;
    }

    void load() throws IOException {
        URLConnection connection = new URL(library.mangos() + DATA_JSON_PATH).openConnection();

        DslJson<Object> dslJson = new DslJson<>();

        List<Book> books = dslJson.deserializeList(Book.class, connection.getInputStream());
        for(Book book : books) book.inflate(library);
    }
}
