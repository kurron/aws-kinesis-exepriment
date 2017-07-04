package org.kurron.kinesis.producer

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.PutRecordsRequest
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry
import com.amazonaws.services.kinesis.model.PutRecordsResult

import java.nio.ByteBuffer

/**
 * Application driver.
 */
class Main {

    static void main(String[] args) {
        def credentialsProvider = new EnvironmentVariableCredentialsProvider()
        def amazonKinesisClient = new AmazonKinesisClient(credentialsProvider)
        def putRecordsRequest  = new PutRecordsRequest()
        putRecordsRequest.setStreamName('example')
        List <PutRecordsRequestEntry> putRecordsRequestEntryList  = new ArrayList<>()
        for (int i = 0; i < 100; i++) {
            def putRecordsRequestEntry  = new PutRecordsRequestEntry()
            putRecordsRequestEntry.setData(ByteBuffer.wrap(String.valueOf(i).getBytes()))
            putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%d", i))
            putRecordsRequestEntryList.add(putRecordsRequestEntry)
        }

        putRecordsRequest.setRecords(putRecordsRequestEntryList)
        PutRecordsResult putRecordsResult  = amazonKinesisClient.putRecords(putRecordsRequest)
        System.out.println("Put Result" + putRecordsResult)
    }
}
