
public void copyFolder(File sourceFolder, File destinationFolder) {
    if (sourceFolder.isDirectory()) {
        //Verify if destinationFolder is already present; If not then create it
        if (!destinationFolder.exists()) {
            destinationFolder.mkdir();
            System.out.println("Directory created :: " + destinationFolder);
        }
        
        //Get all files from source directory
        String files[] = sourceFolder.list();
        
        //Iterate over all files and copy them to destinationFolder one by one
        for (String file : files) {
            File srcFile = new File(sourceFolder, file);
            File destFile = new File(destinationFolder, file);
            
            //Recursive function call
            copyFolder(srcFile, destFile);
        }
    }
    else{
        //Copy the file content from one place to another 
        Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File copied :: " + destinationFolder);
    }
}