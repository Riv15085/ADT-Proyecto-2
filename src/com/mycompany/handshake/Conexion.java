package com.mycompany.handshake;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

 * @author JuanPablo
 */
public class Conexion {
    private static final String MainPath = "C:\\Users\\Usuario\\Documents\\NetBeansProjects\\Recomendaciones\\NetworkEmpleados.graphdb";
    private final String nombreEmpresa;
    private GraphDatabaseService db ;
    public enum NodeType implements Label{
        Persona, Institucion;
    }
    
    public enum RelationType implements RelationshipType{
        CONTRATO_A, RECOMENDADO_POR;
    }
    
        
    public Conexion (){
        nombreEmpresa = "Cementos Progreso";
        
    
    }

    public GraphDatabaseService getDb() {
        return db;
    }

    public void setDb(GraphDatabaseService db) {
        this.db = db;
    }
    
    
    
    
    public void Conectar(){
        if (db == null){

            db = new GraphDatabaseFactory().newEmbeddedDatabase(new File(MainPath));
            
        }
    }
   
    public void IngresarPersona(boolean contratado, String nombre, String edad, String profesion, String localizacion, String salMin,String correo, String nombreRecomendadoPor, String nombreEstudioEn){
        Conectar();
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
                        Node nodoRecomendadoPor = db.findNode(NodeType.Persona, "nombre", nombreRecomendadoPor);
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
        db.shutdown();
    }
    
    public void IngresarInstitucion(String nombre){
        Conectar();
        //GraphDatabaseService db = Conectar();
        try(Transaction tx = db.beginTx()){
            Node nuevoNodo = db.createNode(NodeType.Institucion);
            nuevoNodo.setProperty("nombre", nombre);
            
            tx.success();
        }
        db.shutdown();
    }
    public void Eliminar(String nombre){
        Conectar();
        try(Transaction tx = db.beginTx()){
            
            db.execute("MATCH (n {nombre: '"+nombre+"'}) OPTIONAL MATCH (n)-[r]-() DELETE n,r");
            tx.success();
            tx.close();
        }
        db.shutdown();
        db = null;
    }
    
    public Vector RetrievePersonas(){
        Conectar();
        Vector<String> nombres = new Vector();
        try(Transaction tx = db.beginTx()){
            ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
            while(personas.hasNext()){
                Node persona = personas.next();
                nombres.add((String)persona.getProperty("nombre"));
            }
            tx.success();
        }
        db.shutdown();
        db = null;
        return nombres;
    }
    
    public Vector RetrieveTrabajos(){
        Conectar();
        Vector<String> trabajos = new Vector();
        try(Transaction tx = db.beginTx()){
            ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
            while(personas.hasNext()){
                Node persona = personas.next();
                //for (i)
                trabajos.add((String)persona.getProperty("profesion"));
            }
            tx.success();
        }
        db.shutdown();
        return trabajos;
    }
    
    public Vector RetrieveInstituciones(){
        Conectar();
        Vector<String> nombres = new Vector();
        try(Transaction tx = db.beginTx()){
            ResourceIterator<Node> instituciones = db.findNodes(NodeType.Institucion);
            while(instituciones.hasNext()){
                Node persona = instituciones.next();
                nombres.add((String)persona.getProperty("nombre"));
            }
            tx.success();
        }
        db.shutdown();
        return nombres;
    }
    
    public Vector Recomendar(String profesion){
        Conectar();
        Vector<Node> personasVector = new Vector<Node>();
        try(Transaction tx = db.beginTx()){
            Node empresa = db.findNode(NodeType.Institucion, "nombre", this.nombreEmpresa);
            //Encontrar todas las personas que contrato la empresa
            ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
            Vector<String> nombres = new Vector();
            while(personas.hasNext()){
                Node persona = personas.next();
                //if (persona.getSingleRelationship(RelationType.CONTRATO_A,Direction.INCOMING).getType()!=RelationType.CONTRATO_A){
                if (persona.getSingleRelationship(RelationType.CONTRATO_A, Direction.INCOMING)!=null){
                    if( ((String)persona.getProperty("profesion")).equalsIgnoreCase(profesion)){
                        personasVector.add(persona);   
                    }
                }
            }
            tx.success();
        }
        db.shutdown();
        return personasVector;
    }
    public Vector Recomendar(String profesion, String localizacion){
        Conectar();
        Vector<Node> personasVector = new Vector<Node>();
        try(Transaction tx = db.beginTx()){
            Node empresa = db.findNode(NodeType.Institucion, "nombre", this.nombreEmpresa);
            //Encontrar todas las personas que contrato la empresa
            ResourceIterator<Node> personas = db.findNodes(NodeType.Persona);
            Vector<String> nombres = new Vector();
            while(personas.hasNext()){
                Node persona = personas.next();
                //if (persona.getSingleRelationship(RelationType.CONTRATO_A,Direction.INCOMING).getType()!=RelationType.CONTRATO_A){
                if (persona.getSingleRelationship(RelationType.CONTRATO_A, Direction.INCOMING)!=null){
                    if( ((String)persona.getProperty("profesion")).equalsIgnoreCase(profesion)&&((String)persona.getProperty("localizacion")).equalsIgnoreCase(localizacion)){
                        personasVector.add(persona);   
                    }
                }
            }
            tx.success();
        }
        db.shutdown();
        return personasVector;
    }
}
