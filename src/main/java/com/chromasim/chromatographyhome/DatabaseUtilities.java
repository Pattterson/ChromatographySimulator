package com.chromasim.chromatographyhome;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.jena.iri.impl.Test;
import org.bson.Document;

public class DatabaseUtilities {
    static MongoClientURI uri;
    static MongoClient mongoClient;
    static MongoDatabase database;
    static MongoCollection<Document> instrumentMethodCollection;
    static DBObject method;

    static{
        MongoClientURI uri  = new MongoClientURI("mongodb://test123:test123@ds113855.mlab.com:13855/sandboxdb");
        mongoClient = new MongoClient(uri);
        database = mongoClient.getDatabase("sandboxdb");
         instrumentMethodCollection = database.getCollection("instrumentMethods");

//        instrumentMethodCollection.createIndex(new BasicDBObject("createdAt",1),new BasicDBObject("expireAfterSeconds",1L));

    }


    public  static void pushInstrumentMethodToDatabase(InstrumentMethod im){
        Gson gson = new Gson();
        String json = gson.toJson(im);
        System.out.println(json);
        Document methodToAdd = Document.parse(json);
//        ((BasicDBObject) method).append(json);
//        method = ((BasicDBObject) method).append("samplingRate",im.getSamplingRate()).append("Runtime",im.getRunTime())
//                .append("initialTemp",im.getInitialTemp()).append("initialTime",im.getInitialTime())
//                .append("ramp",im.getRamp()).append("maxTemperature", im.getMaxTemp())
//                .append("inletTemp",im.getInletTemp()).append("columnFlow",im.getColumnFlow());
        instrumentMethodCollection.insertOne(methodToAdd);
        System.out.println(instrumentMethodCollection.countDocuments());



    }

//    //        System.out.println(mongoClient.getAddress());
//    DB database = mongoClient.getDB("sandboxdb");
//
//
////        System.out.println(database.getCollection("people").getCount());
//
//    DBCollection collection = database.getCollection("people");
//    DBCollection collection2 = database.getCollection("people2");
//    DBCollection collection3 = database.getCollection("data");
//        collection3.createIndex(new BasicDBObject("createdAt",1),new BasicDBObject("expireAfterSeconds",1L));
//    DBObject injection= new BasicDBObject();
//
////        String line = null;
//
////        FileReader fileReader = new FileReader("src/chromatogramData.csv");
////        BufferedReader bufferedReader = new BufferedReader(fileReader);
////        double[][] data = new double[9902][2];
////        int index = 0;
////        while((line = bufferedReader.readLine())!=null){
////            data[index][0] = Double.parseDouble(line.substring(0,2));
////            data[index][1] = Double.parseDouble(line.substring(7,line.length()-1));
////                    index++;
////        }
//
////        ((BasicDBObject) injection).append("series",data).append("createdAt",new Date());
//        System.out.println("initial: " + collection3.getCount());
////        collection3.insert(injection);
////        while (true){
////            Thread.sleep(1000);
////            System.out.println(collection3.getCount());
////        }
//
//
////        BasicDBList list = (BasicDBList) object.get("series");
////        System.out.println(list.toArray()[0]);
//
////        ArrayList<double[][]> arrayList = (ArrayList) list;
////
////    Person person = new Person("fred",4);
////
////        collection3.remove(new BasicDBObject());
////        collection3.insert(person.toDBObject());
////
////        for(int i=0; i<100; i++){
////        collection3.insert(person.toDBObject());
////        System.out.println(collection3.getCount());
////
////    }
////    //        System.out.println(collection3.getCount());
////    DBCursor cursor = collection3.find();
////    DBObject object = cursor.one();
//////        System.out.println(object.get("name"));
////        collection3.createIndex(new BasicDBObject("createdAt",1),new BasicDBObject("expireAfterSeconds",1L));
////
////        while (true){
////        Thread.sleep(1000);
////        System.out.println(collection3.getCount());
////    }
//
//
////        DBCursor cursor = collection3.find();
////
////        Random rand = new Random();
//////        DBObject person = new BasicDBObject("_id", rand.nextInt())
//////                .append("name", "Jo Bloggs");
////
//////collection2.insert(person);
////DBObject seriesRet =  cursor.next();
//////        System.out.println(seriesRet);
////        System.out.println(seriesRet.get("series"));
////        BasicDBList list = (BasicDBList) seriesRet.get("series");
////        for(Object element: list){
////            System.out.println(element);
////        }
//
//
////collection.remove(person);
////        collection.insert(person);
////        DBObject query = new BasicDBObject("_id", "jo");
////        DBCursor cursor = collection.find(query);
////        DBObject jo = cursor.one();
////        System.out.println((String)cursor.one().get("name"));
//
//
//
//
//
//

}
