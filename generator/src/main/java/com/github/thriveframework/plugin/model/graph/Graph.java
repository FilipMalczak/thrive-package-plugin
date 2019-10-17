package com.github.thriveframework.plugin.model.graph;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor
@EqualsAndHashCode
public class Graph {
    Map<String, Node> nodes = new HashMap<>();

    public Stream<String> getNodeNames(){
        return nodes.keySet().stream();
    }

    public Stream<Node> getNodes(){
        return nodes.values().stream();
    }

    public void connect(String from, String to){
        node(from).getOutcoming().add(node(to));
        node(to).getIncoming().add(node(from));
    }

    /**
     * This is not a getter - it either creates the node (and remembers it) or fetches an existing
     * one.
     * @param name nodes name
     * @return a node for that name(never null)
     */
    public Node node(String name){
        if (!nodes.containsKey(name))
            nodes.put(name, new Node(name));
        return nodes.get(name);
    }

    public Stream<Node> find(Predicate<Node> predicate){
        return getNodes().filter(predicate);
    }

    public Stream<Node> ofDegree(int degree){
        //todo assert degree>=0
        return find(n -> n.getDegree() == degree);
    }

    public void remove(String name){
        removeInternal(node(name));
    }

    public void remove(Node node){
        remove(node.getName());
    }

    /**
     * "Detaches" nodes - they are removed from graph, but they still hold the information
     * on their old connections.
     */
    public List<Node> pop(Predicate<Node> predicate){
        List<Node> out = find(predicate).peek(x -> System.out.println("Peek "+x)).map(n -> n.copy()).collect(toList());
        out.forEach(this::remove);
        return out;
    }

    //node is guaranteed to be in nodes
    private void removeInternal(Node node){
        nodes.remove(node.getName());
        for (Node source: node.getIncoming()){
            source.disconnectOutcoming(node.getName());
        }
        for (Node target: node.getOutcoming()){
            target.disconnectIncoming(node.getName());
        }
    }

    public int size(){
        return nodes.size();
    }

    public boolean isEmpty(){
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return "Graph{" +
            "nodes=" + nodes +
            '}';
    }
}

