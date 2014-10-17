package net.daum.younin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NameService {
    private List names;
    
    /** Creates a new instance of NameService */
    private NameService(List list_of_names) {
        this.names = list_of_names;
    }
    
    public static NameService getInstance(List list_of_names) {
        return new NameService(list_of_names);
    }
    
    public List findNames(String prefix) {
        String prefix_upper = prefix.toUpperCase();
        List matches = new ArrayList();
        Iterator iter = names.iterator();
        while(iter.hasNext()) {
            String name = (String) iter.next();
            String name_upper_case = name.toUpperCase();
            if(name_upper_case.startsWith(prefix_upper)){        
                boolean result = matches.add(name);
            }
        }
        return matches;
    }
    
}
