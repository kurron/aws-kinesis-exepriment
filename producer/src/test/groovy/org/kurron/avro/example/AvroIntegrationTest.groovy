package org.kurron.avro.example

import org.apache.avro.file.DataFileReader
import org.apache.avro.specific.SpecificDatumReader
import spock.lang.Specification

import static org.kurron.avro.example.DatFileWriter.NAME

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    static final dataFileLocation = '../v100.bin'

    def 'exercise codec'() {
        given: 'a data file with an object in it'
        def dataFile = new File(dataFileLocation)

        when: 'the object is decoded from disk'
        def userDatumReader = new SpecificDatumReader<User>(User)
        def dataFileReader = new DataFileReader<User>(dataFile, userDatumReader)
        def decoded = dataFileReader.hasNext() ? dataFileReader.next( new User() ) : new User()

        then: 'the encoded and decoded match'
        NAME == decoded.name as String
    }
}
