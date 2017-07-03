package org.kurron.avro.example

import org.apache.avro.file.DataFileReader
import org.apache.avro.specific.SpecificDatumReader
import spock.lang.Specification

import static org.kurron.avro.example.DatFileWriter.NAME
import static org.kurron.avro.example.DatFileWriter.USERNAME

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    static final PREVIOUS_VERSION_DATA_FILE_LOCATION = '../v100.bin'

    def 'exercise codec'() {
        given: 'a data file with an object in it'
        def datafile = new File(DatFileWriter.DATA_FILE_LOCATION)

        when: 'the object is decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(datafile, userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the encoded and decoded match'
        with( decoded ) {
            NAME == name as String
            USERNAME == username as String
        }
    }

    def 'exercise backwards compatibility'() {
        when: 'an object decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(new File(PREVIOUS_VERSION_DATA_FILE_LOCATION), userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the decoded attributes make sense'
        with( decoded ) {
            'name-v100' == name as String
            'defaulted v110 username' == username as String
        }
    }
}
