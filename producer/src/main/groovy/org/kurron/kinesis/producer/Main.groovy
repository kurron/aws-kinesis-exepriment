package org.kurron.kinesis.producer

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.amazonaws.services.kinesis.model.PutRecordRequest
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.kurron.kinesis.Event
import org.kurron.kinesis.LamportTimestamp
import org.kurron.kinesis.Schema
import org.kurron.kinesis.SelfDescribingEvent

import static java.lang.String.format
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
            def lamportTimestampBuilder = LamportTimestamp.newBuilder()
                                                          .setCounter( 1 )
                                                          .setNodeID( 1 )
            def eventBuilder = Event.newBuilder()
                                    .setSubject( 'Bob' )
                                    .setVerb( 'completed' )
                                    .setDirectObject( 'list item 123' )
                                    .setTime( 'ISO 8601 timestamp' )
                                    .setLamportTimestampBuilder( lamportTimestampBuilder )
            def schemaBuilder = Schema.newBuilder()
                                      .setName( 'event.type.subtype')
                                      .setVendor( 'org.kurron' )
                                      .setName( 'Event' )
                                      .setVersion( '2.0.0')

            def selfDescribingEvent = SelfDescribingEvent.newBuilder()
                                                         .setEventBuilder( eventBuilder )
                                                         .setSchema$Builder( schemaBuilder )
                                                         .build()

            def schema = SelfDescribingEvent.classSchema
            def factory = EncoderFactory.get()
            def stream = new ByteArrayOutputStream()
            def binaryEncoder = factory.directBinaryEncoder( stream, null )
            def encoder = factory.validatingEncoder( schema, binaryEncoder )
            def writer = new SpecificDatumWriter<SelfDescribingEvent>( SelfDescribingEvent )
            writer.write( selfDescribingEvent, encoder )
            encoder.flush()
            new PutRecordRequest( streamName: 'example',
                                  data: wrap( stream.toByteArray() ),
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
