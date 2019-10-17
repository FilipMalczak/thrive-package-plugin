package com.github.thriveframework.plugin.model.graph;

import com.github.thriveframework.plugin.model.Port;
import lombok.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

//fixme theres a leaky abstraction here; its a generic Node, but holds domain-specific "exposedPorts"
@Value
@ToString(onlyExplicitlyIncluded = true)
public class Node {
    @ToString.Include
    @NonNull String name;
    @ToString.Include
    Collection<Integer> exposedPorts = new HashSet<>();
    Collection<Node> incoming = new HashSet<>();
    @ToString.Include
    Collection<Node> outcoming = new HashSet<>();

    @ToString.Include
    public List<String> getIncomingNames(){
        return incoming.stream().map(Node::getName).collect(toList());
    }

    public int getDegree(){
        return outcoming.size();
    }

    public Node copy(){
        Node out = new Node(name);
        out.exposedPorts.addAll(exposedPorts);
        out.getIncoming().addAll(incoming);
        out.getOutcoming().addAll(outcoming);
        return out;
    }

    public void disconnectIncoming(String name){
        //todo specialized exception
        Node n = incoming.stream().filter(x -> x.getName().equals(name)).findFirst().get();
        incoming.remove(n);
    }

    public void disconnectOutcoming(String name){
        //todo specialized exception
        Node n = outcoming.stream().filter(x -> x.getName().equals(name)).findFirst().get();
        outcoming.remove(n);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return name.equals(node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
