package org.kurron.avro.example

import org.apache.avro.file.DataFileReader
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.util.Utf8
import spock.lang.Specification

import java.nio.ByteBuffer

import static org.kurron.avro.example.DatFileWriter.*

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    static final previousVersionDataFileLocation = '../v120.bin'

    def 'exercise codec'() {
        given: 'a data file'
        def dataFile = new File(DATA_FILE_LOCATION)

        when: 'the object is decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(dataFile, userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the encoded and decoded match'
        with( decoded ) {
            FIRST_NAME == firstname as String
            LAST_NAME == lastname as String
            USERNAME == username as String
            ACTIVE == active
            ID == id
            DATE == addedDate
            TIME == addedTime
            GENDER == gender
            COMMENT == comments.first() as String
            // there is a CharSet to String comparison issue with the keys
            SESSION_VALUE == sessions[new Utf8( SESSION_KEY )]

            INT_TO_LONG == promotionExample.intToLong
            STRING_TO_BYTES == promotionExample.stringToBytes as String
            ByteBuffer.wrap( BYTES_TO_STRING ) == promotionExample.bytesToString
        }
    }

    def 'exercise backwards compatibility'() {
        when: 'an object decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(previousVersionDataFileLocation), userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the decoded attributes make sense'
        with( decoded ) {
            'firstname-v120' == firstname as String
            'lastname-v120' == lastname as String
            'username-v120' == username as String
            active
            0 == id
            0 == addedDate
            0 == addedTime
            Gender.UNDECLARED == gender
            [] == comments
            [:] == sessions
            -1 == promotionExample.intToLong
            'defaulted v130 stringToBytes' == promotionExample.stringToBytes as String
            ByteBuffer.wrap( 'defaulted v130 bytesToString'.getBytes( 'UTF-8' ) ) == promotionExample.bytesToString
        }
    }
}
