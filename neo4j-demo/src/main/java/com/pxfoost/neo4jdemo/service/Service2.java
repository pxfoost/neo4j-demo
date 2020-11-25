//package com.pxfoost.neo4jdemo.service;
//
//import org.neo4j.graphdb.DynamicLabel;
//import org.neo4j.graphdb.Label;
//import org.neo4j.graphdb.RelationshipType;
//import org.neo4j.helpers.collection.MapUtil;
//import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
//import org.neo4j.unsafe.batchinsert.BatchInserter;
//import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
//import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
//import org.neo4j.unsafe.batchinsert.BatchInserters;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.*;
//
//public class Service2 {
//
//    private static final String dbPath = "E:\\tmp\\test\\instance1";
//    final Label personLabel = Label.label( "Person" );
//    final RelationshipType knows = RelationshipType.withName("KNOWS");
//
//    public void batchImport(){
//        BatchInserter inserter = null;
//        BatchInserterIndex index = null;
//        final ArrayList<Long> nodeIds = new ArrayList<>();
//        final Random random = new Random();
//        try {
//            inserter = BatchInserters.inserter(Paths.get(dbPath).toFile() );
//            BatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider( inserter );
//            index = indexProvider.nodeIndex("myindex", MapUtil.stringMap( "type", "exact" ) );
//
//            for(int i=0; i<1e5; i++){
//                Map<String, Object> properties = new HashMap<>();
//                properties.put("name", UUID.randomUUID().toString());
//                long newNode = inserter.createNode(properties, personLabel);
//                index.add(newNode, properties);
//                nodeIds.add(newNode);
//            }
//            index.flush();
//            for(int i=0; i<1e3;i++){
//                Long from = nodeIds.get((random.nextInt() % nodeIds.size() + nodeIds.size()) % nodeIds.size());
//                Long to = nodeIds.get((random.nextInt() % nodeIds.size() + nodeIds.size()) % nodeIds.size());
//                final long relId = inserter.createRelationship(from, to, knows, null);
////                index.add(relId, null);
//            }
////            index.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(inserter != null){
//                inserter.shutdown();
//            }
//        }
//
//
//    }
//}
