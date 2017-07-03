package org.kurron.avro.example

import org.apache.avro.file.DataFileReader
import org.apache.avro.specific.SpecificDatumReader
import spock.lang.Specification

import static org.kurron.avro.example.DatFileWriter.*

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    static final previousVersionDataFileLocation = '../v110.bin'

    def 'exercise codec'() {
        given: 'an encoded file'
        def dataFile = new File(DatFileWriter.DATA_FILE_LOCATION)

        when: 'the object is decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(dataFile, userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the encoded and decoded match'
        with( decoded ) {
            FIRST_NAME == firstname as String
            LAST_NAME == lastname as String
            USERNAME == username as String
        }
    }

    def 'exercise backwards compatibility'() {
        when: 'an object decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(previousVersionDataFileLocation), userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the decoded attributes make sense'
        with( decoded ){
            'name-v110' == firstname as String
            'defaulted v120 lastname' == lastname as String
            'username-v110' == username as String

        }
    }
}
