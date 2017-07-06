package org.kurron.kinesis.router

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.DescribeStreamRequest
import com.amazonaws.services.kinesis.model.GetRecordsRequest
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest
import com.amazonaws.services.kinesis.model.Shard

/**
 * Application driver.
 */
class Main {

    static void main(String[] args) {
        def client = new AmazonKinesisClient( new EnvironmentVariableCredentialsProvider() )

        final def streamName = 'example'
        def shards = fetchShards( client, streamName )

        def shard = shards.first()
        def getShardIteratorRequest = new GetShardIteratorRequest( streamName: streamName,
                shardId: shard.shardId,
                shardIteratorType: 'TRIM_HORIZON' )

        def getShardIteratorResult = client.getShardIterator( getShardIteratorRequest )
        def shardIterator = getShardIteratorResult.getShardIterator()

        while( true ) {
            println "Shard iterator: ${shardIterator}"
            def request = new GetRecordsRequest( shardIterator: shardIterator, limit: 25 )
            def result = client.getRecords( request )
            def records = result.getRecords()
            println "Just read ${records.size()} records"
            Thread.sleep( 1000 )
            shardIterator = result.getNextShardIterator()
        }
    }

    private static ArrayList<Shard> fetchShards( AmazonKinesisClient client, String streamName ) {
        def describeStreamRequest = new DescribeStreamRequest( streamName: streamName )

        List<Shard> shards = new ArrayList<>(32)
        String exclusiveStartShardId = null
        while (true) {
            describeStreamRequest.setExclusiveStartShardId(exclusiveStartShardId)
            def describeStreamResult = client.describeStream(describeStreamRequest)
            shards.addAll(describeStreamResult.getStreamDescription().getShards())
            exclusiveStartShardId = describeStreamResult.getStreamDescription().getHasMoreShards() && shards.size() > 0 ? shards.get(shards.size() - 1).getShardId() : null
            if (exclusiveStartShardId == null) break
        }
        shards
    }
}
