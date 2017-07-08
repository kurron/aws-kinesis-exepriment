package org.kurron.kinesis.producer

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.amazonaws.services.kinesis.model.PutRecordsRequest
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry

import java.nio.ByteBuffer

/**
 * Application driver.
 */
class Main {

    static void main(String[] args) {
        def client = AmazonKinesisClientBuilder.standard()
                                               .withCredentials( new EnvironmentVariableCredentialsProvider() )
                                               .withRegion( 'us-west-2')
                                               .build()
        def records = (1..100).collect {
            new PutRecordsRequestEntry( data: ByteBuffer.wrap(String.valueOf(it).getBytes()),
                    partitionKey: String.format("partitionKey-%d", it) )
        }
        def request = new PutRecordsRequest( streamName: 'example', records: records )
        def result = client.putRecords( request )
        System.out.println("Put Result" + result)
    }
}
