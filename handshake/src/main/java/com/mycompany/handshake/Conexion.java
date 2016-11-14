/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.handshake;
import java.io.File;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
/**
 *
 * @author JuanPablo
 */
public class Conexion {
    private static final String MainPath = "C:\\Users\\JuanPablo\\Documents\\Neo4j\\HDT10.graphdb";
    private final String nombreEmpresa = "Cementos Progreso";
      
    public GraphDatabaseService Conectar(){
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService graphDb = dbFactory.newEmbeddedDatabase(new File(MainPath));
        return graphDb;
    }
    
    public enum NodeType implements Label{
        Persona, Institucion;
    }
    
    public enum RelationType implements RelationshipType{
        CONTRATO_A, RECOMENDADO_POR;
    }
    
    public void IngresarPersona(boolean contratado, String nombre, int edad, String profesion, String localizacion, int salMin,String correo, String nombreRecomendadoPor, String nombreEstudioEn){
        GraphDatabaseService db = Conectar();
        try(Transaction tx = db.beginTx()){
            Node nuevoNodo = db.createNode(NodeType.Persona);
            nuevoNodo.setProperty("nombre", nombre); 
            nuevoNodo.setProperty("edad", edad);
            nuevoNodo.setProperty("profesion", profesion);
            nuevoNodo.setProperty("localizacion", localizacion);
            nuevoNodo.setProperty("salMin", salMin);
            nuevoNodo.setProperty("estudioEn", nombreEstudioEn);
            nuevoNodo.setProperty("correo", correo);
     
     /*1*/      if (nombreRecomendadoPor.equalsIgnoreCase("")==false){
                    try{
                        Node nodoRecomendadoPor = db.findNode(NodeType.Institucion, "nombre", nombreRecomendadoPor);
                       nuevoNodo.createRelationshipTo(nodoRecomendadoPor, RelationType.RECOMENDADO_POR);
                    }
                    catch(Exception e){
                        JOptionPane.showMessageDialog(null,"Relacion no efectuada: Recomendado Por "+nombreRecomendadoPor);
                    }
                }
     /*2*/      if (contratado){
                    try{
                         Node empresa = db.findNode(NodeType.Institucion, "nombre", nombreEmpresa);
                        empresa.createRelationshipTo(nuevoNodo, RelationType.CONTRATO_A);
                    }
                    catch(Exception e){
                        JOptionPane.showMessageDialog(null,"Relacion no efectuada: Contratado");
                    }
                }   
            tx.success();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void IngresarInstitucion(String nombre){
        GraphDatabaseService db = Conectar();
        try(Transaction tx = db.beginTx()){
            Node nuevoNodo = db.createNode(NodeType.Institucion);
            nuevoNodo.setProperty("nombre", nombre);
            
            tx.success();
        }
    }
    public void Eliminar(String nombre){
        GraphDatabaseService db = Conectar();
        try(Transaction tx = db.beginTx()){
            db.execute("MATCH (n {nombre: '"+nombre+"'}) DELETE n");
            tx.success();
        }
        
    }
    
    public Vector RetrievePersonas(){
        GraphDatabaseService db = Conectar();
        ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
        Vector<String> nombres = new Vector();
        while(personas.hasNext()){
            Node persona = personas.next();
            nombres.add((String)persona.getProperty("nombre"));
        }
        return nombres;
    }
    
    public Vector RetrieveInstituciones(){
        GraphDatabaseService db = Conectar();
        ResourceIterator<Node> instituciones = db.findNodes(NodeType.Institucion);
        Vector<String> nombres = new Vector();
        while(instituciones.hasNext()){
            Node persona = instituciones.next();
            nombres.add((String)persona.getProperty("nombre"));
        }
        return nombres;
    }
    
    public Vector Recomendar(String profesion){
        GraphDatabaseService db = Conectar();
        Node empresa = db.findNode(NodeType.Institucion, "nombre", this.nombreEmpresa);
        //Encontrar todas las personas que contrato la empresa
        Vector<Node> personasVector = new Vector<Node>();
        ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
        Vector<String> nombres = new Vector();
        while(personas.hasNext()){
            Node persona = personas.next();
            if (persona.getSingleRelationship(RelationType.CONTRATO_A,Direction.INCOMING).getType()!=RelationType.CONTRATO_A){
                if( ((String)persona.getProperty("profesion")).equalsIgnoreCase(profesion)){
                    personasVector.add(persona);   
                }
            }
        }
        return personasVector;
    }
    public Vector Recomendar(String profesion, String localizacion){
        GraphDatabaseService db = Conectar();
        Node empresa = db.findNode(NodeType.Institucion, "nombre", this.nombreEmpresa);
        //Encontrar todas las personas que contrato la empresa
        Vector<Node> personasVector = new Vector<Node>();
        ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
        Vector<String> nombres = new Vector();
        while(personas.hasNext()){
            Node persona = personas.next();
            if (persona.getSingleRelationship(RelationType.CONTRATO_A,Direction.INCOMING).getType()!=RelationType.CONTRATO_A){
                if( ((String)persona.getProperty("profesion")).equalsIgnoreCase(profesion)&&((String)persona.getProperty("localizacion")).equalsIgnoreCase(localizacion)){
                    personasVector.add(persona);   
                }
            }
        }
        return personasVector;
    }
}
