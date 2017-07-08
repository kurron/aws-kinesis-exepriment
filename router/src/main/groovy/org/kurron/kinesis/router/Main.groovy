package org.kurron.kinesis.router

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesis
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.amazonaws.services.kinesis.model.DescribeStreamRequest
import com.amazonaws.services.kinesis.model.GetRecordsRequest
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest
import com.amazonaws.services.kinesis.model.Shard

/**
 * Application driver.
 */
class Main {

    static void main(String[] args) {
        def client = AmazonKinesisClientBuilder.standard()
                                               .withCredentials( new EnvironmentVariableCredentialsProvider() )
                                               .withRegion( 'us-west-2')
                                               .build()
        def streamName = 'example'
        def shards = fetchShards( client, streamName )

        def shard = shards.first()
        def getShardIteratorRequest = new GetShardIteratorRequest( streamName: streamName,
                                                                   shardId: shard.shardId,
                                                                   shardIteratorType: 'TRIM_HORIZON' )

        def getShardIteratorResult = client.getShardIterator( getShardIteratorRequest )
        def shardIterator = getShardIteratorResult.getShardIterator()

        //noinspection GroovyInfiniteLoopStatement
        while( true ) {
            def request = new GetRecordsRequest( shardIterator: shardIterator, limit: 250 )
            def result = client.getRecords( request )
            def records = result.getRecords()
            println "Just read ${records.size()} records"
            records.each {
                println "Record number is ${it.sequenceNumber}"
            }
            Thread.sleep( 1000 )
            shardIterator = result.getNextShardIterator()
        }
    }

    private static List<Shard> fetchShards(AmazonKinesis client, String streamName ) {
        def request = new DescribeStreamRequest( streamName: streamName )

        List<Shard> shards = new ArrayList<>(32)
        String exclusiveStartShardId = null
        while (true) {
            request.setExclusiveStartShardId(exclusiveStartShardId)
            def result = client.describeStream(request)
            shards.addAll(result.getStreamDescription().getShards())
            exclusiveStartShardId = result.getStreamDescription().getHasMoreShards() && shards.size() > 0 ? shards.get(shards.size() - 1).getShardId() : null
            if (exclusiveStartShardId == null) break
        }
        shards
    }
}
