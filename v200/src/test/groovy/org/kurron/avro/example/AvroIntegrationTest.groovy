package org.kurron.avro.example

import org.apache.avro.AvroTypeException
import org.apache.avro.file.DataFileReader
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.util.Utf8
import spock.lang.Specification

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    static final previousVersionDataFileLocation = '../v140.bin'

    def 'exercise codec'() {
        when: 'the object is decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(DatFileWriter.dataFileLocation), userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the encoded and decoded match'
        DatFileWriter.FIRST_NAME == decoded.firstname as String
        DatFileWriter.LAST_NAME == decoded.lastname as String
        DatFileWriter.USER_NAME == decoded.username as String
        DatFileWriter.ACTIVE == decoded.active
        DatFileWriter.ID == decoded.id
        DatFileWriter.date == decoded.addedDate
        DatFileWriter.time == decoded.addedTime
        DatFileWriter.GENDER == decoded.gender
        'Reset password v200' == decoded.comments.first() as String
        // there is a CharSet to String comparison issue with the keys
        200 == decoded.sessions[ new Utf8('May') ]
    }

    def 'exercise forwards compatibility'() {
        when: 'an object decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(previousVersionDataFileLocation), userDatumReader)
        dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'Avro complains about a missing required field'
        def error = thrown( AvroTypeException )
        error.message.contains( 'missing required field breakingChange' )
    }
}
