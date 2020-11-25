package com.pxfoost.neo4jdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.batchinsert.BatchInserter;
import org.neo4j.batchinsert.BatchInserters;
import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.neo4j.io.fs.DefaultFileSystemAbstraction;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.layout.DatabaseLayout;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Service {

    private static final String dbPath = "D:\\Temp\\neo4j\\instanceA";
    private DatabaseManagementService databaseManagementService;
    private GraphDatabaseService graphDb;
    final Label personLabel = Label.label( "Person" );
    final RelationshipType knows = RelationshipType.withName("KNOWS");

    /**
     * 不能使用shutdown过后内置实例的DatabaseLayout，shutdown的时候已经将实例的全部文件删除了
     * @throws IOException
     * @throws InterruptedException
     */
    public void test() throws IOException, InterruptedException {
//        startEmbeddedNeo4jServer();
//        databaseManagementService.shutdown();
//        Thread.sleep(3000);

        batchImport();
        startEmbeddedNeo4jServer();
    }

    private void startEmbeddedNeo4jServer() throws IOException, InterruptedException {
        databaseManagementService = new DatabaseManagementServiceBuilder(Paths.get(dbPath).toFile())
                .setConfig(GraphDatabaseSettings.neo4j_home, Paths.get(dbPath))
                .setConfig(BoltConnector.enabled, true)
                .build();
        graphDb = databaseManagementService.database("neo4j");
        registerShutdownHook( databaseManagementService );
        Thread.sleep(3000);
    }

    private Config configuration(){

        return Config.newBuilder()
                .set( GraphDatabaseSettings.neo4j_home, Paths.get(dbPath) )
                .set(BoltConnector.enabled, true)
                .build();
    }

    private void registerShutdownHook( final DatabaseManagementService managementService ){
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                managementService.shutdown();
            }
        } );
    }

    private void batchImport() throws IOException {
        final DatabaseLayout tempStoreDir = DatabaseLayout.of(configuration());
        final FileSystemAbstraction fileSystem = new DefaultFileSystemAbstraction();
        try(final BatchInserter batchInserter = BatchInserters.inserter(tempStoreDir, fileSystem)){
            batchInserter.createDeferredSchemaIndex(personLabel).on("name").create();
            final ArrayList<Long> nodeIds = new ArrayList<>();
            //没有索引，通过Neo4j Desktop打开左侧没有 Node Labels和Relationship Types，有索引的情况下，count为0
            for(int i=0; i<1e5; i++){
                Map<String, Object> properties = new HashMap<>();
                properties.put( "name", UUID.randomUUID().toString() );
                long nodeId = batchInserter.createNode( properties, personLabel );
                nodeIds.add(nodeId);
            }
            final Random random = new Random();
            for(int i=0; i<1e3; i++){
                Long from = nodeIds.get((random.nextInt() % nodeIds.size() + nodeIds.size()) % nodeIds.size());
                Long to = nodeIds.get((random.nextInt() % nodeIds.size() + nodeIds.size()) % nodeIds.size());
                batchInserter.createRelationship( from, to, knows, null );
            }
        }
    }

    private void createIndex() {
        try(final Transaction tx = graphDb.beginTx()){
            tx.execute(" create index on :Person(name)");
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCount(){
        AtomicInteger count = new AtomicInteger();
        try(final Transaction tx = graphDb.beginTx()){
            final ResourceIterable<Node> allNodes = tx.getAllNodes();
            allNodes.forEach( obj -> {
                count.getAndIncrement();
            });
            tx.commit();
        }
        log.debug("count:" + count.get());
    }
}
