package org.kurron.kinesis.producer

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.amazonaws.services.kinesis.model.PutRecordRequest

import static java.lang.String.format
import static java.lang.String.valueOf
import static java.nio.ByteBuffer.wrap

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
            new PutRecordRequest( streamName: 'example',
                                  data: wrap( valueOf( it ).bytes ),
                                  partitionKey: format( 'partitionKey-%d', it ) )
        }

        def finalSequenceNumber = records.inject( '0' ) { String sequenceNumber, request ->
            request.setSequenceNumberForOrdering( sequenceNumber )
            def result = client.putRecord( request )
            result.sequenceNumber
        }
        System.out.println("Just pushed ${records.size()} records")
        System.out.println( "Final sequence number is ${finalSequenceNumber}")
    }
}
