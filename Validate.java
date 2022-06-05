import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.CollectionUtils;

/*
General approach:
1. Traverse the entire directory once, and store owners and dependency info into a hashtable
2. For each file changed, check owner file for that directory, and check all other directories that have dependency
3. If there's now owner file for particular directory, refer to root owner file
4. Display the result
*/

public class Validate {

    // For this exercise, assume all source files are in java
    public static final String sourceExtension = "java";

    public static Hashtable<String, Helper> fileMapping;

    public static void main(String... args) {

        fileMapping = new Hashtable<String, Helper>();

        // Set directory to current
        File dir = new File (".");

        // Traverse through directory and fill in fileMapping table
        traverseDirectory(dir.listFiles(), fileMapping);

        // Parse arguments
        String[] nameInput = args[0].split(",");
        ArrayList <String> names = new ArrayList<String>(Arrays.asList(nameInput));
        String[] filesInput = args[1].split(",");
        ArrayList <String> paths = new ArrayList<String>(Arrays.asList(filesInput));

        // Check if changed files are approved
        validateApprovals(names, paths, fileMapping);
    }

    public static Hashtable<String, Helper> traverseDirectory(File[] files, Hashtable<String, Helper> input) {
        Hashtable<String, Helper> result = new Hashtable<String, Helper>();

        for (File file: files) {
            // found directory, keep searching for files recursively
            if (file.isDirectory()) {
                traverseDirectory(file.listFiles(), input);
            }
            else {
                // found source file
                if (file.getAbsolutePath().endsWith(sourceExtension)) {
                    result = generateMapping(file.getParentFile(), input);
                }
            }
        }
        return result;
    }

    public static Hashtable<String, Helper> generateMapping(File file, Hashtable<String, Helper> input) {

        ArrayList<String> owners = new ArrayList<String>();
        ArrayList<String> dependencies = new ArrayList<String>();

        Helper helper = new Helper(owners, dependencies);

        File[] files = file.listFiles();

        // Scan and read OWNERS and DEPENDENCIES files
        for (File lookup: files) {
            if (lookup.getAbsolutePath().endsWith("OWNERS")) {
                Scanner scanner = null;
                try {
                    scanner = new Scanner(lookup);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    helper.addOwner(line);
                }
            } else if (lookup.getAbsolutePath().endsWith("DEPENDENCIES")) {
                Scanner scanner = null;
                try {
                    scanner = new Scanner(lookup);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    helper.addDependency(line);
                }
            }
        }

        // Strip out the unnecessary prefix in path and store the values in hashtabl
        input.put(file.getPath().replaceFirst("./", ""), helper);

        return input;
    }

    public static String validateApprovals(ArrayList<String> names, ArrayList<String> files, Hashtable<String, Helper> input) {
        for (String path: files) {
            // Handles invalid file path
            boolean fileExist = new File(".", path).exists();
            if (!fileExist) {
                System.out.println("Invalid file path provided");
                return "Invalid file path provided";
            }

            File target = new File(path);
            Boolean result = compareMappings(names, target, input);
            if (result == false) {
                // If any of the file doesn't have enough approvals, return insufficient
                System.out.println("Insufficient approvals");
                return "Insufficient approvals";
            }
        }

        // If all files are checked and have sufficient approvals, then return approved
        System.out.println("Approved");
        return "Approved";
    }

    public static Boolean compareMappings(ArrayList<String> name, File path, Hashtable<String, Helper> input) {
        String parentDir = path.getParent();
        Helper currentDir = input.get(parentDir);
        ArrayList<String> currentOwners = new ArrayList<String>();
        ArrayList<String> rootOwners = input.get(".").owners;
        AtomicBoolean result = new AtomicBoolean(false);

        // get current dir owners
        currentOwners.addAll(currentDir.owners);

        // if the passed in name is within the OWNER file, set flag to true
        // else set to false and return
        if(CollectionUtils.containsAny(currentOwners, name)) {
            result.set(true);
        } else {
            result.set(false);
            return result.get();
        }

        // search all dependencies
        input.forEach((key, value) -> {
            // if no owner file, set to root dir owner
            if (!CollectionUtils.containsAny(rootOwners, value.owners)) {
                input.get(key).owners.addAll(rootOwners);
            }
            // found a dependency
            if(value.dependencies.contains(parentDir))
            {
                // if the passed in name is not in the dependency OWNER, set flag to false
                if(!CollectionUtils.containsAny(input.get(key).owners, name)) {
                    result.set(false);
                }
            }
        });
        return result.get();
    }
}
