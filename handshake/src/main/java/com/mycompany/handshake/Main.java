/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.handshake;
import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
/**
 *
 * @author JuanPablo
 */
public class Main {
    private static final String MainPath = "C:\\Users\\JuanPablo\\Documents\\Neo4j\\HDT10.graphdb";
    /**
     * @param args the command line arguments
     */
    public enum NodeType implements Label{
        Programador;
    }
    
    public static void main(String[] args) {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService graphDb = dbFactory.newEmbeddedDatabase(new File(MainPath));
        
        try(Transaction tx = graphDb.beginTx()){
            Node jpNodo = graphDb.createNode(NodeType.Programador);
            jpNodo.setProperty("nombre", "Juan Pablo");
            jpNodo.setProperty("carrera", "Mecatronica");
            
            //graphDb.execute("MATCH (n:Programador {nombre: 'Juan Pablo'}) DELETE n");
            
            tx.success();
        }
    }
    
}
