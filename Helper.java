import java.util.ArrayList;
/*
Helper class to store list of owners and dependencies per directory
 */

public class Helper {
    public ArrayList<String> owners;
    public ArrayList<String> dependencies;

    public Helper(ArrayList<String> owners, ArrayList<String> dependencies) {
        this.owners = owners;
        this.dependencies = dependencies;
    }

    public ArrayList<String> getOwners() { return owners; }
    public ArrayList<String> getDependencies() { return dependencies; }

    public void addOwner(String owner) {
        owners.add(owner);
    }
    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }
}
