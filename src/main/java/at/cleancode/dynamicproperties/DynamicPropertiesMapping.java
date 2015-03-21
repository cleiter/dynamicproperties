package at.cleancode.dynamicproperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicPropertiesMapping {

    private final Map<String, List<ChangeAction>> changeActions = new HashMap<>();
    private final Map<String, List<AfterChangeAction>> afterChangeActions = new HashMap<>();

    public void addChangeAction(String property, ChangeAction changeAction) {
        Assert.argumentNotNull(property, "property");
        Assert.argumentNotNull(changeAction, "changeAction");
        list(changeActions, property).add(changeAction);
    }

    public void addAfterChangeAction(String property, AfterChangeAction afterChangeAction) {
        Assert.argumentNotNull(property, "property");
        Assert.argumentNotNull(afterChangeAction, "afterChangeAction");
        list(afterChangeActions, property).add(afterChangeAction);
    }

    public List<ChangeAction> getChangeActions(String property) {
        Assert.argumentNotNull(property, "property");
        List<ChangeAction> list = changeActions.get(property);
        return list == null ? Collections.<ChangeAction>emptyList() : list;
    }

    public List<AfterChangeAction> getAfterChangeActions(String property) {
        Assert.argumentNotNull(property, "property");
        List<AfterChangeAction> list = afterChangeActions.get(property);
        return list == null ? Collections.<AfterChangeAction>emptyList() : list;
    }

    private <T> List<T> list(Map<String, List<T>> map, String property) {
        List<T> list = map.get(property);
        if (list == null) {
            list = new ArrayList<>();
            map.put(property, list);
        }
        return list;
    }

}
