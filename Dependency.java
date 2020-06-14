import java.util.*;

public class Dependency {

    private Set<String> installed_components;
    private Map<String, List<String>> dependency_list;
    private Map<String, List<String>> dependent_list;

    public void install_recursively(String item){
        if(!dependency_list.containsKey(item)){
            System.out.println("Installing " + item);
            installed_components.add(item);
            return;
        }
        List<String> dependants = dependency_list.get(item);
        for(String s: dependants){
            install_recursively(s);
        }
    }

    public void execute(String command, String[] tokens,String input){
        if("LIST".equals(command)){
            for(String s:installed_components){
                System.out.println(s);
            }
            return;
        }
        if("DEPEND".equals(command)){
            System.out.println(command+" "+ input);
            String item = tokens[1];
            if(dependency_list.containsKey(item)){
                for(int i=2; i<tokens.length; i++) {
                    dependency_list.get(item).add(tokens[i]);
                }
            } else {
                List<String> item_req = new ArrayList<>();
                for(int i=2; i<tokens.length; i++) {
                    item_req.add(tokens[i]);
                }
                dependency_list.put(item, item_req);
            }
            // populate dependent list
            if(dependent_list!=null) {
                for (int i = 2; i < tokens.length; i++) {
                    if (dependent_list.containsKey(tokens[i])) {
                        dependent_list.get(tokens[i]).add(item);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(item);
                        dependent_list.put(tokens[i], list);
                    }
                }
            }
            return;
        }
        if("INSTALL".equals(command)){
            String item = tokens[1];
            if(installed_components.contains(item)){
                System.out.println(item + " is already installed.");
                return;
            }
            install_recursively(item);
            return;
        }
        if("REMOVE".equals(command)){
            String item = tokens[1];
            if(installed_components!=null && !installed_components.contains(item)){
                System.out.println(item + " is not installed.");
                return;
            }
            if(dependent_list!=null && !dependent_list.containsKey(item)){
                System.out.println("Removing " + item);
                installed_components.remove(item);
                return;
            }
            boolean isRequired = false;
            if(dependent_list!=null) {
                for (String s : dependent_list.get(item)) {
                    if (installed_components.contains(s)) {
                        isRequired = true;
                    }
                }
            }
            if(isRequired){
                System.out.println(item + " is still needed.");
            }
        }
    }

    public static void main(String[] args){
        Dependency dependency = new Dependency();
        dependency.installed_components = new HashSet<>();
        dependency.dependency_list = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!"END".equalsIgnoreCase(input)) {
            String[] tokens = input.split(" ");
            dependency.execute(tokens[0], tokens,input);
            input = scanner.nextLine();
        }
    }
}
