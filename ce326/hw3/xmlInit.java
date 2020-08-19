
    public void xmlFileInit(File file) {

        try (FileWriter fileWriter = new FileWriter(file);) {
            StringBuilder content = new StringBuilder();

            content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?");

            content.append("<favourites>");
            content.append("</favourites>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }