package net.bloople.manga;

import java.util.ArrayList;

class PagesInflater {
    private String pagesString;

    PagesInflater(String inPagesString) {
        pagesString = inPagesString;
    }

    ArrayList<String> inflate() {
        ArrayList<String> paths = new ArrayList<>();

        String[] parts = pagesString.split("\\|");
        for(String part : parts) {
            if(part.contains("/")) {
                String[] partParts = part.split("/");
                String name = partParts[0];
                int count = Integer.valueOf(partParts[1]);

                String[] nameParts = name.split("\\.");
                String lastBase = nameParts[0];
                String lastExt = nameParts[1];

                paths.add(name);

                for(int i = 0; i < count; i++) {
                    lastBase = StringNextUtil.next(lastBase);
                    paths.add(lastBase + "." + lastExt);
                }
            }
            else {
                paths.add(part);
            }
        }

        return paths;
    }
}
