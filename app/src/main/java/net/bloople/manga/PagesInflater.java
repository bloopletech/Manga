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

                int lastPeriod = name.lastIndexOf(".");
                String lastBase = name.substring(0, lastPeriod);
                String lastExt = name.substring(lastPeriod);

                paths.add(name);

                for(int i = 0; i < count; i++) {
                    lastBase = StringNextUtil.next(lastBase);
                    paths.add(lastBase + lastExt);
                }
            }
            else {
                paths.add(part);
            }
        }

        return paths;
    }
}
