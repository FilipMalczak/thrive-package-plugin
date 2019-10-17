package com.github.thriveframework.plugin;

import com.github.thriveframework.plugin.model.compose.Root;
import com.github.thriveframework.plugin.model.compose.ServiceDef;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public class YmlDumper {
    public static String toYaml(Root root){
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Map<String, Object> asMap = toMap(root);
        Yaml yaml = new Yaml(options);
        return yaml.dump(asMap);
    }

    private static Map<String, Object> toMap(Root root){
        Map<String, Object> result = new HashMap<>();
        result.put("version", root.getVersion());
        Map<String, Object> services = new HashMap<>();
        for (String serviceName: root.getServices().keySet()) {
            ServiceDef service = root.getServices().get(serviceName);
            Map<String, Object> s = new HashMap<>();
            if (service.getImageDefinition() != null)
                s.put(
                    service.getImageDefinition().getComposeKey(),
                    service.getImageDefinition().getImageSpec()
                );
            s.put("environment", service.getEnvironment());
            s.put("ports", service.getPorts());
            s.put("depends_on", service.getDepends_on());
            s.put("links", service.getLinks());
            s.put("command", service.getCommand());
            removeEmpty(s);
            services.put(serviceName, s);
        }
        result.put("services", services);
        return result;
    }

    private static void removeEmpty(Map<String, Object> map){
        List<String> toRemove = new LinkedList<>();
        for (String key: map.keySet()){
            Object val = map.get(key);
            if (val == null) {
                toRemove.add(key);
                continue;
            }
            if (val instanceof Collection && ((Collection) val).isEmpty()) {
                toRemove.add(key);
                continue;
            }
            if (val instanceof Map && ((Map) val).isEmpty()) {
                toRemove.add(key);
                continue;
            }
            if (val instanceof String && ((String) val).isEmpty()) {
                toRemove.add(key);
                continue;
            }
        }
        for (String key: toRemove)
            map.remove(key);
    }
}
