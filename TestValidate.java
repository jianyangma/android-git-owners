import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

public class TestValidate {

    public static Hashtable<String, Helper> dict;
    @Before
    public void setup() {
        dict = new Hashtable<String, Helper>();
        File dir = new File (".");
        dict = Validate.traverseDirectory(dir.listFiles(), dict);
    }

    @Test
    public void testRootOwner() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("alovelace","ghopper"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/follow/Follow.java"
                ,"src/com/twitter/user/User.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Approved", message);
    }

    @Test
    public void testInsufficientDependency() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("alovelace"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/follow/Follow.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Insufficient approvals", message);
    }

    @Test
    public void testInsufficientOwner() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("eclarke"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/follow/Follow.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Insufficient approvals", message);
    }

    @Test
    public void testMultiOwner() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("alovelace","eclarke"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/follow/Follow.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Approved", message);
    }

    @Test
    public void testSingleOwner() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("mfox"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/tweet/Tweet.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Approved", message);
    }

    @Test
    public void testNonexistingOwner() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("allen.ma"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/follow/Follow.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Insufficient approvals", message);
    }

    @Test
    public void testNonexistingFile() {
        String message = "";

        ArrayList<String> names = new ArrayList<String>(Arrays.asList("ghopper"));
        ArrayList<String> paths = new ArrayList<String>(Arrays.asList("src/com/twitter/follow/HelloWorld.java"));

        System.out.println("Testing with owners: " + names + " and files changed: " + paths);

        message = Validate.validateApprovals(names, paths, dict);

        assertEquals("Invalid file path provided", message);
    }
}
