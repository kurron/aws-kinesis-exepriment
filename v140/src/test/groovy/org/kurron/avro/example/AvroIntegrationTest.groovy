package org.kurron.avro.example

import org.apache.avro.file.DataFileReader
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.util.Utf8
import spock.lang.Specification

import java.nio.ByteBuffer
import java.time.LocalDateTime

import static org.kurron.avro.example.DatFileWriter.*

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    static final previousVersionDataFileLocation = '../v130.bin'

    def 'exercise codec'() {
        given: 'a fresh object'

        when: 'the object is decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(DATA_FILE_LOCATION), userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the encoded and decoded match'
        with( decoded ) {
            FIRST_NAME == firstname as String
            LAST_NAME == lastname as String
            USER_NAME == username as String
            ACTIVE == active
            ID == id
            DATE == addedDate
            TIME == addedTime
            GENDER == gender
            COMMENT == comments.first() as String
            // there is a CharSet to String comparison issue with the keys
            SESSION_VALUE == sessions[new Utf8( SESSION_KEY )]

            promotionExample.intToLong == promotionExample.intToLong
            promotionExample.stringToBytes == promotionExample.stringToBytes
            promotionExample.bytesToString == promotionExample.bytesToString as String
        }
    }

    def 'exercise backwards compatibility'() {
        when: 'an object decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(previousVersionDataFileLocation), userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the decoded attributes make sense'
        Integer.MAX_VALUE as long == decoded.promotionExample.intToLong
        with( decoded ) {
            'firstname-v130' == firstname as String
            'lastname-v130' == lastname as String
            'username-v130' == username as String
            active
            Integer.MAX_VALUE == id
            DATE == addedDate
            68400 == addedTime
            Gender.FEMALE == gender
            'Reset password v130' == comments.first() as String
            130 == sessions[ new Utf8('May') ]

            // Spock got confused if I didn't explicitly use decoded
            Integer.MAX_VALUE as long == decoded.promotionExample.intToLong
            ByteBuffer.wrap( LocalDateTime.now().toLocalDate().toString().getBytes( 'UTF-8' ) ) == decoded.promotionExample.stringToBytes
            LocalDateTime.now().toLocalDate().toString() == decoded.promotionExample.bytesToString as String
        }
    }
}
